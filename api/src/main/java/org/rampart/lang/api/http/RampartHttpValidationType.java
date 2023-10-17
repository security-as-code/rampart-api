package org.rampart.lang.api.http;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;

/**
 * Enumeration of Rampart Http input validation types
 * @since 1.4
 */
public enum RampartHttpValidationType implements RampartObject {
    @Deprecated
    HTTP_COOKIE("cookie"),
    @Deprecated
    HTTP_HEADER("header"),
    @Deprecated
    HTTP_PARAMETER("parameter"),
    @Deprecated
    CSRF(RampartHttpFeaturePattern.CSRF.getDeclarationTerm()),
    COOKIES("cookies"),
    HEADERS("headers"),
    PARAMETERS("parameters"),
    METHOD("method"),
    REQUEST(RampartHttpIOType.REQUEST.getName());

    final RampartConstant name;

    RampartHttpValidationType(final String name) {
        this.name = new RampartConstant() {
            @Override
            public String toString() {
                return name;
            }
        };
    }

    RampartHttpValidationType(RampartConstant name) {
        this.name = name;
    }

    public RampartConstant getName() {
        return name;
    }

    @Deprecated
    public static RampartHttpValidationType fromRampartString(RampartString inputValidationType) {
        for (RampartHttpValidationType validationType : RampartHttpValidationType.values()) {
            if (validationType.name.asRampartString().equals(inputValidationType)) {
                return validationType;
            }
        }
        return null;
    }

    public static RampartHttpValidationType fromConstant(RampartConstant inputValidationType) {
        return fromRampartString(inputValidationType.asRampartString());
    }

    @Override
    public String toString() {
        return name.toString();
    }
}
