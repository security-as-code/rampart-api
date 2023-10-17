package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.patch.RampartFunction;
import org.rampart.lang.impl.patch.RampartFunctionImpl;

public class RampartFunctionBuilder implements RampartObjectBuilder<RampartFunction> {
    private RampartString functionName;
    private RampartList checksums = RampartList.EMPTY;

    public RampartFunction createRampartObject() {
        return new RampartFunctionImpl(functionName, checksums);
    }

    public RampartFunctionBuilder addFunctionName(RampartString functionName) {
        this.functionName = functionName;
        return this;
    }

    /**
     * @param checksums non empty RampartList of RampartStrings
     * @return
     */
    public RampartFunctionBuilder addChecksums(RampartList checksums) {
        this.checksums = checksums;
        return this;
    }
}
