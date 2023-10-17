package org.rampart.lang.impl.apiprotect.parsers;

import java.util.Map;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.apiprotect.RampartApiFilter;
import org.rampart.lang.api.constants.RampartApiProtectConstants;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.impl.apiprotect.RampartApiFilterImpl;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

/** Parser for the API Filter specification. */
public final class RampartApiFilterParser {

    /** Default key used in the API filter constant. */
    public static final RampartConstant API_FILTER_KEY = RampartApiProtectConstants.APIPROTECT_KEY;

    /** Convenience array for use with structure validators. */
    public static final RampartConstant[] DEFAULT_API_FILTER_KEYS = { API_FILTER_KEY };


    private RampartApiFilterParser() {
        throw new UnsupportedOperationException();
    }


    /** Parses the Filter list definition into the list. */
    public static RampartApiFilter parse(RampartList specification) throws InvalidRampartRuleException {
        /* Filters are optional. */
        if (specification == null) {
            return null;
        }

        if (specification.contains(RampartApiProtectConstants.ANY_KEY) == RampartBoolean.TRUE) {
            validateOnlyAnyKey(specification);
            return RampartApiFilterImpl.ANY;
        } else {
            validateListOfPatterns(specification);
            return RampartApiFilterImpl.forPatterns(specification);
        }
    }


    /** Validates that the specification contains only the single "any" key. */
    private static void validateOnlyAnyKey(RampartList specification) throws InvalidRampartRuleException {
        final RampartObjectIterator iterator = specification.getObjectIterator();
        final RampartObject first = iterator.next();

        if (!RampartApiProtectConstants.ANY_KEY.equals(first)) {
            throw new InvalidRampartRuleException(
                    "The 'any' API specifier could only be used alone,  used with \"" + first + "\"");
        }

        if (iterator.hasNext() == RampartBoolean.TRUE) {
            throw new InvalidRampartRuleException(
                    "The 'any' API specifier could only be used alone,  used with \"" + iterator.next() + "\"");
        }
     }


    /** Validates that the rampart list contains a list of valid URL patterns. */
    private static void validateListOfPatterns(RampartList specification) throws InvalidRampartRuleException {
        final RampartObjectIterator iterator = specification.getObjectIterator();

        if (iterator.hasNext() == RampartBoolean.FALSE) {
            throw new InvalidRampartRuleException(
                    "The 'api' filter clause must contain an 'any' endpoint specifier or a list of URL patterns");
        }

        while (iterator.hasNext() == RampartBoolean.TRUE) {
            final RampartObject entry = iterator.next();
            if (!(entry instanceof RampartString)) {
                throw new InvalidRampartRuleException("Invalid entry \"" + entry
                        + "\" in the 'api' filter clause. Only 'any' specifier strings are allowed");
            }
            final String urlPattern = entry.toString();
            if (urlPattern.length() == 0) {
                throw new InvalidRampartRuleException("Empty strings are not allowed in the 'api' filter clause");
            }
            validateWildcardPattern(urlPattern);
        }
    }


    /** Validates that the wildcard pattern is correct. */
    private static void validateWildcardPattern(String string) throws InvalidRampartRuleException {
        final int firstWildcardIndex = string.indexOf('*');
        if (firstWildcardIndex < 0) {
            return;
        }

        if (string.indexOf('*', firstWildcardIndex + 1) >= 0) {
            throw new InvalidRampartRuleException(
                    "Invalid url pattern \"" + string + "\", at most one wildcard character is allowed");
        }
    }


    public static RampartApiFilter parse(Map<String, RampartList> symbolTable) throws InvalidRampartRuleException {
        return parse(symbolTable.get(API_FILTER_KEY.toString()));
    }
}
