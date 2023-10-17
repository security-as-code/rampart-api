package org.rampart.lang.impl.core;

import static org.rampart.lang.java.RampartPrimitives.newRampartInteger;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartInteger;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.core.RampartVersion;
import org.rampart.lang.impl.utils.ObjectUtils;
import org.rampart.lang.java.RampartPrimitives;

public class RampartVersionImpl extends RampartVersion {

    public static final RampartVersionImpl v1_0 = new RampartVersionImpl(
            newRampartInteger(1), newRampartInteger(0));
    public static final RampartVersionImpl v1_1 = new RampartVersionImpl(
            newRampartInteger(1), newRampartInteger(1));
    public static final RampartVersionImpl v1_2 = new RampartVersionImpl(
            newRampartInteger(1), newRampartInteger(2));
    public static final RampartVersionImpl v1_3 = new RampartVersionImpl(
            newRampartInteger(1), newRampartInteger(3));
    public static final RampartVersionImpl v1_4 = new RampartVersionImpl(
            newRampartInteger(1), newRampartInteger(4));
    public static final RampartVersionImpl v1_5 = new RampartVersionImpl(
            newRampartInteger(1), newRampartInteger(5));
    public static final RampartVersionImpl v1_6 = new RampartVersionImpl(
            newRampartInteger(1), newRampartInteger(6));
    public static final RampartVersionImpl v2_0 = new RampartVersionImpl(
            newRampartInteger(2), newRampartInteger(0));
    public static final RampartVersionImpl v2_1 = new RampartVersionImpl(
            newRampartInteger(2), newRampartInteger(1));
    public static final RampartVersionImpl v2_2 = new RampartVersionImpl(
            newRampartInteger(2), newRampartInteger(2));
    public static final RampartVersionImpl v2_3 = new RampartVersionImpl(
            newRampartInteger(2), newRampartInteger(3));
    public static final RampartVersionImpl v2_4 = new RampartVersionImpl(
            newRampartInteger(2), newRampartInteger(4));
    public static final RampartVersionImpl v2_5 = new RampartVersionImpl(
            newRampartInteger(2), newRampartInteger(5));
    public static final RampartVersionImpl v2_6 = new RampartVersionImpl(
            newRampartInteger(2), newRampartInteger(6));
    public static final RampartVersionImpl v2_7 = new RampartVersionImpl(
            newRampartInteger(2), newRampartInteger(7));
    public static final RampartVersionImpl v2_8 = new RampartVersionImpl(
            newRampartInteger(2), newRampartInteger(8));
    public static final RampartVersionImpl v2_9 = new RampartVersionImpl(
            newRampartInteger(2), newRampartInteger(9));
    public static final RampartVersionImpl v2_10 = new RampartVersionImpl(
            newRampartInteger(2), newRampartInteger(10));

    public static final RampartList AVAILABLE_VERSIONS = newRampartList(
            v1_0, v1_1, v1_2, v1_3, v1_4, v1_5, v1_6,
            v2_0, v2_1, v2_2, v2_3, v2_4, v2_5, v2_6, v2_7, v2_8, v2_9, v2_10);

    private final RampartInteger major;
    private final RampartInteger minor;

    private RampartVersionImpl(RampartInteger major, RampartInteger minor) {
        this.major = major;
        this.minor = minor;
    }

    // @Override
    public RampartConstant asConstant() {
        return RampartPrimitives.newRampartConstant(toString());
    }

    // @Override
    public RampartInteger getMajor() {
        return major;
    }

    // @Override
    public RampartInteger getMinor() {
        return minor;
    }

    public static RampartVersionImpl valueOf(int major, int minor) {
        RampartObjectIterator it = AVAILABLE_VERSIONS.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartVersionImpl version = (RampartVersionImpl) it.next();
            if (RampartPrimitives.toJavaInt(version.getMajor()) == major
                    && RampartPrimitives.toJavaInt(version.getMinor()) == minor) {
                return version;
            }
        }
        throw new IllegalArgumentException("invalid version " + major + "." + minor);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartVersionImpl)) {
            return false;
        }
        RampartVersionImpl otherVersion = (RampartVersionImpl) other;
        return ObjectUtils.equals(major, otherVersion.major) &&
                ObjectUtils.equals(minor, otherVersion.minor);
    }

    @Override
    public int hashCode() {
        return ObjectUtils.hash(major, minor);
    }

    @Override
    public String toString() {
        return major + "." + minor;
    }
}
