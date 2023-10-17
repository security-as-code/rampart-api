package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.apiprotect.RampartApiFilter;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartCode;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.process.RampartProcess;
import org.rampart.lang.impl.process.RampartProcessImpl;
import org.rampart.lang.java.parser.RampartSingleAppVisitor;

/**
 * Class used by the validators to build an RampartProcessImpl
 * @see RampartProcessImpl
 * @see RampartSingleAppVisitor
 * @since 1.5
 */
public class RampartProcessBuilder implements RampartRuleBuilder<RampartProcess> {
    private RampartString ruleName;
    private RampartList processList = RampartList.EMPTY;
    private RampartAction action;
    private RampartList targetOSList;
    private RampartMetadata metadata;
    private RampartList dataInputs;
    private RampartApiFilter apiFilter;

    // @Override
    public RampartProcess createRampartRule(RampartString appName) {
        return new RampartProcessImpl(appName, ruleName, processList, action, targetOSList, metadata, dataInputs,
                apiFilter);
    }

    // @Override
    public RampartProcessBuilder addRuleName(RampartString ruleName) {
        this.ruleName = ruleName;
        return this;
    }

    // @Override
    public RampartProcessBuilder addCode(RampartCode code) {
        return null; // todo: when code block support added.
    }

    public RampartProcessBuilder addAction(RampartAction action) {
        this.action = action;
        return this;
    }

    /**
     * @param targetOSList RampartList of RampartConstants
     * @return
     */
    // @Override
    public RampartProcessBuilder addTargetOSList(RampartList targetOSList) {
        this.targetOSList = targetOSList;
        return this;
    }

    public RampartProcessBuilder addMetadata(RampartMetadata metadata) {
        this.metadata = metadata;
        return this;
    }

    /**
     * @param processList RampartList of RampartStrings
     * @return
     */
    public RampartProcessBuilder addProcessList(RampartList processList) {
        this.processList = processList;
        return this;
    }


    public RampartProcessBuilder addApiFilter(RampartApiFilter apiFilter) {
        this.apiFilter = apiFilter;
        return this;
    }

    /**
     * Adds tainting input list to this builder.
     * @param dataInputs description of data inputs.
     * @return this builder.
     */
    public RampartProcessBuilder addDataInputs(RampartList dataInputs) {
        this.dataInputs = dataInputs;
        return this;
    }
}
