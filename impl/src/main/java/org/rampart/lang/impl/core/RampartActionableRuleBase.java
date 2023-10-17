package org.rampart.lang.impl.core;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartActionableRule;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.impl.utils.ObjectUtils;

/**
 * Class to encapsulate common state among Actionable rules
 */
public abstract class RampartActionableRuleBase extends RampartRuleBase implements RampartActionableRule {
    private final RampartAction action;
    private final int localHashCode;

    protected RampartActionableRuleBase(RampartString appName, RampartString ruleName, RampartAction action, RampartList targetOSList,
                                        RampartMetadata metadata) {
        super(appName, ruleName, null, targetOSList, metadata);
        this.action = action;
        this.localHashCode = ObjectUtils.hash(action, super.hashCode());
    }

    // @Override
    public RampartAction getAction() {
        return action;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartActionableRuleBase)) {
            return false;
        }
        RampartActionableRuleBase otherRule = (RampartActionableRuleBase) other;
        return ObjectUtils.equals(action, otherRule.action) && super.equals(other);
    }

    @Override
    public int hashCode() {
        return localHashCode;
    }

    @Override
    protected void appendRuleBody(StringBuilder builder) {
        builder.append("\t").append(getAction()).append(LINE_SEPARATOR);
    }
}
