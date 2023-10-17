package org.rampart.lang.impl.patch.validators.v2;

import static org.rampart.lang.api.constants.RampartPatchConstants.*;

import java.util.Map;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.java.builder.RampartPatchBuilder;

public class RampartPatchValidator2_1 extends RampartPatchValidatorUpTo2_0 {
    public RampartPatchValidator2_1(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        functionValidator = new RampartFunctionValidator2_1(visitorSymbolTable.get(FUNCTION_KEY.toString()));
        locationValidator = new RampartLocationValidator2_1(visitorSymbolTable);
    }

    @Override
    public RampartPatchBuilder validate() throws InvalidRampartRuleException {
        super.validate();
        builder.addFunctionName(
                ((RampartFunctionValidator2_1) functionValidator).validateFunctionString(
                        builder.getRampartCode().getLanguage()));
        builder.addLocationParameter(((RampartLocationValidator2_1) locationValidator)
                .validateLocationSpecifier(rulepatchType, builder.getRampartCode().getLanguage()));
        return builder;
    }
}
