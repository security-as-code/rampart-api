package org.rampart.lang.impl.core;

import org.rampart.lang.api.core.RampartApp;
import org.rampart.lang.impl.core.RampartRuleBase;

/** Backdoor to the RAMPART rules. */
public final class RampartRuleBackdoor {
    public static void setApp(RampartRuleBase rule, RampartApp app) {
        rule.setApp(app);
    }
}
