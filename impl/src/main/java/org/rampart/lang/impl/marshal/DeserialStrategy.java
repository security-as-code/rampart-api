package org.rampart.lang.impl.marshal;

import static org.rampart.lang.api.constants.RampartMarshalConstants.DOS_KEY;
import static org.rampart.lang.api.constants.RampartMarshalConstants.RCE_KEY;

public enum DeserialStrategy {
    RCE(RCE_KEY.toString()),
    DOS(DOS_KEY.toString());

    private final String name;

    DeserialStrategy(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}