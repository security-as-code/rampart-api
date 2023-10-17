package org.rampart.lang.impl.socket.parsers.v2;

import static org.rampart.lang.api.constants.RampartSocketConstants.ACCEPT_KEY;
import static org.rampart.lang.api.constants.RampartSocketConstants.IPV4_WILDCARD;
import static org.rampart.lang.api.constants.RampartSocketConstants.IPV6_WILDCARD;
import static org.rampart.lang.api.constants.RampartSocketConstants.SOCKET_KEY;

import java.util.Map;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartActionAttribute;
import org.rampart.lang.api.core.RampartActionTarget;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.api.core.RampartActionWithAttribute;
import org.rampart.lang.api.socket.RampartSocketOperation;
import org.rampart.lang.api.socket.RampartSocketType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.impl.core.parsers.RampartRuleNameParser;
import org.rampart.lang.impl.core.parsers.RampartTargetOsParser;
import org.rampart.lang.impl.core.parsers.v2.RampartActionAttributeParser2_0;
import org.rampart.lang.impl.core.parsers.v2.RampartActionParser2_0;
import org.rampart.lang.impl.core.validators.RuleStructureValidator;
import org.rampart.lang.java.RampartPrimitives;
import org.rampart.lang.java.builder.RampartSocketBuilder;

public class RampartSocketParser2_1 implements Validatable<RampartSocketBuilder, InvalidRampartRuleException> {
    /** Supported action types. */
    static final RampartActionType[] SUPPORTED_ACTIONS =
        {RampartActionType.ALLOW, RampartActionType.DETECT, RampartActionType.PROTECT};

    /** Actions supported by the rule. */
    static final RampartConstant[] SUPPORTED_ACTION_KEYS = RampartActionParser2_0.toRampartKeys(SUPPORTED_ACTIONS);

    /** Key used by this rule. */
    static final RampartConstant THIS_RULE_KEY = SOCKET_KEY;

    /** A convenient array of the key that could be used in the checks for supported fields. */
    static final RampartConstant[] THIS_RULE_KEYS = {THIS_RULE_KEY};

    /** Supported action attributes (action targets). */
    static final RampartActionTarget[] SUPPORTED_ACTION_TARGETS = { RampartActionTarget.CONNECTION };


    static final RampartActionAttributeParser2_0.AttributeConfigMapParser ATTRIBUTE_CONFIG_PARSER =
        new RampartActionAttributeParser2_0.AttributeConfigMapParser() {
            //@Override
            public RampartList parseAttributeConfigMap(RampartObject attributeData, RampartActionAttribute attributeType)
                    throws InvalidRampartRuleException {
                return RampartSocketParser2_1.parseActionAttributeConfig(attributeData, attributeType);
            }
        };


    private final Map<String, RampartList> symbolTable;

    public RampartSocketParser2_1(Map<String, RampartList> symbolTable) {
        this.symbolTable = symbolTable;
    }

    //@Override
    public RampartSocketBuilder validate() throws InvalidRampartRuleException {
        return parse(symbolTable);
    }


    public static RampartSocketBuilder parse(Map<String, RampartList> symbolTable) throws InvalidRampartRuleException {
        final RampartString ruleName = RampartRuleNameParser.getRuleName(symbolTable, THIS_RULE_KEY);
        RuleStructureValidator.validateDeclarations(
            ruleName, symbolTable,
            THIS_RULE_KEYS,
            SUPPORTED_ACTION_KEYS,
            RampartSocketOperationParser.SUPPORTED_KEYS
        );

        final RampartList targetOsList = RampartTargetOsParser.parseTargetOs(symbolTable, THIS_RULE_KEY);
        final RampartSocketOperation socketOperation = RampartSocketOperationParser.parseOperation(symbolTable);
        final RampartAction socketAction =
            RampartActionAttributeParser2_0.parseActionWithOptionalAttribute(
                symbolTable, ruleName,
                SUPPORTED_ACTIONS,
                ATTRIBUTE_CONFIG_PARSER, SUPPORTED_ACTION_TARGETS
            );

        crossValidate(socketOperation, socketAction);
        return new RampartSocketBuilder()
                .addRuleName(ruleName)
                .addSocketOperation(socketOperation)
                .addTargetOSList(targetOsList)
                .addAction(socketAction);
    }


    /**
     * Validates the shape of the action attribute and its original specification and returns
     * value that should be used as an attribute.
     */
    static RampartList parseActionAttributeConfig(RampartObject targetObjValue, RampartActionAttribute attributeType)
            throws InvalidRampartRuleException {
        if (!(targetObjValue instanceof RampartConstant)) {
            throw new InvalidRampartRuleException(
                    "action target attribute \"" + attributeType + "\" must be a constant");
        }
        return RampartList.EMPTY;
    }

    /**
     * CrossValidate means to check if there are incompatible configurations between declarations.
     * We need to parse the configuration first and then check for compatibilities.
     *
     * The crossValidate method for RampartSocket is much lighter than RampartHttp for instance,
     * because there are more cases of incompatibilities to check between high level declarations.
     *
     * In this case,
     * accept(“192.168.1.1:8080”) is incompatible with protect(connection: secure),
     * equally
     * accept("example.com:80") is incompatible with protect(connection: secure).
     *
     * And the same for hostnames. So we need to account for this when a hostname is configured.
     * @param operation
     * @param action
     * @throws InvalidRampartRuleException
     */
    static void crossValidate(RampartSocketOperation operation, RampartAction action) throws InvalidRampartRuleException {
        // right now, incompatibilities are only possible for TLS-Upgrade and Connection-Secure rules,
        // both of them use RampartActionWithAttribute
        if (!(action instanceof RampartActionWithAttribute)) {
            return;
        }

        RampartActionTarget actionTarget = ((RampartActionWithAttribute) action).getTarget();
        if (!operation.getOperationName().equals(ACCEPT_KEY)) {
            throw new InvalidRampartRuleException("\"" + operation.getOperationName() + "\" is not supported with action target \"" + actionTarget + "\"");
        }
        if (RampartPrimitives.toJavaBoolean(operation.getTargetAddress(RampartSocketType.SERVER).hasHostname())) {
            throw new InvalidRampartRuleException(
                    "cannot configure action target \"" + actionTarget + "\" using hostname, value provided \"" +
                            operation.getTargetAddress(RampartSocketType.SERVER).getHostname() + "\"");
        }
        // Only wildcards are valid for TLS-Upgrade and Connection-Secure
        RampartString ipAddress = operation.getTargetAddress(RampartSocketType.SERVER).getIpAddress();
        if (!IPV4_WILDCARD.equals(ipAddress)
                && !IPV6_WILDCARD.equals(ipAddress)) {
            throw new InvalidRampartRuleException(
                    "cannot configure action target \"" + actionTarget + "\" with ip address \"" + ipAddress + "\"");
        }
    }

}
