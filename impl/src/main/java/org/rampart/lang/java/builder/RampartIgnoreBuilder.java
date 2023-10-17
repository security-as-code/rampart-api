package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.sanitization.RampartIgnore;
import org.rampart.lang.impl.sanitization.RampartIgnoreImpl;

public class RampartIgnoreBuilder implements RampartObjectBuilder<RampartIgnore> {
    private RampartList payloads = RampartList.EMPTY;
    private RampartList attributes = RampartList.EMPTY;

    public RampartIgnore createRampartObject() {
        return new RampartIgnoreImpl(payloads, attributes);
    }

    /**
     * @param payloads RampartList of RampartStrings
     * @return
     */
    public RampartIgnoreBuilder addPayloads(RampartList payloads) {
        this.payloads = payloads;
        return this;
    }

    /**
     * @param attributes RampartList of RampartStrings
     * @return
     */
    public RampartIgnoreBuilder addAttributes(RampartList attributes) {
        this.attributes = attributes;
        return this;
    }

}
