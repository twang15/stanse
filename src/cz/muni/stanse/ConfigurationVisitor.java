package cz.muni.stanse;

import cz.muni.stanse.codestructures.Unit;
import cz.muni.stanse.codestructures.CFG;
import cz.muni.stanse.checker.Checker;

import java.util.List;
import java.util.HashMap;

public interface ConfigurationVisitor {

    // public section

    public boolean visit(final List<Unit> units, final Checker checker,
                         final HashMap<CFG,Unit> cfgToUnitMapping)
                         throws Exception;
}