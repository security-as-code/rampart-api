package org.rampart.lang.api.process;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.apiprotect.RampartApiFilterRule;
import org.rampart.lang.api.core.RampartActionableRule;
import org.rampart.lang.api.core.RampartDataInputsRule;

public interface RampartProcess extends RampartActionableRule, RampartDataInputsRule, RampartApiFilterRule {
    RampartList getProcessList();
}
