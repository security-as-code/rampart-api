package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.socket.RampartAddress;
import org.rampart.lang.impl.socket.RampartAddressHostnameImpl;
import org.rampart.lang.impl.socket.RampartAddressIpImpl;
import org.rampart.lang.impl.socket.NetworkAddress;
import org.rampart.lang.impl.utils.NetworkValidationUtils;

import static org.rampart.lang.java.RampartPrimitives.newRampartString;

public class RampartAddressBuilder implements RampartObjectBuilder<RampartAddress> {
    // this can be a hostname or an IP address
    private RampartString address;

    public RampartAddress createRampartObject() {
        // do minimal verification to see if its an IP or hostname. Caller should provide a sane address
        if (NetworkAddress.isIPv4(address.toString())) {
            return new RampartAddressIpImpl(address);
        } else if (NetworkAddress.isMostLikelyIPv6(address.toString())) {
            String ip = NetworkValidationUtils.getNormalizedIpAddress(address.toString());
            return new RampartAddressIpImpl(newRampartString(ip));
        }
        return new RampartAddressHostnameImpl(address);
    }

    public RampartAddressBuilder addAddress(RampartString address) {
        this.address = address;
        return this;
    }

}
