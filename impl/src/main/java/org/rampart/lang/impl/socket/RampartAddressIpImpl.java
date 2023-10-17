package org.rampart.lang.impl.socket;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.socket.RampartAddress;
import org.rampart.lang.impl.utils.ObjectUtils;

public class RampartAddressIpImpl implements RampartAddress {
    private final RampartString ipAddress;

    public RampartAddressIpImpl(final RampartString ipAddress) {
        this.ipAddress = ipAddress;
    }

    public RampartBoolean hasHostname() {
        return RampartBoolean.FALSE;
    }

    public RampartString getHostname() {
        return null;
    }

    public RampartBoolean hasIpAddress() {
        return RampartBoolean.TRUE;
    }

    public RampartString getIpAddress() {
        return ipAddress;
    }

    @Override
    public String toString() {
        return ipAddress.toString();
    }

    @Override
    public int hashCode() {
        return ipAddress.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof RampartAddressIpImpl)) {
            return false;
        }
        RampartAddressIpImpl otherHostname = (RampartAddressIpImpl) other;
        return ObjectUtils.equals(ipAddress, otherHostname.ipAddress);
    }
}
