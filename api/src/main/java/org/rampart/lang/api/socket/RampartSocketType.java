package org.rampart.lang.api.socket;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartObject;

public enum RampartSocketType implements RampartObject {

    CLIENT("client"),
    SERVER("server");

    private final RampartConstant name;

    RampartSocketType(final String name) {
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

    @Override
    public String toString() {
        return name.toString();
    }

    public static RampartSocketType fromConstant(RampartConstant socType) {
        for (RampartSocketType type : values()) {
            if (type.getName().equals(socType)) {
                return type;
            }
        }
        return null;
    }
}
