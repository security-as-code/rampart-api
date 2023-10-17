package org.rampart.lang.impl.marshal;

import static org.rampart.lang.api.constants.RampartMarshalConstants.DESERIALIZE_KEY;
import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.api.marshal.RampartMarshal;
import org.rampart.lang.api.marshal.ExternalXmlEntityConfig;
import org.rampart.lang.impl.core.RampartActionableRuleBase;
import org.rampart.lang.impl.utils.ObjectUtils;

public class RampartMarshalImpl extends RampartActionableRuleBase implements RampartMarshal {

    private final RampartList deserialTypes;
    private final DeserialStrategy deserialStrategy;
    private final String toStringValue;
    private final ExternalXmlEntityConfig externalXmlEntityConfig;
    private final int hashCode;

    public RampartMarshalImpl(RampartString appName, RampartString ruleName,
                              RampartList deserialTypes, DeserialStrategy deserialStrategy, RampartAction action, RampartList targetOSList,
                              RampartMetadata metadata, ExternalXmlEntityConfig externalXmlEntityConfig) {
        super(appName, ruleName, action, targetOSList, metadata);
        this.deserialTypes = deserialTypes;
        this.deserialStrategy = deserialStrategy;
        this.externalXmlEntityConfig = externalXmlEntityConfig;
        this.toStringValue = super.toString();
        this.hashCode = ObjectUtils.hash(deserialTypes, deserialStrategy, externalXmlEntityConfig, super.hashCode());
    }

    // @Override
    public RampartRuleType getRuleType() {
        return RampartRuleType.MARSHAL;
    }

    // @Override
    public RampartList getRampartDeserializeTypes() {
        return deserialTypes;
    }

    // @Override
    public RampartBoolean onRemoteCodeExecution() {
        return deserialStrategy == DeserialStrategy.RCE ? RampartBoolean.TRUE : RampartBoolean.FALSE;
    }

    // @Override
    public RampartBoolean onDenialOfService() {
        return deserialStrategy == DeserialStrategy.DOS ? RampartBoolean.TRUE : RampartBoolean.FALSE;
    }

    // @Override
    public ExternalXmlEntityConfig getExternalXmlEntityConfig() {
        return this.externalXmlEntityConfig;
    }

    @Override
    protected void appendRuleBody(StringBuilder builder) {
        if (isRCEOrDOSStrategy()) {
            builder.append('\t').append(DESERIALIZE_KEY)
                    .append("(");
            appendDeserializeTypes(builder);
            builder.append(')')
                    .append(LINE_SEPARATOR)
                    .append('\t').append(deserialStrategy).append("()")
                    .append(LINE_SEPARATOR);
        }
        if (hasExternalXmlEntityConfig()) {
            builder.append('\t').append(externalXmlEntityConfig);
        }
        super.appendRuleBody(builder);
    }

    private void appendDeserializeTypes(StringBuilder builder) {
        String delim = "";
        RampartObjectIterator it = deserialTypes.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            builder.append(delim).append(it.next());
            delim = ", ";
        }
    }

    private boolean isRCEOrDOSStrategy() {
        return DeserialStrategy.RCE.equals(this.deserialStrategy)
               || DeserialStrategy.DOS.equals(this.deserialStrategy);
    }

    private boolean hasExternalXmlEntityConfig() {
        return this.externalXmlEntityConfig != null;
    }

    @Override
    public String toString() {
        return toStringValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartMarshalImpl)) {
            return false;
        }
        RampartMarshalImpl otherMarshal = (RampartMarshalImpl) other;
        return super.equals(otherMarshal)
               && ObjectUtils.equals(deserialTypes, otherMarshal.deserialTypes)
               && ObjectUtils.equals(deserialStrategy, otherMarshal.deserialStrategy)
               && ObjectUtils.equals(externalXmlEntityConfig, otherMarshal.externalXmlEntityConfig);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

}
