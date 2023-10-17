package org.rampart.lang.impl.core.validators;

public abstract class RampartSystem {
    public abstract boolean isAbsolute(String path);
    public abstract String normalize(String path);
    public abstract char getFileSeparator();
}
