package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartCode;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.library.RampartLibrary;
import org.rampart.lang.impl.library.RampartLibraryImpl;

public class RampartLibraryBuilder implements RampartRuleBuilder<RampartLibrary> {
    private RampartString ruleName;
    private RampartList libraryList = RampartList.EMPTY;
    private RampartAction ruleAction;
    private RampartList targetOSList;
    private RampartMetadata metadata;

    // @Override
    public RampartLibrary createRampartRule(RampartString appName) {
        return new RampartLibraryImpl(appName, ruleName, libraryList, ruleAction, targetOSList, metadata);
    }

    // @Override
    public RampartLibraryBuilder addRuleName(RampartString ruleName) {
        this.ruleName = ruleName;
        return this;
    }

    // @Override
    public RampartLibraryBuilder addCode(RampartCode code) {
        return this; // todo: When we have generic codeblock support.
    }

    public RampartLibraryBuilder addAction(RampartAction action) {
        this.ruleAction = action;
        return this;
    }

    /**
     * @param targetOSList RampartList of RampartConstants
     * @return
     */
    // @Override
    public RampartLibraryBuilder addTargetOSList(RampartList targetOSList) {
        this.targetOSList = targetOSList;
        return this;
    }

    public RampartLibraryBuilder addMetadata(RampartMetadata metadata) {
        this.metadata = metadata;
        return this;
    }

    /**
     * @param libraryList RampartList of RampartStrings
     * @return
     */
    public RampartLibraryBuilder addLibraryList(RampartList libraryList) {
        this.libraryList = libraryList;
        return this;
    }

}
