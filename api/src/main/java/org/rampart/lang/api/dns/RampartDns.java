package org.rampart.lang.api.dns;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.apiprotect.RampartApiFilterRule;
import org.rampart.lang.api.core.RampartActionableRule;
import org.rampart.lang.api.core.RampartDataInputsRule;

public interface RampartDns extends RampartActionableRule, RampartDataInputsRule, RampartApiFilterRule {
    RampartString getLookupTarget();
    RampartBoolean onAllTargets();
}
