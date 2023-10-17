package org.rampart.lang.impl.patch.validators.v2;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.patch.validators.RampartPatchStructureValidator;

import java.util.Map;

import static org.rampart.lang.api.constants.RampartGeneralConstants.METADATA_KEY;
import static org.rampart.lang.java.RampartPrimitives.newRampartConstant;

public class RampartPatchStructureValidator2_6 extends RampartPatchStructureValidator {
    public RampartPatchStructureValidator2_6(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
    }

    @Override
    protected boolean keySetContainsInvalidKey(RampartList keys) {
        for (String key : visitorSymbolTable.keySet()) {
            if (keys.contains(newRampartConstant(key)) == RampartBoolean.FALSE
                && !METADATA_KEY.toString().equals(key)) {
                return true;
            }
        }
        return false;
    }
}
