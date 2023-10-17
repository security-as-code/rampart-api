package org.rampart.lang.api.core;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;

public interface RampartAction extends RampartObject {
    RampartActionType getActionType();

    RampartString getLogMessage();

    RampartSeverity getSeverity();

    RampartBoolean shouldLog();

    RampartBoolean hasStacktrace();

    /**
     * Initially we allow only a single value: "full",
     * but this might be extended in future so RampartString
     * is better then RampartBoolean.
     * @return the value configured, or null when stacktrace attribute is not set
     * @since 2.3
     */
    RampartString getStacktrace();

}
