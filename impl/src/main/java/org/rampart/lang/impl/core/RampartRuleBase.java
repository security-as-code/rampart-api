package org.rampart.lang.impl.core;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.*;
import org.rampart.lang.impl.utils.ObjectUtils;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;

/**
 * Class to encapsulate common state among Rampart rules.
 */
public abstract class RampartRuleBase implements RampartRule {
    private final RampartString ruleName;
    private final RampartCode code;
    private final RampartList targetOSList;
    private RampartMetadata metadata = RampartMetadataImpl.EMPTY;
    private RampartMetadata combinedMetadata;
    private final int localHashcode;

    protected RampartRuleBase(RampartString appName, RampartString ruleName, RampartCode code, RampartList targetOSList, RampartMetadata metadata) {
        this.ruleName = ruleName;
        this.code = code;
        this.targetOSList = targetOSList != null
                ? targetOSList : newRampartList(ANY_KEY);
        if (metadata != null) {
            this.metadata = metadata;
        }
        this.localHashcode = ObjectUtils.hash(ruleName, code, targetOSList, appName, metadata);
    }

    private RampartApp app;

    void setApp(RampartApp app) {
        this.app = app;
    }

    public RampartApp getApp(){
        return app;
    }

    // @Override
    public RampartString getRuleName() {
        return ruleName;
    }

    // @Override
    public RampartCode getCode() {
        return code;
    }

    // @Override
    public RampartList getTargetOSList() {
        return targetOSList;
    }

    // @Override
    public RampartMetadata getMetadata() {
        if (combinedMetadata != null) {
            return combinedMetadata;
        }
        combinedMetadata = metadata.mergeWith(app.getMetadata());
        return combinedMetadata;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartRuleBase)) {
            return false;
        }
        RampartRuleBase otherRule = (RampartRuleBase) other;
        return ObjectUtils.equals(ruleName, otherRule.ruleName)
                && ObjectUtils.equals(code, otherRule.code)
                && ObjectUtils.equals(targetOSList, otherRule.targetOSList)
                && ObjectUtils.equals(metadata, otherRule.metadata)
                && ObjectUtils.equals(app.getAppName(), otherRule.app.getAppName());
    }

    @Override
    public int hashCode() {
        return localHashcode;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(getRuleType().getName().toString())
                .append('(').append(getRuleName().formatted());
        if (targetOSList.contains(ANY_KEY) == RampartBoolean.FALSE) {
            builder.append(", ").append(OS_KEY).append(": ").append(targetOSList);
        }
        builder.append("):").append(LINE_SEPARATOR);
        if (!toJavaBoolean(metadata.isEmpty())) {
            builder.append('\t').append(metadata.toString().replaceAll(LINE_SEPARATOR, LINE_SEPARATOR + "\t"))
                    .append(LINE_SEPARATOR);
        }
        appendRuleBody(builder);
        if (code != null) {
            builder.append('\t').append(getCode()).append(LINE_SEPARATOR);
        }
        return builder.append("end").append(getRuleType().getName()).toString();
    }

    protected abstract void appendRuleBody(StringBuilder builder);
}
