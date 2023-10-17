package org.rampart.lang.impl.socket.parsers.v2;

import java.util.Map;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.socket.RampartSocketOperation;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.impl.core.parsers.RampartRuleNameParser;
import org.rampart.lang.impl.core.parsers.RampartTargetOsParser;
import org.rampart.lang.impl.core.parsers.v2.RampartActionAttributeParser2_3;
import org.rampart.lang.impl.core.parsers.v2.RampartMetadataParser;
import org.rampart.lang.impl.core.validators.RuleStructureValidator;
import org.rampart.lang.java.builder.RampartSocketBuilder;

public class RampartSocketParser2_6 implements Validatable<RampartSocketBuilder, InvalidRampartRuleException> {
    private final Map<String, RampartList> symbolTable;

    public RampartSocketParser2_6(Map<String, RampartList> symbolTable) {
        this.symbolTable = symbolTable;
    }

    //@Override
    public RampartSocketBuilder validate() throws InvalidRampartRuleException {
        return parse(symbolTable);
    }


    public static RampartSocketBuilder parse(Map<String, RampartList> symbolTable) throws InvalidRampartRuleException {
        final RampartString ruleName = RampartRuleNameParser.getRuleName(symbolTable, RampartSocketParser2_1.THIS_RULE_KEY);
        RuleStructureValidator.validateDeclarations(
            ruleName, symbolTable,
            RampartSocketParser2_1.THIS_RULE_KEYS,
            RampartSocketParser2_1.SUPPORTED_ACTION_KEYS,
            RampartSocketOperationParser.SUPPORTED_KEYS,
            RampartMetadataParser.DEFAULT_METADATA_KEYS
        );

        final RampartList targetOsList = RampartTargetOsParser.parseTargetOs(symbolTable, RampartSocketParser2_1.THIS_RULE_KEY);
        final RampartSocketOperation socketOperation = RampartSocketOperationParser.parseOperation(symbolTable);

        final RampartAction socketAction =
                RampartActionAttributeParser2_3.parseActionWithOptionalAttribute(
                    symbolTable, ruleName,
                    RampartSocketParser2_1.SUPPORTED_ACTIONS,
                    RampartSocketParser2_1.ATTRIBUTE_CONFIG_PARSER,
                    RampartSocketParser2_1.SUPPORTED_ACTION_TARGETS
                );

        RampartSocketParser2_1.crossValidate(socketOperation, socketAction);
        return new RampartSocketBuilder()
                .addRuleName(ruleName)
                .addSocketOperation(socketOperation)
                .addTargetOSList(targetOsList)
                .addAction(socketAction)
                .addMetadata(RampartMetadataParser.parseRuleMetadata(symbolTable));
    }
}
