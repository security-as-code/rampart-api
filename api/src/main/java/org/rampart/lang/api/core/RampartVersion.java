package org.rampart.lang.api.core;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartInteger;
import org.rampart.lang.api.RampartObject;

/**
 * Class to represent an Rampart App version
 * Eg.
 * app("appName"):
 * requires(version : "RAMPART/2.10)
 */
public abstract class RampartVersion implements RampartObject {
    /**
     * Compares this RampartVersion with the specified RampartVersion.
     * @param other RampartVersion to which this RampartVersion is to be compared.
     * @return -1, 0 or 1 as this RampartVersion is numerically less than, equal
     * to, or greater than other.
     */
    private int compareTo(RampartVersion other) {
        if (getMajor().equals(other.getMajor()) && getMinor().equals(other.getMinor())) {
            return 0;
        } else if (getMajor().isGreaterThan(other.getMajor()) == RampartBoolean.TRUE) {
            return 1;
        } else if (getMajor().isLessThan(other.getMajor()) == RampartBoolean.TRUE) {
            return -1;
        } else if (getMinor().isGreaterThan(other.getMinor()) == RampartBoolean.TRUE) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * Creates an RampartConstant representation of the version
     * @return RampartConstant wrapper around toString()
     */
    public abstract RampartConstant asConstant();

    public abstract RampartInteger getMajor();

    public abstract RampartInteger getMinor();

    /**
     * Checks that `this` version is within startVersion (inclusive) and endVersion (inclusive).
     *
     * @return true if version is within the range, false otherwise
     */
    public RampartBoolean isWithinRange(RampartVersion startVersion, RampartVersion endVersion) {
        return (compareTo(startVersion) != -1
                    && compareTo(endVersion) != 1) ? RampartBoolean.TRUE : RampartBoolean.FALSE;
    }

    /**
     * Checks if `this` version is greater or equal than the version passed as a parameter.
     * @param version to compare with
     * @return true if `this` version is greater or equal, false otherwise
     */
    public RampartBoolean greaterOrEqualThan(RampartVersion version) {
        return (compareTo(version) != -1) ? RampartBoolean.TRUE : RampartBoolean.FALSE;
    }
}
