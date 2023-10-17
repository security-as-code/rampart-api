package org.rampart.lang.impl.core;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.*;
import org.rampart.lang.impl.utils.ObjectUtils;

public class RampartActionWithAttributeImpl extends RampartActionImpl implements RampartActionWithAttribute {

    private final RampartActionTarget target;
    private final RampartActionAttribute attribute;
    private final RampartList configMap;
    private final String toStringValue;
    private final int hashCode;

    public RampartActionWithAttributeImpl(RampartAction action, RampartActionTarget target,
                                          RampartActionAttribute attribute, RampartList configMap) {
        super(action.getActionType(), action.getLogMessage(), action.getSeverity(),
                action.shouldLog(), action.getStacktrace());
        this.target = target;
        this.attribute = attribute;
        this.configMap = configMap;
        this.toStringValue = createStringRepresentation();
        this.hashCode = ObjectUtils.hash(target, attribute, configMap, action);
    }

    // @Override
    public RampartActionTarget getTarget() {
        return target;
    }

    // @Override
    public RampartActionAttribute getAttribute() {
        return attribute;
    }

    // @Override
    public RampartList getConfigMap() {
        return configMap;
    }

    @Override
    public String toString() {
        return toStringValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartActionWithAttributeImpl)) {
            return false;
        }
        RampartActionWithAttributeImpl otherActionWithAttributes = (RampartActionWithAttributeImpl) other;
        return ObjectUtils.equals(target, otherActionWithAttributes.target)
                && ObjectUtils.equals(attribute, otherActionWithAttributes.attribute)
                && ObjectUtils.equals(configMap, otherActionWithAttributes.configMap)
                && super.equals(otherActionWithAttributes);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    private String createStringRepresentation() {
        StringBuilder builder = new StringBuilder(actionType.getName().toString()).append("(");
        builder.append(target).append(": ");
        if (configMap.isEmpty() == RampartBoolean.TRUE) {
            builder.append(attribute);
        } else {
            builder.append('{').append(attribute).append(": {");

            // this is a special RampartList that is actually a mapping, cannot use plain toString
            String delim = "";
            RampartObjectIterator it = configMap.getObjectIterator();
            while (it.hasNext() == RampartBoolean.TRUE) {
                builder.append(delim).append(it.next());
                delim = ", ";
            }
            builder.append("}}");

        }
        builder.append(", ");
        if (shouldLog == RampartBoolean.TRUE) {
            builder.append(MESSAGE_KEY).append(": ").append(logMessage.formatted()).append(", ");
        }
        builder.append(SEVERITY_KEY).append(": ").append(severity);
        builder.append(stacktracePart());
        builder.append(')');
        return builder.toString();
    }

}
