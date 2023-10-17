package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartCode;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.marshal.RampartMarshal;
import org.rampart.lang.api.marshal.ExternalXmlEntityConfig;
import org.rampart.lang.impl.marshal.DeserialStrategy;
import org.rampart.lang.impl.marshal.RampartMarshalImpl;

public class RampartMarshalBuilder implements RampartRuleBuilder<RampartMarshal> {
    private RampartString ruleName;
    private RampartList deserialTypes = RampartList.EMPTY;
    private DeserialStrategy deserialStrategy;
    private ExternalXmlEntityConfig externalXmlEntityConfig;
    private RampartAction action;
    private RampartList targetOSList;
    private RampartMetadata metadata;

    // @Override
    public RampartMarshal createRampartRule(RampartString appName) {
        return new RampartMarshalImpl(appName, ruleName, deserialTypes,
                deserialStrategy, action, targetOSList, metadata, externalXmlEntityConfig);
    }

    // @Override
    public RampartMarshalBuilder addRuleName(RampartString ruleName) {
        this.ruleName = ruleName;
        return this;
    }

    /**
     * @param deserialTypes RampartList of RampartConstants
     * @return
     */
    public RampartMarshalBuilder addDeserialTypes(RampartList deserialTypes) {
        this.deserialTypes = deserialTypes;
        return this;
    }

    public boolean hasDeserialTypes() {
        return this.deserialTypes != null;
    }

    public RampartMarshalBuilder withProtectOnRce() {
        this.deserialStrategy = DeserialStrategy.RCE;
        return this;
    }

    public RampartMarshalBuilder withProtectOnDos() {
        this.deserialStrategy = DeserialStrategy.DOS;
        return this;
    }

    // @Override
    public RampartMarshalBuilder addCode(RampartCode code) {
        // TODO: support to be added for code block soon
        return this;
    }

    public RampartMarshalBuilder addAction(RampartAction action) {
        this.action = action;
        return this;
    }

    public RampartAction getAction() {
        return action;
    }

    /**
     * @param targetOSList RampartList of RampartConstants
     * @return
     */
    // @Override
    public RampartMarshalBuilder addTargetOSList(RampartList targetOSList) {
        this.targetOSList = targetOSList;
        return this;
    }

    public RampartRuleBuilder<RampartMarshal> addMetadata(RampartMetadata metadata) {
        this.metadata = metadata;
        return this;
    }

    public RampartMarshalBuilder addExternalXmlEntityConfig(ExternalXmlEntityConfig xml) {
        this.externalXmlEntityConfig = xml;
        return this;
    }

    public boolean hasExternalXmlEntityConfig() {
        return this.externalXmlEntityConfig != null;
    }
}
