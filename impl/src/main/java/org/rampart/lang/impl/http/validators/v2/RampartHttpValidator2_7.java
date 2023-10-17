package org.rampart.lang.impl.http.validators.v2;

import java.util.Map;

import org.rampart.lang.api.RampartList;

public class RampartHttpValidator2_7 extends RampartHttpValidator2_6 {

    public RampartHttpValidator2_7(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        structureValidator = new RampartHttpStructureValidator2_7(visitorSymbolTable, builder);
        openRedirectValidator = new OpenRedirectValidator2_7(visitorSymbolTable);
    }

}
