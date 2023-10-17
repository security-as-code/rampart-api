package org.rampart.lang.api.patch;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartObject;

/**
 * Enumeration of the possible Rampart patch location types.
 * @since 1.0
 */
public enum RampartPatchType implements RampartObject {
    CALL("call"),
    CALLSITE("callsite"),
    CALLRETURN("callreturn"),
    ENTRY("entry"),
    ERROR("error"),
    EXIT("exit"),
    INSTRUCTION("instruction"),
    LINE("line"),
    READSITE("readsite"),
    READ("read"),
    READRETURN("readreturn"),
    WRITESITE("writesite"),
    WRITE("write"),
    WRITERETURN("writereturn");

    RampartConstant name;

    RampartPatchType(final String name) {
        this.name = new RampartConstant() {
            @Override
            public String toString() {
                return name;
            }
        };
    }

    public static RampartPatchType fromRampartString(RampartConstant name) {
        for (RampartPatchType rampartPatchType : values()) {
            if (rampartPatchType.name.equals(name)) {
                return rampartPatchType;
            }
        }
        throw new IllegalArgumentException("invalid location specifier: \"" + name + "\"");
    }

    public RampartConstant getName() {
        return name;
    }
}
