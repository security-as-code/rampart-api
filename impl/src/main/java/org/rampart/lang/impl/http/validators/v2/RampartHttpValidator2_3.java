package org.rampart.lang.impl.http.validators.v2;

import org.rampart.lang.api.RampartList;

import java.util.Map;

public class RampartHttpValidator2_3 extends RampartHttpValidator2_2 {

    public RampartHttpValidator2_3(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        actionValidator = new HttpActionValidator2_3(visitorSymbolTable);
    }

}
