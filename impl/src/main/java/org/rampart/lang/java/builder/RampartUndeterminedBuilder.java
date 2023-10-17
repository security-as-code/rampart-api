package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.sanitization.RampartUndetermined;
import org.rampart.lang.impl.sanitization.RampartUndeterminedImpl2_8;

public class RampartUndeterminedBuilder implements RampartObjectBuilder<RampartUndetermined> {
    private RampartBoolean isSafe;
    private RampartBoolean shouldLog = null;

    public RampartUndetermined createRampartObject() {
        return new RampartUndeterminedImpl2_8(isSafe, shouldLog);
    }

    /**
     * Mandatory setter for if the undetermined values are considered to be safe.
     * @param isSafe flag, should not be null
     */
    public RampartUndeterminedBuilder setIsSafe(final RampartBoolean isSafe) {
        this.isSafe = isSafe;
        return this;
    }

    /**
     * Optional setter for if the undetermined values should be logged.
     * @param shouldLog flag, should not be null
     */
    public RampartUndeterminedBuilder setShouldLog(final RampartBoolean shouldLog) {
        this.shouldLog = shouldLog;
        return this;
    }

}
