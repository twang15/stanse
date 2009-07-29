package cz.muni.stanse.codestructures;

import cz.muni.stanse.codestructures.builders.CallGraphBuilder;
import cz.muni.stanse.codestructures.builders.NodeToCFGdictionaryBuilder;
import cz.muni.stanse.codestructures.builders.StartFunctionsSetBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.Map;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public abstract class LazyInternalStructures {

    public LazyInternalStructures(final Collection<Unit> units,
		final Map<CFG,Unit> cfgToUnitDictionary) {
        this.units = units;
        this.cfgToUnitDictionary =
                Collections.unmodifiableMap(cfgToUnitDictionary);

        startFunctions = null;
        callGraph = null;
        navigator = null;
        argumentPassingManager = null;
        returnValuePassingManager = null;
        nodeToCFGdictionary = null;
        elementToCFGdictionary = null;
    }

    public Collection<Unit> getUnits() {
        return units;
    }

    public Collection<CFG> getCFGs() {
        return Collections.unmodifiableSet(getCFGtoUnitDictionary().keySet());
    }

    public Set<CFG> getStartFunctions() {
        if (startFunctions == null)
            setStartFunctions();
        return startFunctions;
    }

    public DefaultDirectedGraph<CFG, DefaultEdge> getCallGraph() {
        if (callGraph == null)
            setCallGraph();
        return callGraph;
    }

    public ArgumentPassingManager getArgumentPassingManager() {
        if (argumentPassingManager == null)
            setArgumentPassingManager();
        return argumentPassingManager;
    }

    public ReturnValuePassingManager getReturnValuePassingManager() {
        if (returnValuePassingManager == null)
            setReturnValuePassingManager();
        return returnValuePassingManager;
    }

    public CFGsNavigator getNavigator() {
        if (navigator == null)
            setNavigator();
        return navigator;
    }

    public Map<CFGNode,CFG> getNodeToCFGdictionary() {
        if (nodeToCFGdictionary == null)
            setNodeToCFGdictionary();
        return nodeToCFGdictionary;
    }

    public Map<CFG,Unit> getCFGtoUnitDictionary() {
        return cfgToUnitDictionary;
    }

    public ElementCFGdictionary getElementToCFGdictionary() {
        if (elementToCFGdictionary == null)
            setElementToCFGdictionary();
        return elementToCFGdictionary;
    }

//    public synchronized void clearStartFunctions() { startFunctions = null; }
//    public synchronized void clearCallGraph() { callGraph = null; }
//    public synchronized void clearArgumentPassingManager() { argumentPassingManager = null; }
//    public synchronized void clearNavigator() { navigator = null; }
//    public synchronized void clearNodeToCFGdictionary() { nodeToCFGdictionary = null; }
//    public synchronized void clearCFGtoUnitDictionary() { cfgToUnitDictionary = null; }
//    public synchronized void clearElementToCFGdictionary() { elementToCFGdictionary = null; }
//
//    public synchronized void clear() {
//        clearStartFunctions();
//        clearCallGraph();
//        clearArgumentPassingManager();
//        clearNavigator();
//        clearNodeToCFGdictionary();
//        clearCFGtoUnitDictionary();
//        clearElementToCFGdictionary();
//    }

    // private section

    private synchronized void setStartFunctions() {
        if (startFunctions == null)
            startFunctions = Collections.unmodifiableSet(
                                  StartFunctionsSetBuilder.run(getCallGraph()));
    }

    private synchronized void setCallGraph() {
        if (callGraph == null)
            callGraph = CallGraphBuilder.run(getCFGs(),getNavigator(),
                                             getNodeToCFGdictionary());
    }

    private synchronized void setArgumentPassingManager() {
        if (argumentPassingManager == null)
            argumentPassingManager = new ArgumentPassingManager(getNavigator(),
                                                      getNodeToCFGdictionary());
    }

    private synchronized void setReturnValuePassingManager() {
        if (returnValuePassingManager == null)
            returnValuePassingManager =
                new ReturnValuePassingManager(getNavigator());
    }

    abstract void setNavigator();

    private synchronized void setNodeToCFGdictionary() {
        if (nodeToCFGdictionary == null)
            nodeToCFGdictionary = Collections.unmodifiableMap(
                                     NodeToCFGdictionaryBuilder.run(getCFGs()));
    }

    private synchronized void setElementToCFGdictionary() {
        if (elementToCFGdictionary == null)
            elementToCFGdictionary = new ElementCFGdictionary(getCFGs());
    }

    private final Collection<Unit> units;
    private final Map<CFG,Unit> cfgToUnitDictionary;

    private Set<CFG> startFunctions;
    private DefaultDirectedGraph<CFG,DefaultEdge> callGraph;
    protected CFGsNavigator navigator;
    private ArgumentPassingManager argumentPassingManager;
    private ReturnValuePassingManager returnValuePassingManager;
    private Map<CFGNode,CFG> nodeToCFGdictionary;
    private ElementCFGdictionary elementToCFGdictionary;
}