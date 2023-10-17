package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.*;
import org.rampart.lang.impl.core.RampartActionImpl;
import org.rampart.lang.impl.core.RampartActionWithAttributeImpl;

public class RampartActionBuilder implements RampartObjectBuilder<RampartAction> {
    protected RampartActionType actionType;
    protected RampartString logMessage;
    protected RampartSeverity severity;
    private RampartString stacktrace;
    private RampartActionTarget target;
    private RampartActionAttribute attribute;
    private RampartList targetConfigMap = RampartList.EMPTY;

    public RampartAction createRampartObject() {
        RampartAction action;
        if (stacktrace != null) {
            action = new RampartActionImpl(
                    actionType,
                    logMessage,
                    severity,
                    logMessage != null ? RampartBoolean.TRUE : RampartBoolean.FALSE,
                    stacktrace);
        } else {
            action = new RampartActionImpl(actionType,
                    logMessage,
                    severity,
                    logMessage != null ? RampartBoolean.TRUE : RampartBoolean.FALSE);
        }
        if (target != null) {
            return new RampartActionWithAttributeImpl(action, target, attribute, targetConfigMap);
        }
        return action;
    }

    public RampartActionBuilder addActionType(RampartActionType actionType) {
        this.actionType = actionType;
        return this;
    }

    public RampartActionBuilder addLogMessage(RampartString logMessage) {
        this.logMessage = logMessage;
        return this;
    }

    public RampartActionBuilder addSeverity(RampartSeverity severity) {
        this.severity = severity;
        return this;
    }

    public RampartActionBuilder addStacktrace(RampartString stacktrace) {
        this.stacktrace = stacktrace;
        return this;
    }

    public RampartActionBuilder addActionTarget(RampartActionTarget target) {
        this.target = target;
        return this;
    }

    public RampartActionBuilder addActionAttribute(RampartActionAttribute attribute) {
        this.attribute = attribute;
        return this;
    }

    /**
     * @param configMap RampartList of user specified RampartNamedValues
     * @return
     */
    public RampartActionBuilder addTargetConfigMap(RampartList configMap) {
        this.targetConfigMap = configMap;
        return this;
    }
}
