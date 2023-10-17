package org.rampart.lang.api.core;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartObject;

/**
 * Enumeration of Rampart rule types
 * @since 1.4
 */
public enum RampartRuleType implements RampartObject {
    DNS("dns"),
    FILESYSTEM("filesystem"),
    HTTP("http"),
    LIBRARY("library"),
    MARSHAL("marshal"),
    PATCH("patch"),
    PROCESS("process"),
    SANITIZATION("sanitization"),
    SOCKET("socket"),
    SQL("sql"),
    API("api"),
    UNKNOWN;

    private final RampartConstant name;

    RampartRuleType(final String name) {
        this.name = new RampartConstant() {
            @Override
            public String toString() {
                return name;
            }
        };
    }

    RampartRuleType() {
        this(null);
    }

    /**
     * Getter for the name RampartString of the instance
     * @return 'name' of the instance
     * Note: Will return null in the case of an UNKNOWN type
     */
    public RampartConstant getName() {
        return name;
    }

    public static RampartRuleType fromConstant(RampartConstant rampartRuleType) {
        for (RampartRuleType ruleType: RampartRuleType.values()) {
            if (ruleType.name != null && ruleType.name.equals(rampartRuleType)) {
                return ruleType;
            }
        }
        throw new IllegalArgumentException("unknown rampart rule type specified: " + rampartRuleType);
    }

    @Override
    public String toString() {
        return name.toString();
    }
}
