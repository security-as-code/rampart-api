package org.rampart.lang.api;

import org.rampart.lang.impl.utils.ObjectUtils;

/**
 * WARNING: This class MUST be immutable or it will affect the hashcode computation
 */
public abstract class RampartNamedValue implements RampartObject {
    private final int hashCode;

    { hashCode = ObjectUtils.hash(getName(), getRampartObject()); }

    public abstract RampartConstant getName();

    public abstract RampartObject getRampartObject();

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(getName() + ": ");
        if (getRampartObject() instanceof RampartString) {
            s.append(((RampartString) getRampartObject()).formatted());
        } else {
            s.append(getRampartObject());
        }
        return s.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartNamedValue)) {
            return false;
        }
        RampartNamedValue otherNamedValue = (RampartNamedValue) other;
        return ObjectUtils.equals(getName(), otherNamedValue.getName())
                && ObjectUtils.equals(getRampartObject(), otherNamedValue.getRampartObject());
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
