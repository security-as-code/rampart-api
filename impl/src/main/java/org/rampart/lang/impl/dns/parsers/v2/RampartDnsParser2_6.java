package org.rampart.lang.impl.dns.parsers.v2;

import java.util.Map;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.impl.core.parsers.RampartRuleNameParser;
import org.rampart.lang.impl.core.parsers.RampartTargetOsParser;
import org.rampart.lang.impl.core.parsers.v2.RampartActionParser2_3;
import org.rampart.lang.impl.core.parsers.v2.RampartMetadataParser;
import org.rampart.lang.impl.core.validators.RuleStructureValidator;
import org.rampart.lang.java.builder.RampartDnsBuilder;


/** Parser of the RAMPART DNS rule. */
public final class RampartDnsParser2_6 implements Validatable<RampartDnsBuilder, InvalidRampartRuleException> {
    private final Map<String, RampartList> symbolTable;

    public RampartDnsParser2_6(Map<String, RampartList> symbolTable) {
        this.symbolTable = symbolTable;
    }

    //@Override
    public RampartDnsBuilder validate() throws InvalidRampartRuleException {
        return parse(symbolTable);
    }

    public static RampartDnsBuilder parse(Map<String, RampartList> symbolTable) throws InvalidRampartRuleException {
        final RampartString ruleName = RampartRuleNameParser.getRuleName(symbolTable, RampartDnsParser2_1.THIS_RULE_KEY);
        RuleStructureValidator.validateDeclarations(
            ruleName, symbolTable,
            RampartDnsParser2_1.THIS_RULE_KEYS,
            RampartDnsParser2_1.SUPPORTED_ACTION_KEYS,
            RampartDnsLookupParser.SUPPORTED_FIELDS,
            RampartMetadataParser.DEFAULT_METADATA_KEYS
        );

        return new RampartDnsBuilder()
                .addRuleName(ruleName)
                .addLookupTarget(RampartDnsLookupParser.parseTarget(symbolTable))
                .addAction(RampartActionParser2_3.parseRampartAction(
                        symbolTable, ruleName, RampartDnsParser2_1.SUPPORTED_ACTIONS
                ))
                .addTargetOSList(RampartTargetOsParser.parseTargetOs(symbolTable, RampartDnsParser2_1.THIS_RULE_KEY))
                .addMetadata(RampartMetadataParser.parseRuleMetadata(symbolTable));
    }
}
