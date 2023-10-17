package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.patch.RampartLocation;
import org.rampart.lang.api.patch.RampartPatchType;
import org.rampart.lang.impl.patch.RampartLocationImpl;

public class RampartLocationBuilder implements RampartObjectBuilder<RampartLocation> {
    private RampartPatchType patchType;
    private RampartObject parameter;
    private RampartList occurrences = RampartList.EMPTY;

    public RampartLocation createRampartObject() {
        return new RampartLocationImpl(patchType, parameter, occurrences);
    }

    public RampartLocationBuilder addPatchType(RampartPatchType patchType) {
        this.patchType = patchType;
        return this;
    }

    public RampartLocationBuilder addLocationParameter(RampartObject parameter) {
        this.parameter = parameter;
        return this;
    }

    /**
     * @param occurrences RampartList of RampartIntegers
     * @return
     */
    public RampartLocationBuilder addOccurrences(RampartList occurrences) {
        this.occurrences = occurrences;
        return this;
    }
}
