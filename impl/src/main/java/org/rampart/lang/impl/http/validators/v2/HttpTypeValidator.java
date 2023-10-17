package org.rampart.lang.impl.http.validators.v2;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.rampart.lang.api.*;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.http.RampartHttpIOType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.RampartValidatorBase;
import org.rampart.lang.impl.core.validators.FirstClassRuleObjectValidator;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;
import org.rampart.lang.impl.utils.UriUtils;

public class HttpTypeValidator extends RampartValidatorBase implements FirstClassRuleObjectValidator {

    private final Map<String, RampartList> visitorSymbolTable;

    public HttpTypeValidator(Map<String, RampartList> visitorSymbolTable) {
        super(getPathsRampartObject(visitorSymbolTable));
        this.visitorSymbolTable = visitorSymbolTable;
    }

    private static RampartObject getPathsRampartObject(Map<String, RampartList> visitorSymbolTable) {
        RampartList httpType;
        if ((httpType = visitorSymbolTable.get(REQUEST_KEY.toString())) != null
                || (httpType = visitorSymbolTable.get(RESPONSE_KEY.toString())) != null) {
            return RampartInterpreterUtils.findRampartNamedValue(PATHS_KEY, httpType);
        }
        return null;
    }

    public RampartHttpIOType validateHttpType() throws InvalidRampartRuleException {
        if (visitorSymbolTable.containsKey(REQUEST_KEY.toString())
                && visitorSymbolTable.containsKey(RESPONSE_KEY.toString())) {
            throw new InvalidRampartRuleException("cannot declare both \"" + REQUEST_KEY + "\" and \"" + RESPONSE_KEY + "\"");
        }
        if (visitorSymbolTable.containsKey(REQUEST_KEY.toString())) {
            return RampartHttpIOType.REQUEST;
        }
        if (visitorSymbolTable.containsKey(RESPONSE_KEY.toString())) {
            return RampartHttpIOType.RESPONSE;
        }
        throw new InvalidRampartRuleException(
                "missing mandatory \"" + REQUEST_KEY + "\" or \"" + RESPONSE_KEY + "\" declarations");
    }

    /**
     * Validates uri paths contained in the request or response declaration of the http rule.
     *
     * @return an RampartList containing the RampartString type for each validated URI path
     * @throws InvalidRampartRuleException
     */
    public RampartList validateUriPaths() throws InvalidRampartRuleException {
        lookForInvalidTypes(validateHttpType());
        if (validatableObject == null) {
            // this actually means all uris
            return RampartList.EMPTY;
        }
        if (validatableObject instanceof RampartString) {
            return newRampartList(validateUri((RampartString) validatableObject));
        }
        if (validatableObject instanceof RampartList) {
            return validateIsRampartListOfNonEmptyEntries("\"" + PATHS_KEY + "\" declaration");
        }
        throw new InvalidRampartRuleException(
                "\"" + PATHS_KEY + "\" parameter must be followed by a string literal or a list of string literals");
    }

    /**
     * Look for any invalid parameters to the request/response declaration. Only valid value at the moment is:
     *  request(paths: "/myuri/example")
     *  response(paths: ["/myuri/example"])
     */
    private void lookForInvalidTypes(RampartHttpIOType type) throws InvalidRampartRuleException {
        RampartList httpTypeObject = visitorSymbolTable.get(type.toString());
        if (httpTypeObject == null) {
            return;
        }
        RampartObjectIterator it = httpTypeObject.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject httpTypeParam = it.next();
            if (!(httpTypeParam instanceof RampartNamedValue)
                    || !PATHS_KEY.equals(((RampartNamedValue) httpTypeParam).getName())) {
                throw new InvalidRampartRuleException(
                        "invalid parameter \"" + httpTypeParam + "\" passed to \"" + type + "\" declaration");
            }
        }
    }

    @Override
    protected void validateListEntry(RampartObject entry, String entryContext) throws InvalidRampartRuleException {
        super.validateListEntry(entry, entryContext);
        validateUri((RampartString) entry);
    }

    private static RampartString validateUri(RampartString uriValue) throws InvalidRampartRuleException {
        if (!UriUtils.isValidUriPath(uriValue.toString())) {
            throw new InvalidRampartRuleException("\"" + uriValue + "\" is not a valid relative URI");
        }
        return uriValue;
    }

    // @Override
    public List<RampartConstant> allowedKeys() {
        return Arrays.asList(REQUEST_KEY, RESPONSE_KEY);
    }
}
