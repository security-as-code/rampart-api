package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.sql.RampartVendor;
import org.rampart.lang.impl.sql.RampartVendorImpl;
import org.rampart.lang.impl.sql.RampartVendorType;

public class RampartVendorBuilder implements RampartObjectBuilder<RampartVendor> {
    private RampartVendorType vendor;
    private RampartList configuredOptions = RampartList.EMPTY;

    public RampartVendor createRampartObject() {
        return new RampartVendorImpl(vendor, configuredOptions);
    }

    public RampartVendorBuilder addVendor(RampartConstant databaseVendor) {
        this.vendor = RampartVendorType.valueOf(databaseVendor);
        if (vendor == null) {
            // this is a dev error
            throw new IllegalArgumentException("unrecognized database vendor [" + databaseVendor + "]");
        }
        return this;
    }

    /**
     * @param configuredOptions RampartList of RampartConstants
     * @return
     */
    public RampartVendorBuilder addConfiguredOptions(RampartList configuredOptions) {
        this.configuredOptions = configuredOptions;
        return this;
    }

}
