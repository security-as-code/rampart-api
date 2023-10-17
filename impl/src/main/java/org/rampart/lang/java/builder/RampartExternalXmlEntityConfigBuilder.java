package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartInteger;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.marshal.ExternalXmlEntityConfig;
import org.rampart.lang.impl.marshal.ExternalXmlEntityConfigImpl;

public class RampartExternalXmlEntityConfigBuilder implements RampartObjectBuilder<ExternalXmlEntityConfig> {

    private RampartList uris;
    private RampartInteger referenceLimit;
    private RampartInteger referenceExpansionLimit;

    public ExternalXmlEntityConfig createRampartObject() {
        return new ExternalXmlEntityConfigImpl(uris, referenceLimit, referenceExpansionLimit);
    }

    public RampartExternalXmlEntityConfigBuilder setUris(RampartList uris) {
        this.uris = uris;
        return this;
    }

    public RampartExternalXmlEntityConfigBuilder setReferenceLimit(RampartInteger limit) {
        this.referenceLimit = limit;
        return this;
    }

    public RampartExternalXmlEntityConfigBuilder setReferenceExpansionLimit(RampartInteger expansionLimit) {
        this.referenceExpansionLimit = expansionLimit;
        return this;
    }

}
