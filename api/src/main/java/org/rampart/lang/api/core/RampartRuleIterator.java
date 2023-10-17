package org.rampart.lang.api.core;

import org.rampart.lang.api.RampartBoolean;

public interface RampartRuleIterator {

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
     * @return next RampartRule in the collection
     */
    RampartRule next();

}