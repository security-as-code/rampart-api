package org.rampart.lang.impl.apiprotect.validators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.constants.RampartApiProtectConstants;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.http.RampartHttpIOType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.utils.UriUtils;


final class RampartInterceptionPointValidator {
    /** Pair from the RAMPART map. */
    static final class RampartPair {
        /** Key (usually in the rules map). */
        final RampartConstant key;
        /** Value for the given key. */
        final RampartList value;

        public RampartPair(RampartConstant key, RampartList value) {
            this.key = key;
            this.value = value;
        }
    }

    /** Constants that are used for defining interception rules in RAMPART 2.9 and above. */
    static final RampartConstant[] interceptKeys2_9 = {
            RampartApiProtectConstants.REQUEST_KEY,
            RampartApiProtectConstants.RESPONSE_KEY
    };

    private RampartInterceptionPointValidator() {
        throw new UnsupportedOperationException();
    }

    /* Parses/returns request point where the request should be intercepted for RAMPART 2.9. */
    static RampartApiInterceptionPoint getInterceptionPoint2_9(Map<String, RampartList> rules)
            throws InvalidRampartRuleException {
        final RampartPair userChoice = chooseExclusively(rules, interceptKeys2_9);
        final RampartHttpIOType ioType =
                userChoice.key == RampartApiProtectConstants.REQUEST_KEY ? RampartHttpIOType.REQUEST : RampartHttpIOType.RESPONSE;
        final RampartList uriPatterns = validateUriPatterns(userChoice);
        return new RampartApiInterceptionPoint(uriPatterns, ioType);
    }


    /**
     * Decodes/validates URI patterns.
     */
    private static RampartList validateUriPatterns(RampartPair pair) throws InvalidRampartRuleException {
        if (pair.value == null || pair.value.isEmpty() == RampartBoolean.TRUE) {
            throw new InvalidRampartRuleException(
                    "\"" + pair.key + "\" declaration must be followed by a non-empty list");
        }

        final RampartObjectIterator itr = pair.value.getObjectIterator();
        while (itr.hasNext() == RampartBoolean.TRUE) {
            final RampartObject obj = itr.next();
            if (!(obj instanceof RampartString)) {
                throw new InvalidRampartRuleException(
                    "\"" + pair.key + "\" list entries must be quoted string literals");
            }

            final RampartString str = (RampartString) obj;
            if (!UriUtils.isValidUriPath(str.toString())) {
                throw new InvalidRampartRuleException(
                    "\"" + pair.key + "\" list entry \"" + str + "\" is not a valid relative URI");
            }
        }

        return pair.value;
    }


    /**
     * Retrieves a value associated with one of the mutually exclusive constants.
     * @throws InvalidRampartRuleException if more than one constant is present or no key from
     *   the keyConstanst is provided.
     */
    private static RampartPair chooseExclusively(Map<String, RampartList> rules, RampartConstant... keyConstants)
            throws InvalidRampartRuleException {
        final List<RampartPair> goodPairs = new ArrayList<RampartPair>();
        for (RampartConstant constant: keyConstants) {
            final RampartList maybeValue = rules.get(constant.toString());
            if (maybeValue != null) {
                goodPairs.add(new RampartPair(constant, maybeValue));
            }
        }

        if (goodPairs.size() == 1) {
            return goodPairs.get(0);
        }

        if (goodPairs.size() == 0) {
            final StringBuilder sb = new StringBuilder("missing one of the mandatory \"");
            sb.append(keyConstants[0]);
            int ptr = 1;
            while (ptr < keyConstants.length) {
                final RampartConstant constant = keyConstants[ptr++];
                final boolean isLast = ptr >= keyConstants.length;
                sb.append(isLast ? "\" or \"": "\", \"");
                sb.append(constant);
            }
            sb.append('"');
            throw new InvalidRampartRuleException(sb.toString());
        }

        final StringBuilder sb = new StringBuilder("cannot use more than one of ");
        appendKeysMessage(goodPairs, sb);
        sb.append(" at the same time");
        throw new InvalidRampartRuleException(sb.toString());
    }


    /** Appends "keys" of the pairs to the builder. */
    private static void appendKeysMessage(final List<RampartPair> goodPairs, final StringBuilder sb) {
        sb.append('\"');

        final Iterator<RampartPair> itr = goodPairs.iterator();
        sb.append(itr.next().key);

        RampartPair next = itr.next();
        while (next != null) {
            final boolean notLast = itr.hasNext();
            sb.append(notLast ? "\", \"": "\" or \"");
            sb.append(next.key);
            next = notLast ? itr.next() : null;
        }
        sb.append('\"');
    }
}
