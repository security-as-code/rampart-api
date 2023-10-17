package org.rampart.lang.api.constants;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.api.http.RampartHttpIOType;

/**
 * Constants related to the APIProtect module.
 */
public class RampartApiProtectConstants extends RampartGeneralConstants {
    /** Name of the rule key. */
    public static final RampartConstant APIPROTECT_KEY = RampartRuleType.API.getName();

    /** Name of the API Filter key (used in existing rules). */
    public static final RampartConstant API_FILTER_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "api";
        }
    };

    /**
     * Request key - the rule should be applied before the application logic (i.e.
     * "on request received").
     */
    public static final RampartConstant REQUEST_KEY = RampartHttpIOType.REQUEST.getName();

    /**
     * Response key - the rule should be processed after the application logic (i.e.
     * "on response generated".
     */
    public static final RampartConstant RESPONSE_KEY = RampartHttpIOType.RESPONSE.getName();

}
