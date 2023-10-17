package org.rampart.lang.api.filesystem;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartObject;

/**
 * Enumeration of filesystem operation types
 * @since 1.4
 */
public enum RampartFileSystemOperation implements RampartObject {
    READ("read"),
    WRITE("write"),
    NOOP;

    private final RampartConstant name;

    RampartFileSystemOperation() {
        this(null);
    }

    RampartFileSystemOperation(final String name) {
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
}
