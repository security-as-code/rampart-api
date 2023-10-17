package org.rampart.lang.impl.socket.parsers.v2;

import java.util.Map;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.apiprotect.RampartApiFilter;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartActionTarget;
import org.rampart.lang.api.core.RampartActionWithAttribute;
import org.rampart.lang.api.socket.RampartSocketOperation;
import org.rampart.lang.impl.apiprotect.parsers.RampartApiFilterParser;
import org.rampart.lang.impl.apiprotect.validators.RampartApiProtectValidator2_9;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.impl.core.parsers.RampartRuleNameParser;
import org.rampart.lang.impl.core.parsers.RampartTargetOsParser;
import org.rampart.lang.impl.core.parsers.v2.RampartActionAttributeParser2_3;
import org.rampart.lang.impl.core.parsers.v2.RampartInputParser;
import org.rampart.lang.impl.core.parsers.v2.RampartMetadataParser;
import org.rampart.lang.impl.core.validators.RuleStructureValidator;
import org.rampart.lang.java.RampartPrimitives;
import org.rampart.lang.java.builder.RampartSocketBuilder;

public class RampartSocketParser2_9 implements Validatable<RampartSocketBuilder, InvalidRampartRuleException> {
    private final Map<String, RampartList> symbolTable;

    public RampartSocketParser2_9(Map<String, RampartList> symbolTable) {
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
            RampartMetadataParser.DEFAULT_METADATA_KEYS,
            RampartInputParser.DEFAULT_KEYS,
            RampartApiFilterParser.DEFAULT_API_FILTER_KEYS
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
        final RampartList taintInputs = RampartInputParser.parseDataInputs(symbolTable);
        RampartSocketParser2_1.crossValidate(socketOperation, socketAction);
        final RampartApiFilter apiFilter = RampartApiFilterParser.parse(symbolTable);
        RampartApiProtectValidator2_9.crossValidateInputAndActions(
                RampartSocketParser2_1.THIS_RULE_KEY, socketAction, taintInputs, apiFilter);
        crossValidate(socketAction, taintInputs, apiFilter);
        return new RampartSocketBuilder()
                .addRuleName(ruleName)
                .addSocketOperation(socketOperation)
                .addTargetOSList(targetOsList)
                .addAction(socketAction)
                .addMetadata(RampartMetadataParser.parseRuleMetadata(symbolTable))
                .addDataInputs(taintInputs)
                .addApiFilter(apiFilter);
    }


     /**
      * It can be either:
      * "API Protect socket rules with API clause and/or Taint inputs"
      *  OR
      *  any of the "TCP to SSL", "TLS Upgrade rules".
      * But it is not valid to mix these declarations.
      *
      * @param action configured in the rule
      * @param taintInputs array, null is not expected, it should be EMPTY
      * @param rampartApiFilter configured api filter
      * @throws InvalidRampartRuleException when validation fails
      */
    static void crossValidate(
            RampartAction action,
            RampartList taintInputs,
            RampartApiFilter rampartApiFilter) throws InvalidRampartRuleException {

        // right now, incompatibilities are only possible for TLS-Upgrade and Connection-Secure rules,
        // both of them use RampartActionWithAttribute
        if (!(action instanceof RampartActionWithAttribute)) {
            return;
        }

        RampartActionTarget actionTarget = ((RampartActionWithAttribute) action).getTarget();
        if (!RampartPrimitives.toJavaBoolean(taintInputs.isEmpty())) {
            throw new InvalidRampartRuleException(
                    "Invalid rule configuration, taint inputs can not be used together with action target "
                    + "\"" + actionTarget + "\".");
        }
        if (rampartApiFilter != null) {
            throw new InvalidRampartRuleException(
                    "Invalid rule configuration, api clause can not be used together with action target "
                    + "\"" + actionTarget + "\".");
        }
    }

}
