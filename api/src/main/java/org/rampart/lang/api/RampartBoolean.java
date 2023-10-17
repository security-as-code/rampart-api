package org.rampart.lang.api;

public class RampartBoolean implements RampartObject {
    public static final RampartBoolean TRUE = new RampartBoolean(true);
    public static final RampartBoolean FALSE = new RampartBoolean(false);

    private final boolean rampartBoolean;

    private RampartBoolean(boolean rampartBoolean) {
        this.rampartBoolean = rampartBoolean;
    }

    @Override
    public String toString() {
        return Boolean.toString(rampartBoolean);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartBoolean)) {
            return false;
        }
        RampartBoolean otherBool = (RampartBoolean) other;
        return rampartBoolean == otherBool.rampartBoolean;
    }

    @Override
    public int hashCode() {
        // Taken from Java 8 RI
        return rampartBoolean ? 1231 : 1237;
    }

    public RampartBoolean negate() {
        return this == RampartBoolean.TRUE ? RampartBoolean.FALSE : RampartBoolean.TRUE;
    }
}
