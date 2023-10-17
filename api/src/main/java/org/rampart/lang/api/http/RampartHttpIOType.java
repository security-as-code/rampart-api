package org.rampart.lang.api.http;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;

/**
 * Enumeration of Http IO types
 * @since 1.4
 */
public enum RampartHttpIOType implements RampartObject {
    REQUEST("request"),
    RESPONSE("response");

    private final RampartConstant name;

    RampartHttpIOType(final String name) {
        this.name = new RampartConstant() {
            @Override
            public String toString() {
                return name;
            }
        };
    }

    public RampartConstant getName() {
        return name;
    }

    public static RampartHttpIOType fromRampartString(RampartString httpIOType) {
        for (RampartHttpIOType type: RampartHttpIOType.values()) {
            if (type.name.asRampartString().equals(httpIOType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("unknown rampart http IO type specified: " + httpIOType);
    }

    @Override
    public String toString() {
        return name.toString();
    }
}
