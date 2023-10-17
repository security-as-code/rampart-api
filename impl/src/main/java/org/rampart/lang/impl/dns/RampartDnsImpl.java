package org.rampart.lang.impl.dns;

import static org.rampart.lang.api.constants.RampartDnsConstants.*;
import static org.rampart.lang.api.constants.RampartSocketConstants.IPV4_WILDCARD;
import static org.rampart.lang.api.constants.RampartSocketConstants.IPV6_WILDCARD;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.apiprotect.RampartApiFilter;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.api.dns.RampartDns;
import org.rampart.lang.impl.apiprotect.writers.RampartApiFilterWriter;
import org.rampart.lang.impl.core.RampartActionableRuleBase;
import org.rampart.lang.impl.core.writers.v2.RampartInputWriter;
import org.rampart.lang.impl.utils.ObjectUtils;

public class RampartDnsImpl extends RampartActionableRuleBase implements RampartDns {

    private final RampartString lookupTarget;
    private final String toStringValue;
    private final int hashCode;
    private final RampartList dataInputsList;
    private final RampartApiFilter apiFilter;

    public RampartDnsImpl(RampartString appName, RampartString ruleName, RampartAction action, RampartString lookupTarget,
                          RampartList targetOSList, RampartMetadata metadata, RampartList dataInputsList, RampartApiFilter apiFilter) {
        super(appName, ruleName, action, targetOSList, metadata);
        this.lookupTarget = lookupTarget;
        this.dataInputsList = dataInputsList;
        this.apiFilter = apiFilter;
        this.toStringValue = super.toString();
        this.hashCode = ObjectUtils.hash(lookupTarget, dataInputsList, apiFilter, super.hashCode());
    }

    // @Override
    public RampartRuleType getRuleType() {
        return RampartRuleType.DNS;
    }

    // @Override
    public RampartString getLookupTarget() {
        return lookupTarget;
    }

    // @Override
    public RampartList getDataInputs() {
        return dataInputsList;
    }

    //@Override
    public RampartApiFilter getApiFilter() {
        return apiFilter;
    }

    @Override
    protected void appendRuleBody(StringBuilder builder) {
        builder.append('\t').append("lookup(");
        if (isAllTargets()) {
            builder.append(ANY_KEY);
        } else {
            builder.append(lookupTarget.formatted());
        }
        builder.append(')').append(LINE_SEPARATOR);
        RampartApiFilterWriter.appendTo(builder, apiFilter);
        RampartInputWriter.appendTo(builder, dataInputsList);
        super.appendRuleBody(builder);
    }

    @Override
    public String toString() {
        return toStringValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartDnsImpl)) {
            return false;
        }
        RampartDnsImpl otherDns = (RampartDnsImpl) other;
        return ObjectUtils.equals(lookupTarget, otherDns.lookupTarget)
                && ObjectUtils.equals(dataInputsList, otherDns.dataInputsList)
                && ObjectUtils.equals(apiFilter, otherDns.apiFilter)
                && super.equals(otherDns);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    // @Override
    public RampartBoolean onAllTargets() {
        return isAllTargets() ? RampartBoolean.TRUE : RampartBoolean.FALSE;
    }

    private boolean isAllTargets() {
        return lookupTarget.equals(IPV4_WILDCARD)
                || lookupTarget.equals(IPV6_WILDCARD);
    }
}
