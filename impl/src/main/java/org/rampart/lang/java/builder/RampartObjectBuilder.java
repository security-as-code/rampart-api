package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartObject;

public interface RampartObjectBuilder<T extends RampartObject>{
    T createRampartObject();
}
