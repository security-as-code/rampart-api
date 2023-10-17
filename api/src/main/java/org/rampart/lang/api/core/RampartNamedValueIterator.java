package org.rampart.lang.api.core;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartNamedValue;

/**
 * Iterates over a set of configurations represented as a pool of key value pairs, hence the
 * returned type is an RampartNamedValue.
 */
public interface RampartNamedValueIterator {

    /**
     * Check if there is any element left in this iterator.
     *
     * @return true or false depending if there are elements left.
     */
    RampartBoolean hasNext();

    /**
     * Fetch the next element of the iterator. This can throw NoSuchElementException for the single
     * reason that it is ambiguous to return null if there are no more elements in the collection to
     * retrieve.
     *
     * @return next RampartNamedValue in the collection
     */
    RampartNamedValue next();

}
