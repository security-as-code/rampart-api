package org.rampart.lang.impl.sanitization;

import static org.rampart.lang.api.constants.RampartSanitizationConstants.*;
import static org.rampart.lang.java.RampartPrimitives.toJavaBoolean;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.sanitization.RampartUndetermined;
import org.rampart.lang.impl.utils.ObjectUtils;

public class RampartUndeterminedImpl implements RampartUndetermined {

    protected int hashCode;
    protected final RampartBoolean isSafe;

    public RampartUndeterminedImpl(RampartBoolean isSafe) {
        this.isSafe = isSafe;
        hashCode = ObjectUtils.hash(this.isSafe);
    }

    // @Override
    public RampartBoolean isSafe() {
        return isSafe;
    }

    // @since 2.8
    public RampartBoolean shouldLog() {
        return RampartBoolean.TRUE;
    }

    @Override
    public String toString() {
        return "\t" + UNDETERMINED_KEY + "(" + VALUES_KEY + ": " + isSafeToString() + ")" + LINE_SEPARATOR;
    }

    protected String isSafeToString() {
        return toJavaBoolean(this.isSafe) ? SAFE_KEY.toString() : UNSAFE_KEY.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RampartUndeterminedImpl)) {
            return false;
        }
        RampartUndeterminedImpl that = (RampartUndeterminedImpl) obj;
        return isSafe.equals(that.isSafe);
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

}
