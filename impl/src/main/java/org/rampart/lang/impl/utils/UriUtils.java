package org.rampart.lang.impl.utils;

import java.net.URI;
import java.net.URISyntaxException;

public class UriUtils {

    // This is the RAMPART wildcard, W4J Agent might still need to translate that into "^"
    // for as long as legacy CSRF rules storage is used.
    private static final String URI_WILDCARD = "*";
    private static final int URI_WILDCARD_LENGTH = URI_WILDCARD.length();

    /**
     * We allow all valid URI path, and optionally they might be have a URI_WILDCARD prefix,
     * or a suffix, (or both)
     *
     * @param uriPath String to be validated
     * @return true if URI path follows the above format, false otherwise
     */
    public static boolean isValidUriPathWithWildcards(String uriPath) {
        return isValidUriPath(uriPath.substring(
                uriPath.startsWith(URI_WILDCARD) ? URI_WILDCARD_LENGTH : 0,
                uriPath.length() - (uriPath.endsWith(URI_WILDCARD) ? URI_WILDCARD_LENGTH : 0)));
    }

    /**
     * A valid URI path must start with a '/' and to be solely the path part as per RFC.
     *
     * @param uriPath String to be validated
     * @return true if URI path follows the above format, false otherwise
     */
    public static boolean isValidUriPath(String uriPath) {
        try {
            return uriPath.length() != 0
                    && isURIOnlyContextPath(new URI(uriPath));
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private static boolean isURIOnlyContextPath(URI uri) {
        return uri != null
                && uri.getRawPath() != null
                && uri.getScheme() == null
                && uri.getRawAuthority() == null
                && uri.getRawFragment() == null
                && uri.getRawQuery() == null;
    }

    /**
     * A valid host is of the form <code>hostname:port</code> or just <code>hostname</code>. This method does not
     * validate the hostname part however, only the port which needs to be between the [1, 65535]
     * range.
     *
     * @param hostOptionallyPort String containg the hostname and the port of the form <code>hostname:port</code> or
     *        <code>hostname</code>
     * @return true if host follows the format, false otherwise
     */
    public static boolean isValidHost(String hostOptionallyPort) {
        String[] hostAndPort = hostOptionallyPort.split(":");
        if (StringUtils.isBlank(hostAndPort[0])) {
            return false;
        }
        if (hostAndPort.length == 1) {
            return true;
        }
        if (hostAndPort.length != 2) {
            return false;
        }
        int port;
        try {
            port = Integer.parseInt(hostAndPort[1]);
        } catch (NumberFormatException e) {
            return false;
        }
        return port > 0 && port <= 65535;
    }
}
