package org.rampart.lang.impl.core.validators.v2;

import java.util.Map;

import org.rampart.lang.api.*;
import org.rampart.lang.api.core.*;
import org.rampart.lang.impl.core.RampartActionImpl;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;

/**
 * Class to validate an RampartAction for RAMPART 2.3 and above
 * Eg.
 *      protect(message: "informative log message..", severity: 9, stacktrace: "full")
 */
public abstract class RampartActionValidator2_3Plus extends RampartActionValidator2_0Plus {

    public RampartActionValidator2_3Plus(Map<String, RampartList> visitorSymbolTable, RampartRuleType ruleType) {
        super(visitorSymbolTable, ruleType);
    }

    public RampartAction validateRampartAction() throws InvalidRampartRuleException {
        RampartActionType actionType = lookupForAction();
        RampartList actionList = visitorSymbolTable.get(actionType.getName().toString());
        RampartString logMessage = null;
        RampartSeverity severity = null;
        RampartString stacktrace = null;
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
                } else if(STACKTRACE_KEY.equals(paramNamedValue.getName())) {
                    stacktrace = validateStacktraceValue(paramNamedValue.getRampartObject());
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
                shouldLogMessage(logMessage), stacktrace);
    }

    @Override
    protected boolean throwOnUnidentifiedParameter() {
        return true;
    }

    /**
     * Validates stacktrace specified.
     *
     * @param stacktrace object representing the stacktrace in the symbol table
     * @return RampartString instance representing the value specified
     *         or null if the value is not specified
     * @throws InvalidRampartRuleException when stacktrace specified is not an RampartString
     */
    protected RampartString validateStacktraceValue(RampartObject stacktrace)
            throws InvalidRampartRuleException {
        if (stacktrace == null) {
            return null;
        }
        if (!(stacktrace instanceof RampartString)) {
            throw new InvalidRampartRuleException("value for the stacktrace must be a string literal");
        }
        return (RampartString) stacktrace;
    }
}
