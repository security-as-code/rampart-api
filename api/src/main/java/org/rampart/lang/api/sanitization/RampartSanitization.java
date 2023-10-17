package org.rampart.lang.api.sanitization;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartActionableRule;
import org.rampart.lang.api.http.RampartHttpIOType;

public interface RampartSanitization extends RampartActionableRule {

    RampartHttpIOType getHttpIOType();
    RampartList getUriPaths();
    RampartBoolean areUndeterminedValuesSafe();
    // @since 2.8
    RampartBoolean isUndeterminedValuesLoggingOn();
    RampartBoolean hasIgnore();
    RampartIgnore getIgnore();
}
