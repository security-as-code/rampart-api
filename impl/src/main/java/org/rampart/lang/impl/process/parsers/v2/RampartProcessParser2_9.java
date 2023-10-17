package org.rampart.lang.impl.process.parsers.v2;

import java.util.Map;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.apiprotect.RampartApiFilter;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.impl.apiprotect.parsers.RampartApiFilterParser;
import org.rampart.lang.impl.apiprotect.validators.RampartApiProtectValidator2_9;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.impl.core.parsers.RampartPathParser;
import org.rampart.lang.impl.core.parsers.RampartRuleNameParser;
import org.rampart.lang.impl.core.parsers.RampartTargetOsParser;
import org.rampart.lang.impl.core.parsers.v2.RampartActionParser2_3;
import org.rampart.lang.impl.core.parsers.v2.RampartInputParser;
import org.rampart.lang.impl.core.parsers.v2.RampartMetadataParser;
import org.rampart.lang.impl.core.validators.RuleStructureValidator;
import org.rampart.lang.java.builder.RampartProcessBuilder;

public final class RampartProcessParser2_9 implements Validatable<RampartProcessBuilder, InvalidRampartRuleException> {
    private final Map<String, RampartList> symbolTable;

    public RampartProcessParser2_9(Map<String, RampartList> symbolTable) {
        this.symbolTable = symbolTable;
    }

    //@Override
    public RampartProcessBuilder validate() throws InvalidRampartRuleException {
        return parse(symbolTable);
    }


    public static RampartProcessBuilder parse(Map<String, RampartList> symbolTable) throws InvalidRampartRuleException {
        final RampartString ruleName = RampartRuleNameParser.getRuleName(symbolTable, RampartProcessParser2_0.THIS_RULE_KEY);
        RuleStructureValidator.validateDeclarations(
            ruleName, symbolTable,
            RampartProcessParser2_0.REQUIRED_FIELDS,
            RampartProcessParser2_0.THIS_RULE_KEYS,
            RampartProcessParser2_0.SUPPORTED_ACTION_KEYS,
            RampartMetadataParser.DEFAULT_METADATA_KEYS,
            RampartInputParser.DEFAULT_KEYS,
            RampartApiFilterParser.DEFAULT_API_FILTER_KEYS
        );
        RuleStructureValidator.validateRequiredKeys(
            RampartProcessParser2_0.MODEL_NAME, symbolTable.keySet(),
            RampartProcessParser2_0.REQUIRED_FIELDS
        );

        final RampartList targetOsList = RampartTargetOsParser.parseTargetOs(symbolTable, RampartProcessParser2_0.THIS_RULE_KEY);
        final RampartAction action = RampartActionParser2_3.parseRampartAction(
                symbolTable, ruleName, RampartProcessParser2_0.SUPPORTED_ACTIONS
        );
        final RampartList taintInputs = RampartInputParser.parseDataInputs(symbolTable);
        final RampartApiFilter apiFilter = RampartApiFilterParser.parse(symbolTable);
        RampartApiProtectValidator2_9.crossValidateInputAndActions(RampartProcessParser2_0.THIS_RULE_KEY,
                action, taintInputs, apiFilter);

        return new RampartProcessBuilder()
                .addTargetOSList(targetOsList)
                .addRuleName(ruleName)
                .addProcessList(RampartPathParser.parsePaths(
                        symbolTable, RampartProcessParser2_0.PATH_ELEMENT_KEY, targetOsList
                ))
                .addAction(action)
                .addMetadata(RampartMetadataParser.parseRuleMetadata(symbolTable))
                .addDataInputs(taintInputs)
                .addApiFilter(apiFilter);
    }
}