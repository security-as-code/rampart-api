package org.rampart.lang.impl.sql.validators.v2;

import java.util.List;
import java.util.Map;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.impl.core.validators.v2.RampartActionValidator2_3Plus;

public class RampartSqlActionValidator2_3 extends RampartActionValidator2_3Plus {

    public RampartSqlActionValidator2_3(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable, RampartRuleType.SQL);
    }

    public List<RampartConstant> allowedKeys() {
        return RampartSqlActionValidator.SUPPORTED_ACTIONS;
    }
}
