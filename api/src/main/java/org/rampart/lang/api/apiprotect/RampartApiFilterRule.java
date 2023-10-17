package org.rampart.lang.api.apiprotect;

/** Rule with optional API filters. */
public interface RampartApiFilterRule {
    /**
     * Returns a filter on the current API context or <code>null</code> if rule is also
     * applicable to non-API requests.
     *
     * The rule is applied as follows:
     *
     * <ul>
     *   <li><strong>null</strong> - the rule is applicable in non-API context.
     *   <li><strong>filter</strong> where <code>filter.onAllTargets()</code> is <code>RampartBoolean.TRUE</code> -
     *     the rule is applicable in <em>any</em> API context but is not applicable in non-api method calls.
     *   <li><strong>filter</strong> where <code>filter.onAllTargets()</code> is <code>RampartBoolean.FALSE</code> -
     *     The rule is only applicable in API context where request path matches one of the wildcards contained in
     *     the <code>filter.getUrlPatterns()</code> list. The rule is not applicable in the API contexts not matching
     *     any of the URL patterns. It is also not available in non-API contexts.
     * </ul>
     *
     *
     * @return filter for the current API Request context or <code>null</code> if the rule
     * is applicable in non-api context.
     */
    RampartApiFilter getApiFilter();
}
