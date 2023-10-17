package org.rampart.lang.impl.sanitization;

import static org.rampart.lang.api.constants.RampartSanitizationConstants.*;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.impl.utils.ObjectUtils;
import org.rampart.lang.impl.utils.Optional;
import org.rampart.lang.java.RampartPrimitives;

public class RampartUndeterminedImpl2_8 extends RampartUndeterminedImpl {
    private final Optional<RampartBoolean> shouldLog;

    public RampartUndeterminedImpl2_8(final RampartBoolean isSafe, final Optional<RampartBoolean> shouldLog) {
        super(isSafe);
        this.shouldLog = shouldLog;
        hashCode = ObjectUtils.hash(super.isSafe, this.shouldLog);
    }

    /**
     * Dedicated constructor for the RampartUndetermindeBuilder, which has not access to the Optional.
     * @param isSafe mandatory
     * @param shouldLog optional, you can pass null here
     */
    public RampartUndeterminedImpl2_8(final RampartBoolean isSafe, final /*Optional*/ RampartBoolean shouldLog) {
        this(isSafe, Optional.ofNullable(shouldLog));
    }

    // @Override
    public RampartBoolean shouldLog() {
        return shouldLog.orElse(RampartBoolean.TRUE);
    }

    @Override
    public String toString() {
        return "\t" + UNDETERMINED_KEY + "("
                + VALUES_KEY + ": " + isSafeToString()
                + shouldLogToString() +")" + LINE_SEPARATOR;
    }

    protected String shouldLogToString() {
        if (!shouldLog.isPresent()) {
            return "";
        }
        return ", " + LOGGING_KEY + ": "
                + (RampartPrimitives.toJavaBoolean(shouldLog()) ? ON_KEY.toString() : OFF_KEY.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RampartUndeterminedImpl2_8)) {
            return false;
        }
        RampartUndeterminedImpl2_8 that = (RampartUndeterminedImpl2_8) obj;
        return isSafe.equals(that.isSafe)
                && shouldLog.equals(that.shouldLog);
    }

}
