package org.rampart.lang.api.core;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartObject;

/**
 * Enumeration of Rampart Action Types
 * @since 1.4
 */
public enum RampartActionType implements RampartObject {
    /**
     * NB: The order of the elements below is important.
     * Do not change lightly!
     * Order is: most restrictive to least restrictive.
     * PROTECT - CORRECT - ALLOW - DETECT
     * Invocations of 'compareTo' are dependant on the ordinal values of the elements.
     */
    PROTECT("protect"),
    CORRECT("correct"),
    ALLOW("allow"),
    DETECT("detect"),
    UNKNOWN("unknown");

    private final RampartConstant name;

    RampartActionType(final String name) {
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

    /**
     * Creates an RampartActionType from a passed value
     * @param actionType value to create RampartActionType from
     * @return RampartActionType representation of the passed value,
     *         or UNKNOWN if the value does not match any of the standard values
     */
    public static RampartActionType fromConstant(RampartConstant actionType) {
        if (actionType != null) {
            for (RampartActionType rampartActionType : RampartActionType.values()) {
                if (rampartActionType.name.equals(actionType)) {
                    return rampartActionType;
                }
            }
        }
        return UNKNOWN;
    }

    @Override
    public String toString() {
        return name.toString();
    }
}
