package org.rampart.lang.impl.marshal.validators.v2;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import java.util.Map;

public class DeserializeTypeValidator2_6 extends DeserializeTypeValidator2_3 {

    public DeserializeTypeValidator2_6(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
    }

    // Override
    public RampartList validateDeserialType() throws InvalidRampartRuleException {
        if (validatableObject == null) {
            // NOTE:    As of RAMPART/2.6 and the introduction of the XXE
            //          configuration for the Marshal rule, it is now
            //          OK to return 'null' for the DeserialType.
            return null;
        }
        return super.validateDeserialType();
    }

}
