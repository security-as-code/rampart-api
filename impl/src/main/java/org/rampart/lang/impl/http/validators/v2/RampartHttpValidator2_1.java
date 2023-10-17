package org.rampart.lang.impl.http.validators.v2;

import static org.rampart.lang.api.constants.RampartHttpConstants.VALIDATE_KEY;

import java.util.Map;

import org.rampart.lang.api.RampartList;

public class RampartHttpValidator2_1 extends RampartHttpValidator2_0 {

    public RampartHttpValidator2_1(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        inputValidationValidator = new HttpInputValidationValidator2_1(visitorSymbolTable.get(VALIDATE_KEY.toString()));
    }
}
