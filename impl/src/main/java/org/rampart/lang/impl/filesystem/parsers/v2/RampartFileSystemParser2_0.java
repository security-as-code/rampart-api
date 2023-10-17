package org.rampart.lang.impl.filesystem.parsers.v2;

import static org.rampart.lang.api.constants.RampartFileSystemConstants.ABSOLUTE_KEY;
import static org.rampart.lang.api.constants.RampartFileSystemConstants.READ_KEY;
import static org.rampart.lang.api.constants.RampartFileSystemConstants.RELATIVE_KEY;
import static org.rampart.lang.api.constants.RampartFileSystemConstants.TRAVERSAL_KEY;
import static org.rampart.lang.api.constants.RampartFileSystemConstants.WRITE_KEY;
import static org.rampart.lang.api.constants.RampartFileSystemConstants.INPUT_KEY;
import static org.rampart.lang.api.constants.RampartFileSystemConstants.FILESYSTEM_KEY;

import java.util.List;
import java.util.Map;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.api.filesystem.RampartFileSystemOperation;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.impl.core.parsers.RampartRuleNameParser;
import org.rampart.lang.impl.core.parsers.RampartTargetOsParser;
import org.rampart.lang.impl.core.parsers.v2.RampartActionParser2_0;
import org.rampart.lang.impl.core.parsers.v2.RampartInputParser;
import org.rampart.lang.impl.core.validators.RuleStructureValidator;
import org.rampart.lang.java.builder.RampartFileSystemBuilder;


public class RampartFileSystemParser2_0 implements Validatable<RampartFileSystemBuilder, InvalidRampartRuleException> {
    /** Supported action types. */
    static final RampartActionType[] SUPPORTED_ACTIONS =
        {RampartActionType.ALLOW, RampartActionType.DETECT, RampartActionType.PROTECT};

    /** Actions supported by the rule. */
    static final RampartConstant[] SUPPORTED_ACTION_KEYS = RampartActionParser2_0.toRampartKeys(SUPPORTED_ACTIONS);

    /** Key used by this rule. */
    static final RampartConstant THIS_RULE_KEY = FILESYSTEM_KEY;

    /** A convenient array of the key that could be used in the checks for supported fields. */
    static final RampartConstant[] THIS_RULE_KEYS = {THIS_RULE_KEY};

    private final Map<String, RampartList> symbolTable;

    public RampartFileSystemParser2_0(Map<String, RampartList> symbolTable) {
        this.symbolTable = symbolTable;
    }

    //@Override
    public RampartFileSystemBuilder validate() throws InvalidRampartRuleException {
        return parse(symbolTable);
    }

    public static RampartFileSystemBuilder parse(Map<String, RampartList> symbolTable) throws InvalidRampartRuleException {
        final RampartString ruleName = RampartRuleNameParser.getRuleName(symbolTable, THIS_RULE_KEY);
        RuleStructureValidator.validateDeclarations(
            ruleName, symbolTable,
            RampartFileSystemOperationParser.SUPPORTED_KEYS,
            RampartInputParser.DEFAULT_KEYS,
            RampartTraversalParser.DEFAULT_KEYS,
            THIS_RULE_KEYS,
            SUPPORTED_ACTION_KEYS
        );
        final RampartFileSystemOperation operation = RampartFileSystemOperationParser.parseOperation(symbolTable);
        final List<RampartConstant> traversalConfigs = RampartTraversalParser.parseTraversalOptions(symbolTable);
        final RampartList inputs = RampartInputParser.parseDataInputs(symbolTable);
        final RampartAction action = RampartActionParser2_0.parseRampartAction(symbolTable, ruleName, SUPPORTED_ACTIONS);
        final RampartList targetOSList = RampartTargetOsParser.parseTargetOs(symbolTable, THIS_RULE_KEY);
        crossValidate(operation, traversalConfigs, inputs, action);

        return builderForTraversalConfig(traversalConfigs)
                .addRuleName(ruleName)
                .addFileOperation(operation)
                .addFilePaths(RampartFileSystemOperationParser.parsePaths(symbolTable, operation, targetOSList))
                .addDataInputs(inputs)
                .addAction(action)
                .addTargetOSList(targetOSList);
    }


    /** Creates a builder for get given traversal. */
    static RampartFileSystemBuilder builderForTraversalConfig(List<RampartConstant> traversalConfigs) {
        final RampartFileSystemBuilder result = new RampartFileSystemBuilder();
        for (RampartConstant config : traversalConfigs) {
            if (config.equals(RELATIVE_KEY)) {
                result.protectOnRelativePaths();
            } else if (config.equals(ABSOLUTE_KEY)) {
                result.protectOnAbsolutePaths();
            }
        }
        return result;
    }


    public static void crossValidate(
            RampartFileSystemOperation operation,
            List<RampartConstant> traversalOptions,
            RampartList inputs,
            RampartAction action) throws InvalidRampartRuleException {
        switch (operation) {
            case READ:
            case WRITE:
                if (traversalOptions.contains(RELATIVE_KEY)
                        || traversalOptions.contains(ABSOLUTE_KEY)) {
                    throw new InvalidRampartRuleException(getIncompatibleOperationMessage(operation, TRAVERSAL_KEY));
                }
                if (inputs.isEmpty() == RampartBoolean.FALSE) {
                    throw new InvalidRampartRuleException(getIncompatibleOperationMessage(operation, INPUT_KEY));
                }
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

    static String getIncompatibleOperationMessage(RampartFileSystemOperation op, RampartConstant declaration) {
        return "\"" + op + "\" operation cannot be declared with " + declaration;
    }
}
