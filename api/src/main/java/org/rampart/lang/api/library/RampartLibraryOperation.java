package org.rampart.lang.api.library;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartObject;

public enum RampartLibraryOperation implements RampartObject {

    LOAD("load");

    private final RampartConstant name;

    RampartLibraryOperation(final String name) {
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
