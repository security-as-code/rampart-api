package org.rampart.lang.impl.filesystem.parsers.v2;

import static org.rampart.lang.api.constants.RampartFileSystemConstants.ABSOLUTE_KEY;
import static org.rampart.lang.api.constants.RampartFileSystemConstants.READ_KEY;
import static org.rampart.lang.api.constants.RampartFileSystemConstants.RELATIVE_KEY;
import static org.rampart.lang.api.constants.RampartFileSystemConstants.TRAVERSAL_KEY;
import static org.rampart.lang.api.constants.RampartFileSystemConstants.WRITE_KEY;

import java.util.List;
import java.util.Map;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.apiprotect.RampartApiFilter;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.api.filesystem.RampartFileSystemOperation;
import org.rampart.lang.impl.apiprotect.parsers.RampartApiFilterParser;
import org.rampart.lang.impl.apiprotect.validators.RampartApiProtectValidator2_9;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.impl.core.parsers.RampartRuleNameParser;
import org.rampart.lang.impl.core.parsers.RampartTargetOsParser;
import org.rampart.lang.impl.core.parsers.v2.RampartActionParser2_3;
import org.rampart.lang.impl.core.parsers.v2.RampartInputParser;
import org.rampart.lang.impl.core.parsers.v2.RampartMetadataParser;
import org.rampart.lang.impl.core.validators.RuleStructureValidator;
import org.rampart.lang.java.builder.RampartFileSystemBuilder;


public class RampartFileSystemParser2_9 implements Validatable<RampartFileSystemBuilder, InvalidRampartRuleException> {
    private final Map<String, RampartList> symbolTable;

    public RampartFileSystemParser2_9(Map<String, RampartList> symbolTable) {
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
            RampartMetadataParser.DEFAULT_METADATA_KEYS,
            RampartApiFilterParser.DEFAULT_API_FILTER_KEYS
        );
        final RampartFileSystemOperation operation = RampartFileSystemOperationParser.parseOperation(symbolTable);
        final List<RampartConstant> traversalConfigs = RampartTraversalParser.parseTraversalOptions(symbolTable);
        final RampartList inputs = RampartInputParser.parseDataInputs(symbolTable);
        final RampartAction action = RampartActionParser2_3.parseRampartAction(
                symbolTable, ruleName, RampartFileSystemParser2_0.SUPPORTED_ACTIONS
        );
        final RampartList targetOSList = RampartTargetOsParser.parseTargetOs(
                symbolTable, RampartFileSystemParser2_0.THIS_RULE_KEY);
        final RampartApiFilter apiFilter = RampartApiFilterParser.parse(symbolTable);
        RampartFileSystemParser2_9.crossValidate(operation, traversalConfigs, action, inputs, apiFilter);

        return RampartFileSystemParser2_0.builderForTraversalConfig(traversalConfigs)
                .addRuleName(ruleName)
                .addFileOperation(operation)
                .addFilePaths(RampartFileSystemOperationParser.parsePaths(symbolTable, operation, targetOSList))
                .addDataInputs(inputs)
                .addAction(action)
                .addTargetOSList(targetOSList)
                .addMetadata(RampartMetadataParser.parseRuleMetadata(symbolTable))
                .addApiFilter(apiFilter);
    }


    /**
     * Validates multiple options against mutually-exclusive combinations.
     *
     * Since version 2.9 read and write operations are allowed to have input clauses as part of their definition. I.e.
     * the following code is valid only since RAMPART/2.9.
     *
     * <code>
     *   filesystem("Whitelist - Detect read access to /tmp/test.tmp")
     *     read("/tmp/test.tmp")
     *     input(http)
     *     allow()
     *   endfilesystem
     * </code>
     */
    public static void crossValidate(
            RampartFileSystemOperation operation,
            List<RampartConstant> traversalOptions,
            RampartAction action,
            RampartList inputs,
            RampartApiFilter apiFilter) throws InvalidRampartRuleException {
        switch (operation) {
            case READ:
            case WRITE:
                if (traversalOptions.contains(RELATIVE_KEY)
                        || traversalOptions.contains(ABSOLUTE_KEY)) {
                    throw new InvalidRampartRuleException(
                        RampartFileSystemParser2_0.getIncompatibleOperationMessage(operation, TRAVERSAL_KEY));
                }
                RampartApiProtectValidator2_9.crossValidateInputAndActions(
                        RampartFileSystemParser2_0.THIS_RULE_KEY, action, inputs, apiFilter);
                break;
            case NOOP:
                if (traversalOptions.isEmpty()) {
                    throw new InvalidRampartRuleException("missing one of \"" + READ_KEY + "\", " + "\"" + WRITE_KEY
                            + "\" or \"" + TRAVERSAL_KEY + "\" declarations");
                }
                if (action.getActionType() == RampartActionType.ALLOW) {
                    throw new InvalidRampartRuleException("action \"" + RampartActionType.ALLOW
                            + "\" is not supported with declaration \"" + TRAVERSAL_KEY + "\"");
                }
                break;
        }
    }
}
