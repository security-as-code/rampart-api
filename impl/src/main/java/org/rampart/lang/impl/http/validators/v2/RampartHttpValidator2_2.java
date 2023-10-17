package org.rampart.lang.impl.http.validators.v2;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.java.builder.RampartHttpBuilder;

import java.util.Map;

import static org.rampart.lang.api.constants.RampartGeneralConstants.INJECTION_KEY;
import static org.rampart.lang.api.constants.RampartHttpConstants.VALIDATE_KEY;

public class RampartHttpValidator2_2 extends RampartHttpValidator2_1 {
    private final RampartHttpInjectionTypeValidator2_2 injectionTypeValidator;

    public RampartHttpValidator2_2(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        inputValidationValidator = new HttpInputValidationValidator2_2(visitorSymbolTable.get(VALIDATE_KEY.toString()));
        injectionTypeValidator = new RampartHttpInjectionTypeValidator2_2(visitorSymbolTable.get(INJECTION_KEY.toString()));
        openRedirectValidator = new OpenRedirectValidator2_2(visitorSymbolTable);
    }

    @Override
    public RampartHttpBuilder validate() throws InvalidRampartRuleException {
        structureValidator.feedValidators(inputValidationValidator, injectionTypeValidator);
        builder.addInjectionType(injectionTypeValidator.validateInjection())
               .addOpenRedirect(((OpenRedirectValidator2_2) openRedirectValidator).validateRedirectDeclaration());
        return super.validate();
    }
}
