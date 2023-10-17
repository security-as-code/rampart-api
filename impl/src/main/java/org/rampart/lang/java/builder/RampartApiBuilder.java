package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.apiprotect.RampartApi;
import org.rampart.lang.api.core.RampartCode;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.http.RampartHttpIOType;
import org.rampart.lang.impl.apiprotect.RampartApiImpl;

/**
 * Builder of the RAMPART API Protect rule.
 */
public final class RampartApiBuilder implements RampartRuleBuilder<RampartApi> {
    private RampartString ruleName;
    private RampartCode code;
    private RampartList targetOSList;
    private RampartMetadata metadata;
    private RampartList uriPatterns;
    private RampartHttpIOType requestProcessingStage;

    //@Override
    public RampartApi createRampartRule(RampartString appName) {
        return new RampartApiImpl(appName, ruleName, code, targetOSList, metadata, uriPatterns, requestProcessingStage);
    }

    //@Override
    public RampartApiBuilder addRuleName(RampartString ruleName) {
        this.ruleName = ruleName;
        return this;
    }

    //@Override
    public RampartApiBuilder addCode(RampartCode code) {
        this.code = code;
        return this;
    }

    //@Override
    public RampartApiBuilder addTargetOSList(RampartList targetOSList) {
        this.targetOSList = targetOSList;
        return this;
    }

    //@Override
    public RampartApiBuilder addMetadata(RampartMetadata metadata) {
        this.metadata = metadata;
        return this;
    }

    public RampartApiBuilder addUriPatterns(RampartList uriPatterns) {
        this.uriPatterns = uriPatterns;
        return this;
    }

    public RampartApiBuilder addRequestProcessingStage(RampartHttpIOType requestProcessingStage) {
        this.requestProcessingStage = requestProcessingStage;
        return this;
    }
}
