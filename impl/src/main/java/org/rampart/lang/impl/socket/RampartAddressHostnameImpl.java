package org.rampart.lang.impl.socket;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.socket.RampartAddress;
import org.rampart.lang.impl.utils.ObjectUtils;

public class RampartAddressHostnameImpl implements RampartAddress {

    private final RampartString hostname;

    public RampartAddressHostnameImpl(final RampartString hostname) {
        this.hostname = hostname;
    }

    public RampartBoolean hasHostname() {
        return RampartBoolean.TRUE;
    }

    public RampartString getHostname() {
        return hostname;
    }

    public RampartBoolean hasIpAddress() {
        return RampartBoolean.FALSE;
    }

    public RampartString getIpAddress() {
        return null;
    }

    @Override
    public String toString() {
        return hostname.toString();
    }

    @Override
    public int hashCode() {
        return hostname.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof RampartAddressHostnameImpl)) {
            return false;
        }
        RampartAddressHostnameImpl otherHostname = (RampartAddressHostnameImpl) other;
        return ObjectUtils.equals(hostname, otherHostname.hostname);
    }
}
