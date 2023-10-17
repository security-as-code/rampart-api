package org.rampart.lang.api.apiprotect;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;

/**
 * Specification of a filter on the HTTP request context that could be applied
 * to some additional rules.
 *
 * The API filter could be either <em>wildcard</em> (matches any request) or contain
 * a list of URL patterns.
 */
public interface RampartApiFilter extends RampartObject {
    /**
     * Checks if the API filter is wildcard - i.e. it applies to <em>all</em>
     * API requests. If the API filter is wildcard, then the list of the URL patterns is empty.
     */
    public RampartBoolean onAllTargets();

    /**
     * Returns a list of URL patterns to which the API filter should be applied. Returns
     * an empty list if the filter is wildcard (accept all) filter.
     */
    public RampartList getUrlPatterns();
}
