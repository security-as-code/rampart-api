package org.rampart.lang.impl.sql.validators.v2;

import org.rampart.lang.api.RampartList;

import java.util.Map;

public class RampartSqlValidator2_4 extends RampartSqlValidator2_3 {
    public RampartSqlValidator2_4(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        actionValidator = new RampartSqlActionValidator2_4(visitorSymbolTable);
    }
}
