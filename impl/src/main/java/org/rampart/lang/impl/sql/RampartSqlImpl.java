package org.rampart.lang.impl.sql;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.api.core.RampartRuleType.SQL;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.api.sql.RampartSql;
import org.rampart.lang.api.sql.RampartSqlInjectionType;
import org.rampart.lang.api.sql.RampartVendor;
import org.rampart.lang.impl.core.RampartActionableRuleBase;
import org.rampart.lang.impl.utils.ObjectUtils;

public class RampartSqlImpl extends RampartActionableRuleBase implements RampartSql {
    private final RampartList inputs;
    private final RampartSqlInjectionType injectionType;
    private final RampartVendor vendor;
    private final String toStringValue;
    private final int hashCode;

    public RampartSqlImpl(RampartString appName, RampartString ruleName, RampartVendor vendor, RampartList inputs, RampartSqlInjectionType injectionType,
                          RampartAction action, RampartList targetOSList, RampartMetadata metadata) {
        super(appName, ruleName, action, targetOSList, metadata);
        this.vendor = vendor;
        this.inputs = inputs;
        this.injectionType = injectionType;
        this.toStringValue = super.toString();
        this.hashCode = ObjectUtils.hash(inputs, injectionType, vendor, super.hashCode());
    }

    //@Override
    public RampartRuleType getRuleType() {
        return SQL;
    }

    //@Override
    public RampartVendor getVendor() {
        return vendor;
    }

    //@Override
    public RampartList getDataInputs() {
        return inputs;
    }

    //@Override
    public RampartSqlInjectionType getSqlInjectionType() {
        return injectionType;
    }

    @Override
    public String toString() {
        return toStringValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartSqlImpl)) {
            return false;
        }
        RampartSqlImpl otherSql = (RampartSqlImpl) other;
        return ObjectUtils.equals(inputs, otherSql.inputs)
                && ObjectUtils.equals(injectionType, otherSql.injectionType)
                && ObjectUtils.equals(vendor, otherSql.vendor)
                && super.equals(otherSql);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    protected void appendRuleBody(StringBuilder builder) {
        builder.append('\t').append(vendor).append(LINE_SEPARATOR);
        appendInputsToBuilder(builder).append(LINE_SEPARATOR);
        builder.append('\t').append(injectionType).append(LINE_SEPARATOR);
        super.appendRuleBody(builder);
    }

    private StringBuilder appendInputsToBuilder(StringBuilder builder) {
        builder.append('\t').append(INPUT_KEY).append('(');
        String delim = "";
        RampartObjectIterator it = inputs.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            builder.append(delim).append(it.next());
            delim = ", ";
        }
        return builder.append(')');
    }
}
