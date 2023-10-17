package org.rampart.lang.impl.sql;

import static org.rampart.lang.api.constants.RampartSqlConstants.*;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.sql.RampartVendor;
import org.rampart.lang.impl.utils.ObjectUtils;

public class RampartVendorImpl implements RampartVendor {

    private final RampartVendorType vendor;
    private final RampartList options;
    private final String toStringValue;
    private final int hashCode;

    public RampartVendorImpl(RampartVendorType vendor, RampartList options) {
        this.vendor = vendor;
        this.options = options;
        this.toStringValue = createStringRepresentation();
        this.hashCode = ObjectUtils.hash(vendor, options);
    }

    // @Override
    public RampartConstant getName() {
        return vendor.getName();
    }

    // @Override
    public RampartList getOptions() {
        return options;
    }

    @Override
    public String toString() {
        return toStringValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartVendorImpl)) {
            return false;
        }
        RampartVendorImpl otherVendor = (RampartVendorImpl) other;
        return ObjectUtils.equals(vendor, otherVendor.vendor)
                && ObjectUtils.equals(options, otherVendor.options);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    private String createStringRepresentation() {
        StringBuilder builder = new StringBuilder(VENDOR_KEY.toString()).append('(').append(vendor);
        if(options.isEmpty() == RampartBoolean.FALSE) {
            builder.append(", ").append(OPTIONS_KEY).append(": ").append(options);
        }
        return builder.append(')').toString();
    }
}
