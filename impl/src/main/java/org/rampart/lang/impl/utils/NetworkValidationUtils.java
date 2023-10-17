package org.rampart.lang.impl.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.rampart.lang.impl.core.InvalidRampartRuleException;

public class NetworkValidationUtils {
    private static final int INVALID_PORT = -1;

    public static String getHostPart(String addressAndPort) {
        if (StringUtils.isBlank(addressAndPort)) {
            return null;
        }
        int terminatorIndex;
        if ((terminatorIndex = addressAndPort.lastIndexOf(":")) <= 0) {
            terminatorIndex = addressAndPort.length();
        }
        return addressAndPort.substring(0, terminatorIndex);
    }

    public static String getNormalizedIpAddress(String ipAddress) {
        if (StringUtils.isBlank(ipAddress)) {
            return null;
        }
        // this is an IPv6 address and needs to be formatted accordingly for Java libraries
        if (ipAddress.charAt(0) != '[' && ipAddress.lastIndexOf(':') != -1) {
            ipAddress = "[" + ipAddress + "]";
        }
        try {
            return InetAddress.getByName(ipAddress).getHostAddress();
        } catch (UnknownHostException ex) {
            return null;
        }
    }

    public static int[] validatePortRange(String addressAndPort) throws InvalidRampartRuleException {
        int portIndex = addressAndPort.lastIndexOf(':');
        if (portIndex == -1 || portIndex == addressAndPort.length() - 1) {
            throw new InvalidRampartRuleException("invalid address \"" + addressAndPort + "\"");
        }
        int rangeIndex;
        if ((rangeIndex = addressAndPort.indexOf('-', portIndex)) == -1) {
            rangeIndex = addressAndPort.length();
        }
        String portStart = addressAndPort.substring(portIndex + 1, rangeIndex);
        int[] range = new int[2];
        if ((range[0] = getPort(portStart)) == INVALID_PORT) {
            throw new InvalidRampartRuleException("invalid start port \"" + portStart + "\"");
        }
        if (rangeIndex < addressAndPort.length()) {
            String portEnd = addressAndPort.substring(rangeIndex + 1);
            if ((range[1] = getPort(portEnd)) == INVALID_PORT) {
                throw new InvalidRampartRuleException(
                        "invalid end port \"" + portEnd + "\"");
            }
            if(range[0] == 0
                    || range[1] == 0
                    || range[1] < range[0]) {
                throw new InvalidRampartRuleException(
                        "invalid combination for port range \"" + range[0] + "-" + range[1] + "\"");
            }
        } else {
            range[1] = range[0];
        }
        return range;
    }

    private static int getPort(String textPort) {
        try {
            int port = Integer.parseInt(textPort);
            if (port >= 0 && port <= 65535) {
                return port;
            }
        } catch (NumberFormatException nfe) {}
        return INVALID_PORT;
    }

    public static boolean isDomainNameValid(String hostname) {
        return hostname.matches("^([a-z0-9-]{1,63}\\.){0,127}[a-z0-9-]{1,63}[.]{0,1}$");
    }
}
