package org.rampart.lang.impl.http.validators.v1;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.http.RampartHttpIOType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Class to validate the URI passed to the request constructor
 * eg.
 *      request("/struts/someFile.jsp")
 * OR
 *      request(uri: "/struts/someFile.jsp")
 */
@Deprecated
public class RampartUriValidatorUpTo1_5 {
    private static final String RELATIVE_ROOT = "/";
    private final Map<String, RampartList> visitorSymbolTable;

    public RampartUriValidatorUpTo1_5(Map<String, RampartList> visitorSymbolTable) {
        this.visitorSymbolTable = visitorSymbolTable;
    }

    public RampartList validateUriValues(RampartHttpIOType httpIOType) throws InvalidRampartRuleException {
        RampartList httpIOTypeValues = visitorSymbolTable
                .get(httpIOType.getName().toString().toLowerCase());
        RampartList uriValueList = getUriStringValues(httpIOTypeValues);

        RampartObjectIterator it = uriValueList.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            validateUriValue(it.next());
        }
        return uriValueList;
    }

    /**
     * URIs may be specified in an Http rule as:
     *  request("/webapp/api")
     *  request(uri: ["/webapp/api/1", "/webapp/api/2"])
     *  request()
     *
     * Method to return an RampartList instance representing the above values.
     * @param httpIOTypeValues object to extract the URIs from.
     * @return An RampartList containing a single element, multiple elements or an RampartList.Empty
     *  in the cases of the examples given above, respectively.
     * @throws InvalidRampartRuleException when an invalid type or URI is specified.
     */
    private RampartList getUriStringValues(RampartList httpIOTypeValues) throws InvalidRampartRuleException {
        RampartObject namedUriObject = RampartInterpreterUtils.findRampartNamedValue(URI_KEY, httpIOTypeValues);

        if (namedUriObject == null) {
            RampartString singleUriValue = RampartInterpreterUtils.findFirstRampartString(httpIOTypeValues);
            if (singleUriValue != null) {
                return newRampartList(singleUriValue);
            } else {
                return RampartList.EMPTY;
            }
        }

        if (!(namedUriObject instanceof RampartList)) {
            throw new InvalidRampartRuleException("named uri declaration must be followed by a list of relative URIs");
        }

        RampartList namedUriList = (RampartList) namedUriObject;
        if (namedUriList.isEmpty() == RampartBoolean.TRUE) {
            throw new InvalidRampartRuleException("named uri list cannot be empty");
        }

        return namedUriList;
    }

    /**
     * Validates a given Uri value
     * @param uriValue value to be validated
     * @throws InvalidRampartRuleException when given value is: not a quoted string value or an invalid Uri
     */
    private void validateUriValue(RampartObject uriValue) throws InvalidRampartRuleException {
        if (!(uriValue instanceof RampartString)) {
            throw new InvalidRampartRuleException("uri value: \"" + uriValue + "\" must be a quoted string value");
        }
        String uriValueString = uriValue.toString();
        if (!isValidURIPath(uriValueString) || !uriValueString.startsWith(RELATIVE_ROOT)) {
            throw new InvalidRampartRuleException("\"" + uriValueString + "\" is not a valid relative URI");
        }

    }

    private static boolean isValidURIPath(String uriPath) {
        try {
            return isURIOnlyContextPath(new URI(uriPath));
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private static boolean isURIOnlyContextPath(URI uri) {
        return uri != null && uri.getRawPath() != null && uri.getScheme() == null
                && uri.getRawAuthority() == null && uri.getRawFragment() == null
                && uri.getRawQuery() == null;
    }
}
