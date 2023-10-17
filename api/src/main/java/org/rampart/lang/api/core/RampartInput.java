package org.rampart.lang.api.core;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.impl.utils.ObjectUtils;

public class RampartInput implements RampartObject {
    private final RampartConstant name;
    private final int hashCode;

    private RampartInput(RampartConstant name) {
        this.name = name;
        this.hashCode = ObjectUtils.hash(name);
    }

    public static RampartInput HTTP = new RampartInput(RampartRuleType.HTTP.getName());
    public static RampartInput DATABASE = new RampartInput(new RampartConstant() {
        @Override
        public String toString() {
            return "database";
        }
    });
    public static RampartInput DESERIALIZATION = new RampartInput(new RampartConstant() {
        @Override
        public String toString() {
            return "deserialization";
        }
    });

    private static final RampartInput[] VALUES = new RampartInput[] {HTTP, DATABASE, DESERIALIZATION};

    public RampartConstant getName() {
        return name;
    }

    public static RampartInput valueOf(RampartConstant name) {
        for (RampartInput input : VALUES) {
            if (input.getName().equals(name)) {
                return input;
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return name.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartInput)) {
            return false;
        }
        RampartInput otherinput = (RampartInput) other;
        return ObjectUtils.equals(name, otherinput.name);
    }
}
