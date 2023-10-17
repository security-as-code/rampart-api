package org.rampart.lang.impl.sql.validators.v2;

import java.util.Map;

import org.rampart.lang.api.RampartList;

public class RampartSqlValidator2_3 extends RampartSqlValidator2_0 {

    public RampartSqlValidator2_3(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        actionValidator = new RampartSqlActionValidator2_3(visitorSymbolTable);
    }

}
