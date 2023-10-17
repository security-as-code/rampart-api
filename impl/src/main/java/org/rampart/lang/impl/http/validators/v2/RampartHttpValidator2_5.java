package org.rampart.lang.impl.http.validators.v2;

import org.rampart.lang.api.RampartList;

import java.util.Map;

public class RampartHttpValidator2_5 extends RampartHttpValidator2_3 {
    public RampartHttpValidator2_5(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        xssValidator = new HttpXssValidator2_5(visitorSymbolTable);
    }
}
