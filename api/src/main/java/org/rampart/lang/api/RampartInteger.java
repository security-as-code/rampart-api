package org.rampart.lang.api;

public class RampartInteger implements RampartObject {

    private final int value;

    {
        value = Integer.parseInt(toString());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartInteger)) {
            return false;
        }
        RampartInteger otherInt = (RampartInteger) other;
        return value == otherInt.value;
    }

    @Override
    public int hashCode() {
        // Taken from Java 8 RI
        return value;
    }

    public RampartBoolean isGreaterThan(RampartInteger other) {
        if (other == null) {
            return RampartBoolean.FALSE;
        }
        return value > other.value ? RampartBoolean.TRUE : RampartBoolean.FALSE;
    }

    public RampartBoolean isLessThan(RampartInteger other) {
        if (other == null) {
            return RampartBoolean.FALSE;
        }
        return value < other.value ? RampartBoolean.TRUE : RampartBoolean.FALSE;
    }

    public RampartBoolean positiveNumber() {
        return value > 0 ? RampartBoolean.TRUE : RampartBoolean.FALSE;
    }
}
