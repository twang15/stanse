package cz.muni.stanse.automatonchecker;

import cz.muni.stanse.codestructures.CFGNode;
import cz.muni.stanse.codestructures.LazyInternalStructures;
import cz.muni.stanse.utils.Make;
import cz.muni.stanse.utils.Pair;
import cz.muni.stanse.utils.xmlpatterns.XMLAlgo;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.dom4j.Element;

/**
 * Checks whether already freed pointer was not assigned again before
 * dereference by a chance.
 *
 * It is usually
 * a = alloc(); if (!a) { a = b; a->x; }
 * a = alloc(); free(a); a = b; a->x;
 *
 * @author xslaby
 */
final class FPDMemoryReassignedFilter extends FalsePositivesDetector {
    @Override
    int getTraceImportance(final java.util.List<CFGNode> path,
                          final java.util.Stack<CFGNode> cfgContext,
                          final ErrorRule rule) {
        Iterator<CFGNode> nodeI = path.listIterator();
        Element nEl = nodeI.next().getElement();
        List<Element> left = null;
        String nElName = nEl.getName();
	Vector<String> va = rule.getAutomatonID().getVarsAssignment();
        int idx = -1;
	if (rule.getErrorDescription().startsWith("unnecessary check") &&
		va.size() == 1) {
	    left = Make.linkedList(XMLAlgo.toElement("<id>" + va.get(0) +
		    "</id>"));
	} else if (nElName.equals("functionCall")) {
            if (nEl.nodeCount() != 2) /* name and id */
                return getBugImportance(0);
            idx = 1;
        } else if (nElName.equals("assignExpression")) {
            idx = 0;
        } else if (nElName.equals("assert")) {
            left = nEl.selectNodes(".//id");
        } else
            return getBugImportance(0);
        if (left == null) {
            left = new LinkedList<Element>();
            left.add((Element)nEl.elements().get(idx)); // leftside
        }
	while (nodeI.hasNext()) {
	    Element next = nodeI.next().getElement();
	    if (next.getName().equals("assert"))
		continue;

	    List<Element> assignElements = new LinkedList<Element>(
			    next.selectNodes(".//assignExpression"));
	    if (next.getName().equals("assignExpression"))
		    assignElements.add(next);
	    for (Element fno: assignElements) {
		Element left1 = (Element)fno.elements().get(0);
                for (Element e: left)
                    if (XMLAlgo.equalElements(e, left1))
                        return getFalsePositiveImportance();
            }
	}
        return getBugImportance(0);
    }
}
final class FPDMemoryReassignedFilterCreator
        extends FalsePositivesDetectorCreator {

    @Override
    boolean isApplicable(final XMLAutomatonDefinition definition,
                         boolean isInterprocediral) {
        return definition.getAutomatonName().equals(
                "Linux kernel pointer analysis automaton checker");
    }

    @Override
    FalsePositivesDetector create(XMLAutomatonDefinition definition,
        LazyInternalStructures internals,boolean isInterprocediral,
        final Map<CFGNode,Pair<PatternLocation,PatternLocation>>
                                                       nodeLocationDictionary) {
        return new FPDMemoryReassignedFilter();
    }
}
