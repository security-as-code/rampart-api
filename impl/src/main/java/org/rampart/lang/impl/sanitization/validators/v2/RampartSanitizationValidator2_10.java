package org.rampart.lang.impl.sanitization.validators.v2;

import java.util.Map;

import org.rampart.lang.api.RampartList;

public class RampartSanitizationValidator2_10 extends RampartSanitizationValidator2_8 {

    public RampartSanitizationValidator2_10(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        ignoreValidator = new RampartIgnoreValidator2_10(visitorSymbolTable);
    }

}
