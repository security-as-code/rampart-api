package org.rampart.lang.impl.sanitization.validators.v2;

import java.util.Map;

import org.rampart.lang.api.RampartList;

public class RampartSanitizationValidator2_8 extends RampartSanitizationValidator2_6 {

    public RampartSanitizationValidator2_8(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        undeterminedValidator = new RampartUndeterminedValidator2_8(visitorSymbolTable);
    }

}
