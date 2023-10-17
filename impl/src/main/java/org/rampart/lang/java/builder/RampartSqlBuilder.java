package org.rampart.lang.java.builder;

import static org.rampart.lang.java.RampartPrimitives.newRampartList;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartCode;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.core.RampartInput;
import org.rampart.lang.api.sql.RampartSql;
import org.rampart.lang.api.sql.RampartSqlInjectionType;
import org.rampart.lang.api.sql.RampartVendor;
import org.rampart.lang.impl.sql.RampartSqlImpl;

public class RampartSqlBuilder implements RampartRuleBuilder<RampartSql> {
    private RampartString ruleName;
    private RampartVendor vendor;
    private RampartAction action;
    private RampartList inputs = RampartList.EMPTY;
    private RampartSqlInjectionType injectionType;
    private RampartList targetOSList;
    private RampartMetadata metadata;

    //@Override
    public RampartSql createRampartRule(RampartString appName) {
        return new RampartSqlImpl(appName,
                ruleName,
                vendor,
                inputs.isEmpty() == RampartBoolean.TRUE ? newRampartList(RampartInput.HTTP) : inputs,
                injectionType,
                action,
                targetOSList,
                metadata);
    }

    public RampartSqlBuilder addVendor(RampartVendor vendor) {
        this.vendor = vendor;
        return this;
    }

    /**
     * @param inputs RampartList of RampartInputs
     * @return
     */
    public RampartSqlBuilder addDataInput(RampartList inputs) {
        this.inputs = inputs;
        return this;
    }

    public RampartSqlBuilder addInjectionType(RampartSqlInjectionType injectionType) {
        this.injectionType = injectionType;
        return this;
    }

    //@Override
    public RampartSqlBuilder addRuleName(RampartString ruleName) {
        this.ruleName = ruleName;
        return this;
    }

    //@Override
    public RampartSqlBuilder addCode(RampartCode code) {
        // TODO: support to be added for code block in RAMPART-84
        return this;
    }

    public RampartSqlBuilder addAction(RampartAction action) {
        this.action = action;
        return this;
    }

    /**
     * @param targetOSList RampartList of RampartConstants
     * @return
     */
    //@Override
    public RampartSqlBuilder addTargetOSList(RampartList targetOSList) {
        this.targetOSList = targetOSList;
        return this;
    }

    public RampartRuleBuilder<RampartSql> addMetadata(RampartMetadata metadata) {
        this.metadata = metadata;
        return this;
    }
}
