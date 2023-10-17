package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.apiprotect.RampartApiFilter;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartCode;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.socket.RampartSocket;
import org.rampart.lang.api.socket.RampartSocketOperation;
import org.rampart.lang.impl.socket.RampartSocketImpl;

public class RampartSocketBuilder implements RampartRuleBuilder<RampartSocket> {

    private RampartString ruleName;
    private RampartSocketOperation operation;
    private RampartList targetOSList;
    private RampartAction action;
    private RampartMetadata metadata;
    private RampartList dataInputs;
    private RampartApiFilter apiFilter;

    public RampartSocketBuilder addRuleName(RampartString ruleName) {
        this.ruleName = ruleName;
        return this;
    }

    // @Override
    public RampartSocket createRampartRule(RampartString appName) {
        return new RampartSocketImpl(appName, ruleName, operation, action, targetOSList, metadata, dataInputs, apiFilter);
    }

    // @Override
    public RampartSocketBuilder addCode(RampartCode code) {
        // TODO to be implemented
        return this;
    }

    public RampartSocketBuilder addAction(RampartAction action) {
        this.action = action;
        return this;
    }

    /**
     * @param targetOSList RampartList of RampartConstants
     * @return
     */
    // @Override
    public RampartSocketBuilder addTargetOSList(RampartList targetOSList) {
        this.targetOSList = targetOSList;
        return this;
    }

    public RampartSocketBuilder addMetadata(RampartMetadata metadata) {
        this.metadata = metadata;
        return this;
    }

    public RampartSocketBuilder addSocketOperation(RampartSocketOperation operation) {
        this.operation = operation;
        return this;
    }

    /**
     * Adds tainting input list to this builder.
     * @param dataInputs description of data inputs.
     * @return this builder.
     */
    public RampartSocketBuilder addDataInputs(RampartList dataInputs) {
        this.dataInputs = dataInputs;
        return this;
    }


    public RampartSocketBuilder addApiFilter(RampartApiFilter apiFilter) {
        this.apiFilter = apiFilter;
        return this;
    }
}
