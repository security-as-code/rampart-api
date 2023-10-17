package org.rampart.lang.impl.socket;


import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.apiprotect.RampartApiFilter;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.api.socket.RampartSocket;
import org.rampart.lang.api.socket.RampartSocketOperation;
import org.rampart.lang.impl.apiprotect.writers.RampartApiFilterWriter;
import org.rampart.lang.impl.core.RampartActionableRuleBase;
import org.rampart.lang.impl.core.writers.v2.RampartInputWriter;
import org.rampart.lang.impl.utils.ObjectUtils;

public class RampartSocketImpl extends RampartActionableRuleBase implements RampartSocket {

    private final RampartSocketOperation operation;
    private final String toStringValue;
    private final int hashCode;
    private final RampartList dataInputsList;
    private final RampartApiFilter apiFilter;

    public RampartSocketImpl(RampartString appName, RampartString ruleName, RampartSocketOperation operation, RampartAction action,
                             RampartList targetOSList, RampartMetadata metadata, RampartList dataInputsList, RampartApiFilter apiFilter) {
        super(appName, ruleName, action, targetOSList, metadata);
        this.operation = operation;
        this.dataInputsList = dataInputsList;
        this.apiFilter = apiFilter;
        this.toStringValue = super.toString();
        this.hashCode = ObjectUtils.hash(operation, dataInputsList, apiFilter, super.hashCode());
    }

    // @Override
    public RampartRuleType getRuleType() {
        return RampartRuleType.SOCKET;
    }

    // @Override
    public RampartSocketOperation getSocketOperation() {
        return operation;
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
    public String toString() {
        return toStringValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartSocketImpl)) {
            return false;
        }
        RampartSocketImpl otherSocket = (RampartSocketImpl) other;
        return ObjectUtils.equals(operation, otherSocket.operation)
                && ObjectUtils.equals(dataInputsList, otherSocket.dataInputsList)
                && ObjectUtils.equals(apiFilter, otherSocket.apiFilter)
                && super.equals(otherSocket);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    protected void appendRuleBody(StringBuilder builder) {
        builder.append("\t").append(operation).append(LINE_SEPARATOR);
        RampartApiFilterWriter.appendTo(builder, apiFilter);
        RampartInputWriter.appendTo(builder, dataInputsList);
        super.appendRuleBody(builder);
    }
}
