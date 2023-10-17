package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartCode;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.core.RampartRule;

/**
 * Interface all Rampart rule builders must implement
 * @param <T> rule class being created
 */
public interface RampartRuleBuilder<T extends RampartRule> {
    T createRampartRule(RampartString appName);

    RampartRuleBuilder<T> addRuleName(RampartString ruleName);

    RampartRuleBuilder<T> addCode(RampartCode code);

    RampartRuleBuilder<T> addTargetOSList(RampartList targetOSList);

    RampartRuleBuilder<T> addMetadata(RampartMetadata metadata);
}
