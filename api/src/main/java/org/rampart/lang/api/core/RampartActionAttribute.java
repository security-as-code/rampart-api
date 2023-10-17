package org.rampart.lang.api.core;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartObject;

public enum RampartActionAttribute implements RampartObject {

    REGENERATE_ID("regenerate-id"),
    SET_HEADER("set-header"),
    NEW_RESPONSE("new-response"),
    SECURE("secure"),
    UPGRADE_TLS("upgrade-tls");

    private final RampartConstant name;

    RampartActionAttribute(final String name) {
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

    public static RampartActionAttribute fromConstant(RampartConstant attribute) {
        for (RampartActionAttribute actionAttribute : RampartActionAttribute.values()) {
            if (actionAttribute.name.equals(attribute)) {
                return actionAttribute;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name.toString();
    }
}
