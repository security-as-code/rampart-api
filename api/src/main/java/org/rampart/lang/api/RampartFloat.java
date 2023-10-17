package org.rampart.lang.api;

public class RampartFloat implements RampartObject {
    private final float rampartFloat;

    {
        rampartFloat = Float.parseFloat(toString());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartFloat)) {
            return false;
        }
        RampartFloat otherFloat = (RampartFloat) other;
        return Float.compare(rampartFloat, otherFloat.rampartFloat) == 0;
    }

    @Override
    public int hashCode() {
        // Taken from Java 8 RI
        return Float.floatToIntBits(rampartFloat);
    }
}
