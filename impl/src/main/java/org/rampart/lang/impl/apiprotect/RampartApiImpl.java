package org.rampart.lang.impl.apiprotect;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.apiprotect.RampartApi;
import org.rampart.lang.api.core.RampartCode;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.api.http.RampartHttpIOType;
import org.rampart.lang.impl.core.RampartRuleBase;
import org.rampart.lang.impl.utils.ObjectUtils;

/**
 * Implementation of the RampartApi rule.
 */
public class RampartApiImpl extends RampartRuleBase implements RampartApi {
    private final RampartList uriPatterns;
    private final RampartHttpIOType requestProcessingStage;
    private final int hashCode;

    public RampartApiImpl(RampartString appName, RampartString ruleName, RampartCode code, RampartList targetOSList,
                          RampartMetadata metadata, RampartList uriPatterns, RampartHttpIOType requestProcessingStage) {
        super(appName, ruleName, code, targetOSList, metadata);
        this.uriPatterns = uriPatterns;
        this.requestProcessingStage = requestProcessingStage;
        this.hashCode = ObjectUtils.hash(uriPatterns, requestProcessingStage, super.hashCode());
    }

    //@Override
    public RampartList getUriPatterns() {
        return uriPatterns;
    }

    //@Override
    public RampartHttpIOType getRequestProcessingStage() {
        return requestProcessingStage;
    }

    //@Override
    public RampartRuleType getRuleType() {
        return RampartRuleType.API;
    }


    //@Override
    protected void appendRuleBody(StringBuilder builder) {
        builder
            .append('\t')
            .append(requestProcessingStage)
            .append('(');
        appendUriPatterns(builder);
        builder
            .append(')')
            .append(LINE_SEPARATOR);
    }

    //@Override
    public int hashCode() {
        return hashCode;
    }

    //@Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RampartApiImpl)) {
            return false;
        }
        final RampartApiImpl otherApi = (RampartApiImpl) obj;
        return
            ObjectUtils.equals(uriPatterns, otherApi.uriPatterns)
            && ObjectUtils.equals(requestProcessingStage, otherApi.requestProcessingStage)
            && super.equals(obj);
    }


    /** Appends URI patterns to the builder. */
    private void appendUriPatterns(StringBuilder builder) {
        final RampartObjectIterator iterator = getUriPatterns().getObjectIterator();

        if (iterator.hasNext() == RampartBoolean.FALSE) {
            return;
        }

        appendObject(iterator.next(), builder);

        while (iterator.hasNext() == RampartBoolean.TRUE) {
            builder.append(", ");
            appendObject(iterator.next(), builder);
        }
    }


    /** Appends object (which may be RampartString) to the builder. */
    private void appendObject(RampartObject element, StringBuilder builder) {
        if (element instanceof RampartString) {
            builder.append(((RampartString) element).formatted());
        } else {
            builder.append(element);
        }
    }
}
