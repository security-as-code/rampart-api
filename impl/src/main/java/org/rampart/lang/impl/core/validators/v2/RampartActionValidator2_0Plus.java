package org.rampart.lang.impl.core.validators.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartInteger;

import java.util.Map;

import org.rampart.lang.api.*;
import org.rampart.lang.api.core.*;
import org.rampart.lang.impl.core.RampartActionImpl;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.RampartActionValidator;
import org.rampart.lang.impl.core.validators.FirstClassRuleObjectValidator;
import org.rampart.lang.java.RampartPrimitives;

/**
 * Class to validate an RampartAction
 * Eg.
 *      protect(message: "informative log message..", severity: 9)
 */
public abstract class RampartActionValidator2_0Plus implements RampartActionValidator, FirstClassRuleObjectValidator {
    protected final Map<String, RampartList> visitorSymbolTable;
    private final RampartRuleType ruleType;

    public RampartActionValidator2_0Plus(Map<String, RampartList> visitorSymbolTable, RampartRuleType ruleType) {
        this.visitorSymbolTable = visitorSymbolTable;
        this.ruleType = ruleType;
    }

    public RampartAction validateRampartAction() throws InvalidRampartRuleException {
        RampartActionType actionType = lookupForAction();
        RampartList actionList = visitorSymbolTable.get(actionType.getName().toString());
        RampartString logMessage = null;
        RampartSeverity severity = null;
        RampartObjectIterator it = actionList.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject parameter = it.next();
            if (parameter instanceof RampartNamedValue) {
                RampartNamedValue paramNamedValue = (RampartNamedValue) parameter;
                if (MESSAGE_KEY.equals(paramNamedValue.getName())) {
                    logMessage = validateLogMessage(paramNamedValue.getRampartObject());
                    continue;
                } else if(SEVERITY_KEY.equals(paramNamedValue.getName())) {
                    severity = validateSeverityValue(paramNamedValue.getRampartObject());
                    continue;
                }
            }
            if (throwOnUnidentifiedParameter()) {
                throwOnUnsupportedActionParameter(parameter, actionType);
            }
        }
        if (logMessage == null && actionType == RampartActionType.DETECT) {
            throw new InvalidRampartRuleException("\"" + DETECT_KEY + "\" action must declare message");
        }
        return new RampartActionImpl(
                actionType,
                logMessage,
                severity == null ? RampartSeverity.UNKNOWN : severity,
                shouldLogMessage(logMessage));
    }

    static void throwOnUnsupportedActionParameter(RampartObject parameter, RampartActionType actionType)
            throws InvalidRampartRuleException {
        throw new InvalidRampartRuleException(
                "parameter \"" + parameter + "\" to the action \"" + actionType + "\" is not supported");
    }

    protected boolean throwOnUnidentifiedParameter() {
        return true;
    }

    /**
     * Validates the log message passed to the Rampart action
     * @param logMessageValue log message to be validated
     * @return logMessage as an RampartString
     * @throws InvalidRampartRuleException when log message is null or of the incorrect type
     */
    protected RampartString validateLogMessage(RampartObject logMessageValue)
            throws InvalidRampartRuleException {
        if (logMessageValue != null) {
            if (!(logMessageValue instanceof RampartString)) {
                throw new InvalidRampartRuleException("value for the message must be a string literal");
            }
            return (RampartString) logMessageValue;
        }
        return null;
    }

    /**
     * Determines whether the given log message should have an entry in the CEF log.
     * An empty String log message will not have an entry in the CEF log.
     * @param logMessage message to be tested
     * @return whether the log message should be written to the log file or not.
     */
    protected RampartBoolean shouldLogMessage(RampartString logMessage) {
        return logMessage == null ? RampartBoolean.FALSE : RampartBoolean.TRUE;
    }

    /**
     * Validates and creates an RampartSeverity object representing the severity specified.
     *
     * @param severityObject object representing the severity in the symbol table
     * @return RampartSeverity instance representing the value specified
     * @throws InvalidRampartRuleException when severity is specified as an RampartInteger outside
     *         permitted bounds.
     */
    protected RampartSeverity validateSeverityValue(RampartObject severityObject) throws InvalidRampartRuleException {
        if (severityObject == null) {
            return null;
        }
        if (severityObject instanceof RampartConstant) {
            return RampartSeverity.fromConstant((RampartConstant) severityObject);
        }
        if (severityObject instanceof RampartInteger) {
            RampartInteger severityInteger = (RampartInteger) severityObject;
            if (severityInteger.isLessThan(newRampartInteger(0)) == RampartBoolean.TRUE
                    || severityInteger.isGreaterThan(newRampartInteger(10)) == RampartBoolean.TRUE) {
                throw new InvalidRampartRuleException("\"" + SEVERITY_KEY + "\" must be in the range of 0-10 (inclusive)");
            }
            return RampartPrimitives.toRampartSeverity(severityInteger);
        }
        throw new InvalidRampartRuleException("\"" + SEVERITY_KEY + "\" is malformed, must be an integer or a constant");
    }

    /**
     * Subclass is responsible to lookup for the action declared in the rule depending on what
     * actions that model supports. The implementing class should return the proper validated and
     * supported action.
     *
     * @return The supported action validated by the implementing class
     * @throws InvalidRampartRuleException when an issue occurs during validation
     */
    protected RampartActionType lookupForAction() throws InvalidRampartRuleException {
        RampartActionType actionType = null;
        for (RampartConstant type : allowedKeys()) {
            if (visitorSymbolTable.containsKey(type.toString())) {
                if (actionType != null) {
                    throw new InvalidRampartRuleException("actions \"" + actionType.getName() + "\" and \"" + type
                            + "\" are declared. Declaration of more than one action type is not allowed.");
                }
                actionType = RampartActionType.fromConstant(type);
            }
        }
        if (actionType == null) {
            throw new InvalidRampartRuleException(
                    "RAMPART \"" + ruleType.getName() + "\" action is missing. Must be one of: " + allowedKeys());
        }
        return actionType;
    }

}
