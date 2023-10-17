package org.rampart.lang.impl.core;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.api.core.RampartSeverity;
import org.rampart.lang.impl.utils.ObjectUtils;

/**
 * Class to model an RampartAction
 * Eg.
 *  action(protect: "informative log message", severity: 5)
 */
public class RampartActionImpl implements RampartAction {

    protected final RampartActionType actionType;
    protected final RampartString logMessage;
    protected final RampartSeverity severity;
    protected final RampartBoolean shouldLog;
    protected final RampartString stacktrace;
    private final String toStringValue;
    private final int hashCode;

    @Deprecated // since 2.3, when optional named value target 'stacktrace' was added to the RampartAction
    public RampartActionImpl(RampartActionType actionType, RampartString logMessage, RampartSeverity severity,
                             RampartBoolean shouldLog) {
        this(actionType, logMessage, severity, shouldLog, null);
    }

    public RampartActionImpl(RampartActionType actionType, RampartString logMessage, RampartSeverity severity,
                             RampartBoolean shouldLog, RampartString stacktrace) {
        this.actionType = actionType;
        this.logMessage = logMessage;
        this.severity = severity;
        this.shouldLog = shouldLog;
        this.stacktrace = stacktrace;
        this.toStringValue = createStringRepresentation();
        this.hashCode = ObjectUtils.hash(actionType, logMessage, severity, shouldLog);
    }

    // @Override
    public RampartActionType getActionType() {
        return actionType;
    }

    // @Override
    public RampartString getLogMessage() {
        return logMessage;
    }

    // @Override
    public RampartSeverity getSeverity() {
        return severity;
    }

    // @Override
    public RampartBoolean shouldLog() {
        return shouldLog;
    }

    // @Override
    public RampartBoolean hasStacktrace() {
        return stacktrace == null ? RampartBoolean.FALSE : RampartBoolean.TRUE;
    }

    // @Override
    public RampartString getStacktrace() {
        return stacktrace;
    }

    @Override
    public String toString() {
        return toStringValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartActionImpl)) {
            return false;
        }
        RampartActionImpl otherAction = (RampartActionImpl) other;
        return shouldLog == otherAction.shouldLog
                && ObjectUtils.equals(actionType, otherAction.actionType)
                && ObjectUtils.equals(logMessage, otherAction.logMessage)
                && ObjectUtils.equals(severity, otherAction.severity)
                && ObjectUtils.equals(stacktrace, otherAction.stacktrace);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    protected String stacktracePart() {
        return (stacktrace == null ? "" : ", " + STACKTRACE_KEY + ": \"" + stacktrace + "\"");
    }

    private String createStringRepresentation() {
        String messagePart = (shouldLog == RampartBoolean.FALSE ? "" : MESSAGE_KEY + ": " + logMessage.formatted() + ", ");
        return actionType.getName() + "(" + messagePart
                + SEVERITY_KEY + ": " + severity
                + stacktracePart() + ")";
    }

}
