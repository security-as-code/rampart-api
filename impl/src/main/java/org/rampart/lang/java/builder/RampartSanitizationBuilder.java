package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartCode;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.http.RampartHttpIOType;
import org.rampart.lang.api.sanitization.RampartIgnore;
import org.rampart.lang.api.sanitization.RampartSanitization;
import org.rampart.lang.api.sanitization.RampartUndetermined;
import org.rampart.lang.impl.sanitization.RampartSanitizationImpl;

public class RampartSanitizationBuilder implements RampartRuleBuilder<RampartSanitization> {

    private RampartString ruleName;
    private RampartAction action;
    private RampartList targetOSList;
    private RampartList uriPaths = RampartList.EMPTY;
    private RampartHttpIOType httpIOType;
    private RampartUndetermined undetermined;
    private RampartIgnore ignore;
    private RampartMetadata metadata;

    //@Override
    public RampartSanitization createRampartRule(RampartString appName) {
        return new RampartSanitizationImpl(
                appName, ruleName, action, targetOSList, httpIOType,
                uriPaths, undetermined, ignore, metadata);
    }

    //@Override
    public RampartSanitizationBuilder addRuleName(RampartString ruleName) {
        this.ruleName = ruleName;
        return this;
    }

    //@Override
    public RampartSanitizationBuilder addCode(RampartCode code) {
        // TODO: support to be added for code block in RAMPART-84
        return this;
    }

    //@Override
    public RampartSanitizationBuilder addAction(RampartAction action) {
        this.action = action;
        return this;
    }

    /**
     * @param targetOSList RampartList of RampartConstants
     * @return
     */
    //@Override
    public RampartSanitizationBuilder addTargetOSList(RampartList targetOSList) {
        this.targetOSList = targetOSList;
        return this;
    }

    public RampartRuleBuilder<RampartSanitization> addMetadata(RampartMetadata metadata) {
        this.metadata = metadata;
        return this;
    }

    //@Override
    public RampartSanitizationBuilder addHttpIOType(RampartHttpIOType httpIOType) {
        this.httpIOType = httpIOType;
        return this;
    }

    /**
     * @param paths RampartList of RampartStrings
     * @return
     */
    //@Override
    public RampartSanitizationBuilder addUriPaths(RampartList paths) {
        this.uriPaths = paths;
        return this;
    }

    //@Override
    public RampartSanitizationBuilder addUndetermined(RampartUndetermined undetermined) {
        this.undetermined = undetermined;
        return this;
    }

    //@Override
    public RampartSanitizationBuilder addIgnore(RampartIgnore ignore) {
        this.ignore = ignore;
        return this;
    }

}
