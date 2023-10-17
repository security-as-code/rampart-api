package org.rampart.lang.impl.dns.parsers.v2;

import static org.rampart.lang.api.constants.RampartDnsConstants.LOOKUP_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.ANY_KEY;
import static org.rampart.lang.api.constants.RampartSocketConstants.IPV4_WILDCARD;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;

import java.util.Map;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.utils.NetworkValidationUtils;

/**
 * Parser of the DNS lookup action.
 */
public final class RampartDnsLookupParser {

    /** Fields supported by the parser. */
    public static final RampartConstant[] SUPPORTED_FIELDS = { LOOKUP_KEY };


    /** Parses the DNS target from the configuration map. */
    public static RampartString parseTarget(Map<String, RampartList> lookupMap) throws InvalidRampartRuleException {
        return parseTarget(lookupMap.get(LOOKUP_KEY.toString()));
    }

    /** Parses the DNS target from the input. */
    public static RampartString parseTarget(RampartList validatableObject) throws InvalidRampartRuleException {
        if (validatableObject == null) {
            throw new InvalidRampartRuleException("missing \"" + LOOKUP_KEY + "\" mandatory declaration");
        }

        RampartString address = null;
        boolean foundAddress = false;
        RampartObjectIterator it = validatableObject.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject parameter = it.next();
            if (parameter instanceof RampartString) {
                if ((address = getValidHostName((RampartString) parameter)) == null) {
                    throw new InvalidRampartRuleException(
                            "invalid hostname \"" + parameter + "\" in \"" + LOOKUP_KEY + "\" declaration");
                }
            } else if (ANY_KEY.equals(parameter)) {
                address = IPV4_WILDCARD;
            } else {
                throw new InvalidRampartRuleException(
                        "unrecognized parameter \"" + parameter + "\" to the \"" + LOOKUP_KEY + "\" declaration");
            }
            if (foundAddress) {
                throw new InvalidRampartRuleException(
                        "duplicate parameter \"" + parameter + "\" detected for \"" + LOOKUP_KEY + "\" declaration");
            }
            foundAddress = true;
        }
        if (address == null) {
            throw new InvalidRampartRuleException("hostname is not specified in \"lookup\" declaration");
        }
        return address;
    }

    private static RampartString getValidHostName(RampartString address) {
        if (NetworkValidationUtils.isDomainNameValid(address.toString())) {
            return address;
        }
        String normalizedIp = NetworkValidationUtils.getNormalizedIpAddress(address.toString());
        if (normalizedIp == null) {
            return null;
        }
        return newRampartString(normalizedIp);
    }
}
