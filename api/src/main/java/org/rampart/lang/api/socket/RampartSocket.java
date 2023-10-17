package org.rampart.lang.api.socket;

import org.rampart.lang.api.apiprotect.RampartApiFilterRule;
import org.rampart.lang.api.core.RampartActionableRule;
import org.rampart.lang.api.core.RampartDataInputsRule;

public interface RampartSocket extends RampartActionableRule, RampartDataInputsRule, RampartApiFilterRule {
    RampartSocketOperation getSocketOperation();
}
