package org.rampart.lang.impl.http.validators.v1;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.TargetOSValidator;
import org.rampart.lang.java.builder.RampartHttpBuilder;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;
import java.util.Map;

public class RampartHttpValidator1_5 extends RampartHttpValidator1_4 {
    private final TargetOSValidator targetOSValidator;

    public RampartHttpValidator1_5(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        this.targetOSValidator = new TargetOSValidator(RampartInterpreterUtils
                .findRampartNamedValue(OS_KEY, visitorSymbolTable.get(HTTP_KEY.toString())));
    }

    @Override
    public RampartHttpBuilder validate() throws InvalidRampartRuleException {
        super.validate();
        builder.addTargetOSList(targetOSValidator.validateTargetOSList());
        return builder;
    }
}
