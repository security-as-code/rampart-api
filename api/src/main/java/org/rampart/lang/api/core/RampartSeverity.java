package org.rampart.lang.api.core;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartObject;

/**
 * Enumeration of severity types for a CEF log message
 * @since 1.4
 */
public enum RampartSeverity implements RampartObject {
    /**
     * NB: The order of the elements below is important.
     * Do not change lightly!
     * Order is: most restrictive to least restrictive.
     * Invocations of 'compareTo' are dependant on the ordinal values of the elements.
     */
    VERY_HIGH("Very-High"),
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low"),
    UNKNOWN("Unknown");

    private final RampartConstant name;

    RampartSeverity(final String name) {
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
     * Creates an RampartSeverity instance from a String Value
     * LOW: "low"
     * MED: "med"
     * HIGH: "high"
     * VERY_HIGH: "very-high"
     * UNKNOWN: Anything else
     * Note: This function is case INsensitive
     * @param severity String value to create RampartSeverity from
     * @return RampartSeverity representing the String value passed
     */
    public static RampartSeverity fromConstant(RampartConstant severity) {
        if (severity != null) {
            for (RampartSeverity rampartSeverity : RampartSeverity.values()) {
                if (rampartSeverity.name.toString().equalsIgnoreCase(severity.toString())) {
                    return rampartSeverity;
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
