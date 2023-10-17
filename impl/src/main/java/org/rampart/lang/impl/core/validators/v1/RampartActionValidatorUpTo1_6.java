package org.rampart.lang.impl.core.validators.v1;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;

import org.rampart.lang.api.*;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.api.core.RampartSeverity;
import org.rampart.lang.impl.core.RampartActionImpl;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;

/**
 * Class to validate an RampartAction
 * Eg.
 *      action(protect: "informative log message..", severity: 9)
 */
@Deprecated
public class RampartActionValidatorUpTo1_6 {
    private final RampartList actionValueList;

    public RampartActionValidatorUpTo1_6(RampartObject actionValueObject) {
        this.actionValueList = (RampartList)actionValueObject;
    }

    public RampartAction validateRampartAction() throws InvalidRampartRuleException {
        RampartObject actionTypeObject = actionValueList.getFirst();
        if (!(actionTypeObject instanceof RampartNamedValue)) {
            throw new InvalidRampartRuleException("action declaration must begin with an action-type:"
                    + " \"log message\" key pair");
        }

        RampartNamedValue actionNamedValue = (RampartNamedValue) actionTypeObject;
        RampartActionType actionType = validateActionType(newRampartString(actionNamedValue.getName().toString()));

        RampartString logMessage = validateLogMessage(actionNamedValue.getRampartObject());
        RampartBoolean shouldLogMessage = shouldLogMessage(logMessage);

        RampartObject severityObject = RampartInterpreterUtils
                .findRampartNamedValue(SEVERITY_KEY, actionValueList);
        RampartSeverity severity = validateSeverityValue(severityObject);
        return new RampartActionImpl(actionType, logMessage, severity, shouldLogMessage);
    }

    /**
     * Validates and returns the type specified in the rule is a supported action type
     * @param actionTypeString type to be validated
     * @return RampartActionType corresponding to the given String value
     * @throws InvalidRampartRuleException when an invalid type is specified
     */
    protected RampartActionType validateActionType(RampartString actionTypeString) throws InvalidRampartRuleException {
        RampartActionType value = RampartActionType.fromConstant(newRampartConstant(actionTypeString.toString()));
        if (RampartActionType.UNKNOWN.equals(value)) {
            throw new InvalidRampartRuleException(invalidRampartActionTypeMessage(actionTypeString));
        }
        return value;
    }

    public static String invalidRampartActionTypeMessage(RampartString actionTypeString) {
        return "unknown rampart action type specified: " + actionTypeString;
    }

    /**
     * Validates the log message passed to the Rampart action
     * @param logMessage log message to be validated
     * @return logMessage as an RampartString
     * @throws InvalidRampartRuleException when log message is null or of the incorrect type
     */
    private RampartString validateLogMessage(RampartObject logMessage) throws InvalidRampartRuleException {
        if (!(logMessage instanceof RampartString)) {
            throw new InvalidRampartRuleException("action type must supply a log message");
        }
        return (RampartString) logMessage;
    }

    /**
     * Determines whether the given log message should have an entry in the CEF log.
     * An empty String log message will not have an entry in the CEF log.
     * @param logMessage message to tested
     * @return whether the log message should be written to the log file or not.
     */
    private RampartBoolean shouldLogMessage(RampartString logMessage) {
        return logMessage.isEmpty().negate();
    }

    /**
     * Validates and creates an RampartSeverity object representing the severity specified.
     * @param severityObject object representing the severity in the symbol table
     * @return RampartSeverity instance representing the value specified
     * @throws InvalidRampartRuleException when severity is specified as an RampartInteger outside permitted bounds.
     */
    private RampartSeverity validateSeverityValue(RampartObject severityObject) throws InvalidRampartRuleException {
        if (severityObject instanceof RampartString) {
            return RampartSeverity.fromConstant(newRampartConstant(severityObject.toString()));
        }
        if (severityObject instanceof RampartInteger) {
            RampartInteger severityInteger = (RampartInteger) severityObject;
            if (severityInteger.isLessThan(newRampartInteger(0)) == RampartBoolean.TRUE
                    || severityInteger.isGreaterThan(newRampartInteger(10)) == RampartBoolean.TRUE) {
                throw new InvalidRampartRuleException("severity must be in the range of 0-10 (inclusive)");
            }
            return toRampartSeverity(severityInteger);
        }
        // Default if severity is not defined or malformed
        return RampartSeverity.UNKNOWN;
    }
}
