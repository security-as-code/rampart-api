package org.rampart.lang.impl.filesystem.parsers.v2;

import java.util.List;
import java.util.Map;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.filesystem.RampartFileSystemOperation;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.impl.core.parsers.RampartRuleNameParser;
import org.rampart.lang.impl.core.parsers.RampartTargetOsParser;
import org.rampart.lang.impl.core.parsers.v2.RampartActionParser2_3;
import org.rampart.lang.impl.core.parsers.v2.RampartInputParser;
import org.rampart.lang.impl.core.parsers.v2.RampartMetadataParser;
import org.rampart.lang.impl.core.validators.RuleStructureValidator;
import org.rampart.lang.java.builder.RampartFileSystemBuilder;


public class RampartFileSystemParser2_6 implements Validatable<RampartFileSystemBuilder, InvalidRampartRuleException> {
    private final Map<String, RampartList> symbolTable;

    public RampartFileSystemParser2_6(Map<String, RampartList> symbolTable) {
        this.symbolTable = symbolTable;
    }

    //@Override
    public RampartFileSystemBuilder validate() throws InvalidRampartRuleException {
        return parse(symbolTable);
    }

    public static RampartFileSystemBuilder parse(Map<String, RampartList> symbolTable) throws InvalidRampartRuleException {
        final RampartString ruleName = RampartRuleNameParser.getRuleName(symbolTable, RampartFileSystemParser2_0.THIS_RULE_KEY);
        RuleStructureValidator.validateDeclarations(
            ruleName, symbolTable,
            RampartFileSystemOperationParser.SUPPORTED_KEYS,
            RampartInputParser.DEFAULT_KEYS,
            RampartTraversalParser.DEFAULT_KEYS,
            RampartFileSystemParser2_0.THIS_RULE_KEYS,
            RampartFileSystemParser2_0.SUPPORTED_ACTION_KEYS,
            RampartMetadataParser.DEFAULT_METADATA_KEYS
        );
        final RampartFileSystemOperation operation = RampartFileSystemOperationParser.parseOperation(symbolTable);
        final List<RampartConstant> traversalConfigs = RampartTraversalParser.parseTraversalOptions(symbolTable);
        final RampartList inputs = RampartInputParser.parseDataInputs(symbolTable);
        final RampartAction action = RampartActionParser2_3.parseRampartAction(
                symbolTable, ruleName, RampartFileSystemParser2_0.SUPPORTED_ACTIONS
        );
        final RampartList targetOSList = RampartTargetOsParser.parseTargetOs(
                symbolTable, RampartFileSystemParser2_0.THIS_RULE_KEY);
        RampartFileSystemParser2_0.crossValidate(operation, traversalConfigs, inputs, action);

        return RampartFileSystemParser2_0.builderForTraversalConfig(traversalConfigs)
                .addRuleName(ruleName)
                .addFileOperation(operation)
                .addFilePaths(RampartFileSystemOperationParser.parsePaths(symbolTable, operation, targetOSList))
                .addDataInputs(inputs)
                .addAction(action)
                .addTargetOSList(targetOSList)
                .addMetadata(RampartMetadataParser.parseRuleMetadata(symbolTable));
    }
}
