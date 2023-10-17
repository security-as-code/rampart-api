package org.rampart.lang.impl.core.parsers.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartNamedValue;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.java.RampartPrimitives;

/**
 * Utilities for RAMPART parsers. Contains useful things like looking up elements in the lists, etc...
 * Non-parsers should not depend on this class. If some functionality is deemed to be useful in non-parser,
 * then the method should be moved into a more appropriate place.
 */
public class ParserUtils {

    /** A simple holder for a pair of elements. Unfortunately, there is no such standard java type. */
    public static class Pair<F, S> {
        public final F first;
        public final S second;
        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }
    }

    /**
     * Converts the RAMPART list to a general java list.
     * This may be useful for working with other methods.
     * @param rampartList list to convert.
     * @return a list with the same contents as the original. Empty list if the original value was null.
     */
    public static List<RampartObject> toArrayList(RampartList rampartList) {
        if (rampartList == null) {
            return Collections.emptyList();
        }

        final ArrayList<RampartObject> result = new ArrayList<RampartObject>(RampartPrimitives.toJavaInt(rampartList.size()));
        final RampartObjectIterator itr = rampartList.getObjectIterator();
        while (itr.hasNext() == RampartBoolean.TRUE) {
            result.add(itr.next());
        }
        return result;
    }


    /**
     * Retrieves all named values with the given name. Also returns the remained (not-matched) elements.
     * @param name of the value to find.
     * @param items list of items to get the named values from.
     * @return a pair of (list with specific named parameters, list of remaining values). Only the named
     *   values with the name matching <code>name</code> are present in the first list. Other parameters are
     *   returned in the second list. The order of elements in both lists is the same as in the original list.
     */
    public static Pair<List<RampartNamedValue>, List<RampartObject>> extractNamedValues(
            RampartConstant name, List<RampartObject> items) {
        final ArrayList<RampartNamedValue> matchingValues = new ArrayList<RampartNamedValue>();
        final ArrayList<RampartObject> unmatchingValues = new ArrayList<RampartObject>();

        for (RampartObject item: items) {
            if (!(item instanceof RampartNamedValue)) {
                unmatchingValues.add(item);
            } else {
                final RampartNamedValue namedValue = (RampartNamedValue) item;
                if (name.equals(namedValue.getName())) {
                    matchingValues.add(namedValue);
                } else {
                    unmatchingValues.add(namedValue);
                }
            }
        }

        return new Pair<List<RampartNamedValue>, List<RampartObject>>(matchingValues, unmatchingValues);
    }


    /**
     * "Filters out" all the named values with the given name and returns value of the <em>last</em> named element
     * "removed" from the list.
     * @param name of the value to find.
     * @param items list of items to get the named values from.
     * @return a pair of (last value with the given name, list of remaining values). The first element is value of the
     *   (last) named value having the provided name. The second element in the pair is the list of elements not
     *   being a named value or having a different name. The order of elements in the second list is the same as in
     *   the original argument.
     */
    public static Pair<RampartObject, List<RampartObject>> extractLastNamedValue(
            RampartConstant name, List<RampartObject> items) {
        RampartNamedValue matchingValue = null;
        final ArrayList<RampartObject> unmatchingValues = new ArrayList<RampartObject>();

        for (RampartObject item: items) {
            if (!(item instanceof RampartNamedValue)) {
                unmatchingValues.add(item);
            } else {
                final RampartNamedValue namedValue = (RampartNamedValue) item;
                if (name.equals(namedValue.getName())) {
                    matchingValue = namedValue;
                } else {
                    unmatchingValues.add(namedValue);
                }
            }
        }

        final RampartObject value = matchingValue == null ? null : matchingValue.getRampartObject();
        return new Pair<RampartObject, List<RampartObject>>(value, unmatchingValues);
    }
}
