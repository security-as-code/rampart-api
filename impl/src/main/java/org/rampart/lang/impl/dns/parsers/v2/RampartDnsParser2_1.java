package org.rampart.lang.impl.dns.parsers.v2;

import static org.rampart.lang.api.constants.RampartDnsConstants.DNS_KEY;

import java.util.Map;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.impl.core.parsers.RampartRuleNameParser;
import org.rampart.lang.impl.core.parsers.RampartTargetOsParser;
import org.rampart.lang.impl.core.parsers.v2.RampartActionParser2_0;
import org.rampart.lang.impl.core.validators.RuleStructureValidator;
import org.rampart.lang.java.builder.RampartDnsBuilder;


/** Parser of the RAMPART DNS rule. */
public final class RampartDnsParser2_1 implements Validatable<RampartDnsBuilder, InvalidRampartRuleException> {
    /** Supported action types. */
    static final RampartActionType[] SUPPORTED_ACTIONS =
        {RampartActionType.ALLOW, RampartActionType.DETECT, RampartActionType.PROTECT};

    /** Actions supported by the rule. */
    static final RampartConstant[] SUPPORTED_ACTION_KEYS = RampartActionParser2_0.toRampartKeys(SUPPORTED_ACTIONS);

    /** Key used by this rule. */
    static final RampartConstant THIS_RULE_KEY = DNS_KEY;

    /** A convenient array of the key that could be used in the checks for supported fields. */
    static final RampartConstant[] THIS_RULE_KEYS = {THIS_RULE_KEY};


    private final Map<String, RampartList> symbolTable;

    public RampartDnsParser2_1(Map<String, RampartList> symbolTable) {
        this.symbolTable = symbolTable;
    }

    //@Override
    public RampartDnsBuilder validate() throws InvalidRampartRuleException {
        return parse(symbolTable);
    }

    public static RampartDnsBuilder parse(Map<String, RampartList> symbolTable) throws InvalidRampartRuleException {
        final RampartString ruleName = RampartRuleNameParser.getRuleName(symbolTable, THIS_RULE_KEY);
        RuleStructureValidator.validateDeclarations(
            ruleName, symbolTable,
            THIS_RULE_KEYS,
            SUPPORTED_ACTION_KEYS,
            RampartDnsLookupParser.SUPPORTED_FIELDS
        );

        return new RampartDnsBuilder()
                .addRuleName(ruleName)
                .addLookupTarget(RampartDnsLookupParser.parseTarget(symbolTable))
                .addAction(RampartActionParser2_0.parseRampartAction(symbolTable, ruleName, SUPPORTED_ACTIONS))
                .addTargetOSList(RampartTargetOsParser.parseTargetOs(symbolTable, THIS_RULE_KEY));
    }
}
