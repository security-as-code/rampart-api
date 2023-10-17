package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.apiprotect.RampartApiFilter;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartCode;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.dns.RampartDns;
import org.rampart.lang.impl.dns.RampartDnsImpl;

public class RampartDnsBuilder implements RampartRuleBuilder<RampartDns> {
    private RampartString ruleName;
    private RampartString lookupTarget;
    private RampartAction action;
    private RampartList targetOSList;
    private RampartMetadata metadata;
    private RampartList dataInputs;
    private RampartApiFilter apiFilter;

    // @Override
    public RampartDns createRampartRule(RampartString appName) {
        return new RampartDnsImpl(appName, ruleName, action, lookupTarget, targetOSList, metadata, dataInputs, apiFilter);
    }

    // @Override
    public RampartDnsBuilder addRuleName(RampartString ruleName) {
        this.ruleName = ruleName;
        return this;
    }

    // @Override
    public RampartDnsBuilder addCode(RampartCode code) {
        // TODO to be implemented when code blocks are supported
        return this;
    }

    public RampartDnsBuilder addLookupTarget(RampartString lookupTarget) {
        this.lookupTarget = lookupTarget;
        return this;
    }

    public RampartDnsBuilder addAction(RampartAction action) {
        this.action = action;
        return this;
    }

    /**
     * @param targetOSList non empty RampartList of RampartConstants
     * @return
     */
    // @Override
    public RampartDnsBuilder addTargetOSList(RampartList targetOSList) {
        this.targetOSList = targetOSList;
        return this;
    }

    public RampartDnsBuilder addMetadata(RampartMetadata metadata) {
        this.metadata = metadata;
        return this;
    }


    /**
     * Adds tainting input list to this builder.
     * @param dataInputs description of data inputs.
     * @return this builder.
     */
    public RampartDnsBuilder addDataInputs(RampartList dataInputs) {
        this.dataInputs = dataInputs;
        return this;
    }


    public RampartDnsBuilder addApiFilter(RampartApiFilter apiFilter) {
        this.apiFilter = apiFilter;
        return this;
    }
}
