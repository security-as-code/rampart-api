package org.rampart.lang.impl.filesystem.parsers.v2;

import static org.rampart.lang.api.constants.RampartFileSystemConstants.ABSOLUTE_KEY;
import static org.rampart.lang.api.constants.RampartFileSystemConstants.FILESYSTEM_KEY;
import static org.rampart.lang.api.constants.RampartFileSystemConstants.RELATIVE_KEY;
import static org.rampart.lang.api.constants.RampartFileSystemConstants.TRAVERSAL_KEY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

/** Parser for file traversal rules. */
public final class RampartTraversalParser {
    /** Key used for parsing. */
    public static final RampartConstant DEFAULT_KEY = TRAVERSAL_KEY;

    /**
     * Convenience array containing default key, useful in standard checks for
     * required/supported fields.
     */
    public static final RampartConstant[] DEFAULT_KEYS = {DEFAULT_KEY};

    private RampartTraversalParser() {
        throw new UnsupportedOperationException();
    }

    public static List<RampartConstant> parseTraversalOptions(Map<String, RampartList> symbolTable)
            throws InvalidRampartRuleException {
        return parseTraversalOptions(symbolTable.get(DEFAULT_KEY.toString()));
    }


    public static List<RampartConstant> parseTraversalOptions(RampartList traversalOptions) throws InvalidRampartRuleException {
        if (traversalOptions == null) {
            return Collections.emptyList();
        }
        if (traversalOptions.isEmpty() == RampartBoolean.TRUE) {
            return Arrays.asList(RELATIVE_KEY, ABSOLUTE_KEY);
        }
        validateListOfConstants(traversalOptions);
        ArrayList<RampartConstant> traversalOptionsArray = new ArrayList<RampartConstant>();
        RampartObjectIterator it = traversalOptions.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject traversal = it.next();
            if (traversal.equals(RELATIVE_KEY)
                    || traversal.equals(ABSOLUTE_KEY)) {
                traversalOptionsArray.add((RampartConstant) traversal);
            } else {
                throw new InvalidRampartRuleException(
                        "unrecognized constant \"" + traversal + "\" parameter for \"" + TRAVERSAL_KEY + "\" in \"" + FILESYSTEM_KEY + "\" RAMPART rule");
            }
            if (traversalOptionsArray.size() > 1) {
                throw new InvalidRampartRuleException(
                        "traversal declaration for RAMPART  \"" + FILESYSTEM_KEY + "\" rule can only contain the \"" + ABSOLUTE_KEY
                                + "\" or the \"" + RELATIVE_KEY + "\" constants");
            }
        }
        return traversalOptionsArray;
    }

    /** Checks that the list consists of constants. */
    private static void validateListOfConstants(RampartList list) throws InvalidRampartRuleException {
        final RampartObjectIterator itr = list.getObjectIterator();
        while (itr.hasNext() == RampartBoolean.TRUE) {
            final RampartObject entry = itr.next();
            if (!(entry instanceof RampartConstant)) {
                throw new InvalidRampartRuleException("\"" + TRAVERSAL_KEY + "\" declaration list entries must be constants");
            }
        }
    }
}