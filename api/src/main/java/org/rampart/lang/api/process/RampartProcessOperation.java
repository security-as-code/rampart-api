package org.rampart.lang.api.process;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartObject;

public enum RampartProcessOperation implements RampartObject {

    EXECUTE("execute");

    private final RampartConstant name;

    RampartProcessOperation(final String name) {
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
