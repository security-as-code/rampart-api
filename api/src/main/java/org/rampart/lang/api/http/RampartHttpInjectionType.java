package org.rampart.lang.api.http;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.impl.utils.ObjectUtils;

public class RampartHttpInjectionType implements RampartObject {
    public static RampartHttpInjectionType HEADERS = new RampartHttpInjectionType(RampartHttpValidationType.HEADERS.getName());
    public static RampartHttpInjectionType COOKIES = new RampartHttpInjectionType(RampartHttpValidationType.COOKIES.getName());

    private static final RampartHttpInjectionType[] VALUES = new RampartHttpInjectionType[] {HEADERS, COOKIES};

    private final RampartConstant name;
    private final int hashCode;

    private RampartHttpInjectionType(RampartConstant name) {
        this.name = name;
        this.hashCode = ObjectUtils.hash(name);
    }

    public RampartBoolean onHeaderInjection() {
        return this == HEADERS ? RampartBoolean.TRUE : RampartBoolean.FALSE;
    }

    public RampartConstant getName() {
        return name;
    }

    public static RampartHttpInjectionType valueOf(RampartConstant name) {
        for (RampartHttpInjectionType input : VALUES) {
            if (input.getName().equals(name)) {
                return input;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartHttpInjectionType)) {
            return false;
        }
        RampartHttpInjectionType otherInjectionType = (RampartHttpInjectionType) other;
        return ObjectUtils.equals(name, otherInjectionType.name);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
