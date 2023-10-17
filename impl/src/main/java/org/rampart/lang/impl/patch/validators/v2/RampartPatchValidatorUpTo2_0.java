package org.rampart.lang.impl.patch.validators.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;

import java.util.Map;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.validators.v2.RampartCodeValidator2_0;
import org.rampart.lang.impl.patch.validators.v1.RampartPatchValidator1_5;

public class RampartPatchValidatorUpTo2_0 extends RampartPatchValidator1_5 {

    public RampartPatchValidatorUpTo2_0(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        this.codeValidator = new RampartCodeValidator2_0(
                visitorSymbolTable.get(CODE_KEY.toString()), visitorSymbolTable.get(SOURCE_CODE_KEY.toString()));
    }
}
