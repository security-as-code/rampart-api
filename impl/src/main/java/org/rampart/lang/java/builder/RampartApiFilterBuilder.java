package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.apiprotect.RampartApiFilter;
import org.rampart.lang.impl.apiprotect.RampartApiFilterImpl;

public class RampartApiFilterBuilder implements RampartObjectBuilder<RampartApiFilter> {

    private RampartList urlPatterns;

    public RampartApiFilterBuilder() {
        this.urlPatterns = RampartList.EMPTY;
    }

    // @Override
    public RampartApiFilter createRampartObject() {
        if (urlPatterns.isEmpty() == RampartBoolean.TRUE) {
            return RampartApiFilterImpl.ANY;
        }
        return RampartApiFilterImpl.forPatterns(urlPatterns);
    }

    public RampartApiFilterBuilder setUrlPatterns(RampartList urlPatterns) {
        this.urlPatterns = urlPatterns;
        return this;
    }
}
