package org.rampart.lang.impl.socket;

import org.rampart.lang.api.RampartInteger;
import org.rampart.lang.api.socket.RampartAddress;
import org.rampart.lang.impl.utils.ObjectUtils;

import static org.rampart.lang.java.RampartPrimitives.newRampartInteger;

public class NetworkAddress {

    public static boolean isMostLikelyIPv6(String host) {
        // This is simplified check that covers most of normal cases,
        // with except for the likes of:
        // [008JQWOV7O(=61h*;$LC] or [0x00010002000300000000000600000000]
        // But since we have taken the approach with a single field for either: IPv4, IPv6 or FQDN,
        // we will never be able to be certain what is the intention behind these extreme
        // formats.

        // if address contains more than one ":", then it most likely is IPv6
        return host.contains("::")
                || host.split(":").length > 2;
    }

    public static boolean isIPv4(String ip) {
        return ip.matches("^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$");
    }

    private final RampartAddress rampartAddress;
    private final RampartInteger fromPort;
    private final RampartInteger toPort;
    private final String toStringValue;
    private final int hashCode;

    public NetworkAddress(RampartAddress rampartAddress, int fromPort, int toPort) {
        this.rampartAddress = rampartAddress;
        this.fromPort = newRampartInteger(fromPort);
        this.toPort = newRampartInteger(toPort);
        this.toStringValue = createStringRepresentation();
        this.hashCode = ObjectUtils.hash(rampartAddress, fromPort, toPort);
    }

    public RampartAddress getRampartAddress() {
        return rampartAddress;
    }

    public RampartInteger getFromPort() {
        return fromPort;
    }

    public RampartInteger getToPort() {
        return toPort;
    }

    @Override
    public String toString() {
        return toStringValue;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof NetworkAddress)) {
            return false;
        }
        NetworkAddress otherNetworkAddress = (NetworkAddress) other;
        return ObjectUtils.equals(rampartAddress, otherNetworkAddress.rampartAddress)
                && ObjectUtils.equals(fromPort, otherNetworkAddress.fromPort)
                && ObjectUtils.equals(toPort, otherNetworkAddress.toPort);
    }

    private String createStringRepresentation() {
        StringBuilder builder = new StringBuilder("\"").append(rampartAddress.toString()).append(":");
        if (fromPort.equals(toPort)) {
            builder.append(fromPort);
        } else {
            builder.append(fromPort).append('-').append(toPort);
        }
        return builder.append('"').toString();
    }
}
