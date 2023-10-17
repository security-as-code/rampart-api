package org.rampart.lang.impl.patch.validators.v2;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.validators.v2.RampartCodeValidator2_8;

import java.util.Map;

import static org.rampart.lang.api.constants.RampartGeneralConstants.CODE_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.SOURCE_CODE_KEY;

public class RampartPatchValidator2_8 extends RampartPatchValidator2_6 {

    public RampartPatchValidator2_8(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        this.codeValidator = new RampartCodeValidator2_8(
                visitorSymbolTable.get(CODE_KEY.toString()), visitorSymbolTable.get(SOURCE_CODE_KEY.toString()));
    }
}
