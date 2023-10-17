package org.rampart.lang.api;

import org.rampart.lang.impl.utils.ObjectUtils;

public class RampartConstant implements RampartObject {
    private final RampartString rampartString;

    {
        final String constantString = toString();
        rampartString = new RampartString() {
            @Override
            public String toString() {
                return constantString;
            }
        };
    }

    public RampartString asRampartString() {
        return rampartString;
    }

    @Override
    public int hashCode() {
        return rampartString.hashCode();
    }

    @Override
    public String toString() {
        return rampartString.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartConstant)) {
            return false;
        }
        RampartConstant otherConstant = (RampartConstant) other;
        return ObjectUtils.equals(this.rampartString, otherConstant.rampartString);
    }
}
