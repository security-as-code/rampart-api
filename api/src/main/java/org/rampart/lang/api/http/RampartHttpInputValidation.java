package org.rampart.lang.api.http;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;

public interface RampartHttpInputValidation extends RampartObject {
    /**
     * What part of the HTTP request is validated. E.g. parameters, headers, cookies.
     */
    RampartHttpValidationType getType();

    /**
     * Specific targets of the input validation performed. E.g.:
     * type: target
     * parameters: parameter name (RampartString)
     * headers: header name (RampartString)
     * cookies: cookie name (RampartString)
     * request: path in the URL (RampartConstant)
     *
     * Only one RampartHttpValidationType is allowed each time, so the list will refer to a specific
     * type.
     *
     * @return an RampartList containing the validation targets, which could be an RampartString or an
     *         RampartConstant depending on the validation type.
     */
    RampartList getTargets();

    /**
     * Matchers that are pre-built and don't need configuration. E.g. integer, integer-positive,
     * alphanumeric, html-text.
     *
     * @return an RampartList containing matchers, all of the RampartHttpMatcherType type. Or an empty list if
     *         there are no matchers of this type
     */
    RampartList getBuiltInMatchers();

    /**
     * If specified by the user a regex matcher is accessible through here
     */
    RampartString getRegexPattern();

    /**
     * @return RampartmBoolean indicating if a regex matcher was specified by the user
     */
    RampartBoolean hasRegexPattern();

    /**
     * @return RampartmBoolean indicating if a omit string rule was specified by the user
     */
    RampartBoolean hasOmitRule();

    /**
     * @return RampartmBoolean indicating if a target for the input validation type was specified by the user
     */
    RampartBoolean hasTargets();

    /**
     * If specified by the user, the series of omit strings to impose on validation are accessible
     * through here
     *
     * @return an RampartList where all elements are RampartString entries that represent the strings for
     *         the omit rule
     */
    RampartList getOmitRules();

}
