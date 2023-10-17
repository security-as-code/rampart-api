package org.rampart.lang.api.sql;

import org.rampart.lang.api.core.RampartActionableRule;
import org.rampart.lang.api.core.RampartDataInputsRule;

public interface RampartSql extends RampartActionableRule, RampartDataInputsRule {
    RampartVendor getVendor();
    RampartSqlInjectionType getSqlInjectionType();
}
