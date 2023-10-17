package org.rampart.lang.impl.patch.validators.v1;

import static org.rampart.lang.api.constants.RampartPatchConstants.*;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.TargetOSValidator;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;
import org.rampart.lang.java.builder.RampartPatchBuilder;

import java.util.Map;

public class RampartPatchValidator1_5 extends RampartPatchValidator1_3 {
    private final TargetOSValidator targetOSValidator;

    public RampartPatchValidator1_5(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        this.targetOSValidator = new TargetOSValidator(RampartInterpreterUtils.
                findRampartNamedValue(OS_KEY, visitorSymbolTable.get(PATCH_KEY.toString())));
    }

    @Override
    public RampartPatchBuilder validate() throws InvalidRampartRuleException {
        super.validate();
        builder.addTargetOSList(targetOSValidator.validateTargetOSList());
        return builder;
    }
}
