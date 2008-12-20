/**
 * @brief
 * 
 */
package cz.muni.stanse.automatonchecker;

import cz.muni.stanse.utils.Pair;

import java.util.Iterator;

/**
 * @brief
 *
 * @see
 */
final class XMLPattern {

    // package-private section

    /**
     * @brief
     *
     * @param
     * @return
     * @throws
     * @see
     */
    XMLPattern(final org.dom4j.Element XMLelement) {
        patternXMLelement = XMLelement;
        name = patternXMLelement.attribute("name").getValue();
        constructive = XMLelement.selectNodes(
                                   ".//var[@constructive=\"false\"]").isEmpty();
    }

    /**
     * @brief
     *
     * @param
     * @return
     * @throws
     * @see
     */
    String getName() {
        return name;
    }

    boolean isSonstructive() {
        return constructive;
    }

    /**
     * @brief
     *
     * @param
     * @return
     * @throws
     * @see
     */
    Pair<Boolean,PatternVariablesAssignment>
    matchesXMLElement(final org.dom4j.Element XMLelement) {
        final PatternVariablesAssignment varsAssignment =
                new PatternVariablesAssignment();
        return new Pair<Boolean,PatternVariablesAssignment>(
                          matchingElements(getPatternXMLelement(),XMLelement,
                                           varsAssignment),
                          varsAssignment);
    }

    // private section

    /**
     * @brief
     *
     * @param
     * @return
     * @throws
     * @see
     */
    private static boolean matchingElements(final org.dom4j.Element XMLpivot,
                              final org.dom4j.Element XMLelement,
                              final PatternVariablesAssignment varsAssignment) {
        if (XMLpivot.getName().equals("nested"))
            return onNested(XMLpivot,XMLelement,varsAssignment);
        if (XMLpivot.getName().equals("any"))
            return true;
        if (XMLpivot.getName().equals("ignore"))
            return true;
        if (XMLpivot.getName().equals("var")) {
            varsAssignment.put(XMLpivot.attribute("name").getValue(),
                               XMLelement);
            return true;
        }
        
        //if (!XMLpivot.getName().equals(XMLelement.getName()))
        if (!XMLpivot.getName().equals(getAliasedName(XMLelement)))
            return false;
        if (XMLpivot.isTextOnly() != XMLelement.isTextOnly())
            return false;
        if (XMLpivot.isTextOnly() &&
            !XMLpivot.getText().equals(XMLelement.getText()))
            return false;

        final Iterator i = XMLpivot.elementIterator();
        final Iterator j = XMLelement.elementIterator();
        for ( ; i.hasNext() && j.hasNext(); )
            if (!matchingElements((org.dom4j.Element)i.next(),
                                  (org.dom4j.Element)j.next(),
                                  varsAssignment))
                return false;
        if (i.hasNext() || j.hasNext())
            return false;

        return true;
    }

    /**
     * @brief
     *
     * @param
     * @return
     * @throws
     * @see
     */
    private static boolean onNested(final org.dom4j.Element XMLpivot,
                              final org.dom4j.Element XMLelement,
                              final PatternVariablesAssignment varsAssignment) {
        if (matchingElements(
                           (org.dom4j.Element)XMLpivot.elementIterator().next(),
                           XMLelement,varsAssignment))
            return true;
        
        for (final Iterator j = XMLelement.elementIterator() ; j.hasNext(); )
            if (matchingElements(XMLpivot,(org.dom4j.Element)j.next(),
                                 varsAssignment))
                return true;

        return false;
    }

    /**
     * @brief
     *
     * @param
     * @return
     * @throws
     * @see
     */
    private org.dom4j.Element getPatternXMLelement() {
        return (org.dom4j.Element)patternXMLelement.elementIterator().next();
    }
    
    private static String getAliasedName(final org.dom4j.Element element) {
        final String elemName = element.getName();

        if (elemName.equals("prefixExpression") &&
            element.attribute("op").getText().equals("!"))
            return "prefixExpressionLogicalNot";  
        if (elemName.equals("binaryExpression") &&
            element.attribute("op").getText().equals("=="))
            return "binaryExpressionEquality";
        if (elemName.equals("binaryExpression") &&
            element.attribute("op").getText().equals("!="))
            return "binaryExpressionNonEquality";

        return elemName;
    }
            
    /**
     * @brief
     */
    private final org.dom4j.Element patternXMLelement;
    /**
     * @brief
     */
    private final String name;
    private final boolean constructive;
}
