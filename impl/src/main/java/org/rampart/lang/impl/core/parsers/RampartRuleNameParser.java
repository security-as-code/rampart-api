package org.rampart.lang.impl.core.parsers;

import java.util.Map;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.RampartValidatorBase;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;

public final class RampartRuleNameParser {
    private RampartRuleNameParser() {
        throw new UnsupportedOperationException();
    }


    /**
     * Retrieves rule name from the rule definition. For example it would
     * fetch the "This is a magic rule" for the following code block:
     *
     * <code>
     * magic("This is a magical rule"):
     *   ...
     * endmagic
     * </code>
     *
     * @param ruleDefinition definition of the rule.
     * @return name of the rule - first string in its definition.
     * @throws InvalidRampartRuleException if name is missing.
     */
    public static RampartString getRuleName(RampartList ruleDefinition) throws InvalidRampartRuleException {
        final RampartString maybeName = RampartInterpreterUtils.findFirstRampartString(ruleDefinition);
        if (RampartValidatorBase.isNullOrEmptyString(maybeName)) {
            throw new InvalidRampartRuleException("Rule name is missing");
        }
        return maybeName;
    }


    /**
     * Retrieves rule name from a "rule definition" structure that contains all the rule data.
     *
     * Given the below block and "magic" as <tt>ruleTypeName</tt> it would retrieve
     * "This is a magical rule" as the result.
     *
     * <code>
     * magic("This is a magical rule"):
     *   ...
     * endmagic
     * </code>

     * @param visitorSymbolTable data rule definition table.
     * @param ruleTypeName name of the rule type. This is the "opening tag" of the rule structure
     * ("magic" in the example).
     * @return name of the rule.
     * @throws InvalidRampartRuleException if name is missing.
     */
    public static RampartString getRuleName(
            Map<String, RampartList> visitorSymbolTable,
            String ruleTypeName) throws InvalidRampartRuleException {
        return getRuleName(visitorSymbolTable.get(ruleTypeName));
    }


    /**
     * Retrieves rule name from a "rule definition" structure that contains all the rule data.
     *
     * This is a synonym to {@link #getRuleName(Map, String)} but takes constant instead of string.
     *
     */
    public static RampartString getRuleName(
            Map<String, RampartList> visitorSymbolTable,
            RampartConstant ruleTypeName) throws InvalidRampartRuleException {
        return getRuleName(visitorSymbolTable, ruleTypeName.toString());
    }

    /**
     * Retrieves rule name from a "rule definition" structure that contains all the rule data.
     *
     * This is a synonym to {@link #getRuleName(Map, String)} but takes rampart string instead of string.
     *
     */
    public static RampartString getRuleName(
            Map<String, RampartList> visitorSymbolTable,
            RampartString ruleTypeName) throws InvalidRampartRuleException {
        return getRuleName(visitorSymbolTable, ruleTypeName.toString());
    }
}
