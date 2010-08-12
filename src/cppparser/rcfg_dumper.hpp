#ifndef RCFG_DUMPER_HPP
#define RFG_DUMPER_HPP

#include <clang/AST/Stmt.h>
#include <clang/Analysis/CFG.h>

#include <cstdlib>
#include <iostream>
#include <vector>
#include <map>

struct rcfg_node
{
	enum operand_type { ot_none, ot_function, ot_member, ot_varptr, ot_varval, ot_vartgt, ot_nodeval, ot_nodetgt };

	struct operand
	{
		operand_type type;
		std::size_t id;

		operand()
			: type(ot_none), id(0)
		{
		}

		operand(operand_type type, std::size_t id)
			: type(type), id(id)
		{
		}
	};

	struct succ
	{
		std::size_t id;
		clang::Stmt const * label;

		succ(std::size_t id, clang::Stmt const * label = 0)
			: id(id), label(label)
		{
		}
	};

	clang::Stmt const * stmt;
	std::vector<operand> operands;
	std::vector<succ> succs;
	enum break_type_t { bt_none, bt_break, bt_continue, bt_return, bt_goto } break_type;

	rcfg_node(clang::Stmt const * stmt = 0)
		: stmt(stmt), break_type(bt_none)
	{
	}

	rcfg_node & operator()(operand_type type, std::size_t id)
	{
		this->operands.push_back(operand(type, id));
		return *this;
	}

	rcfg_node & operator()(operand const & op)
	{
		this->operands.push_back(op);
		return *this;
	}

	rcfg_node & add_succ(std::size_t target, clang::Stmt const * label = 0)
	{
		this->succs.push_back(succ(target, label));
		return *this;
	}
};

struct rcfg_id_list
{
public:
	explicit rcfg_id_list(clang::FunctionDecl const & fn, clang::ASTContext & ctx);

	std::size_t operator()(clang::NamedDecl const * decl);
	std::size_t operator()(std::string const & str);

	std::map<clang::NamedDecl const *, std::string> const & decl_names() const { return m_decl_names; }
	std::map<std::string, std::size_t> const & name_ids() const { return m_name_ids; }

	std::size_t make_temporary(clang::Type const * type);

	std::string name(std::size_t i) const { return m_names[i]; }
	clang::FunctionDecl const & fn() const { return m_fn; }
	clang::ASTContext & ctx() const { return m_ctx; }

private:
	clang::FunctionDecl const & m_fn;
	clang::ASTContext & m_ctx;
	std::vector<clang::Type const *> m_temporaries;

	std::map<clang::NamedDecl const *, std::string> m_decl_names;
	std::map<std::string, std::size_t> m_name_ids;
	std::vector<std::string> m_names;
};

class rcfg
{
public:
	explicit rcfg(clang::FunctionDecl const & fn);

	void xml_print(std::ostream & out, clang::SourceManager const * sm) const;
	void pretty_print(std::ostream & out, clang::SourceManager const * sm) const;

private:
	struct builder
	{
		builder(rcfg_id_list & id_list, clang::Stmt const * stmt = 0);

		rcfg_node::operand add_node(rcfg_node const & node);

		void build(clang::Stmt const * stmt);
		rcfg_node::operand build_expr(clang::Expr const * expr, rcfg_node::operand const & target = rcfg_node::operand());
		rcfg_node::operand access_var(clang::ValueDecl const * decl);
		rcfg_node::operand deref_var(rcfg_node::operand var);

		rcfg_node::operand make_address(rcfg_node::operand var);
		rcfg_node::operand make_deref(rcfg_node::operand var);
		rcfg_node::operand make_rvalue(rcfg_node::operand var);
		std::size_t make_node(rcfg_node::operand const & var);
		rcfg_node::operand make_param(rcfg_node::operand const & op, clang::Type const * type);

		void fix_function();
		void fix(rcfg_node::break_type_t bt, std::size_t target);

		void append(clang::Stmt const * stmt);
		void append(builder const & nested);
		void append_edge(clang::Stmt const * stmt, std::size_t source_node, std::size_t end_node, clang::Stmt const * label = 0);
		void append_edge(builder const & nested, std::size_t source_node, std::size_t end_node, clang::Stmt const * label = 0);

		void merge_labels(builder const & nested, std::size_t shift);

		std::vector<rcfg_node> m_nodes;
		std::map<clang::LabelStmt const *, std::size_t> m_labels;
		std::vector<std::pair<std::size_t, clang::CaseStmt const *> > m_switch_cases;
		std::pair<std::size_t, clang::DefaultStmt const *> m_default_case;

		builder & operator=(builder const & other)
		{
			m_nodes = other.m_nodes;
			m_labels = other.m_labels;
			m_switch_cases = other.m_switch_cases;
			m_default_case = other.m_default_case;
			return *this;
		}

		rcfg_id_list & m_id_list;
	};

	rcfg_id_list m_id_list;
	clang::FunctionDecl const & m_fn;
	std::vector<rcfg_node> m_nodes;
};

template <typename InputIterator>
void print_rcfg(clang::ASTContext & ctx, std::ostream & fout, clang::SourceManager const * sm, InputIterator firstFun, InputIterator lastFun)
{
	fout <<
		"<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
		"<cfgs>";

	for (; firstFun != lastFun; ++firstFun)
	{
		{
			clang::CFG * cfg = clang::CFG::buildCFG(*firstFun, (*firstFun)->getBody(), &ctx);
			//cfg->dump(clang::LangOptions());
			delete cfg;
		}

		rcfg c(**firstFun);
		c.xml_print(fout, sm);
	}

	fout << "</cfgs>\n";
}

template <typename InputIterator>
void print_debug_rcfg(clang::ASTContext & ctx, std::ostream & fout, clang::SourceManager const * sm, InputIterator firstFun, InputIterator lastFun)
{
	for (; firstFun != lastFun; ++firstFun)
	{
		{
			clang::CFG * cfg = clang::CFG::buildCFG(*firstFun, (*firstFun)->getBody(), &ctx);
			//cfg->dump(clang::LangOptions());
			delete cfg;
		}

		rcfg c(**firstFun);
		c.pretty_print(fout, sm);
	}
}

#endif
