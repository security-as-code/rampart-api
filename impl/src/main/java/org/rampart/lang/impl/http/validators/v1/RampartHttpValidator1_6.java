package org.rampart.lang.impl.http.validators.v1;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.http.RampartHttpFeaturePattern;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.v1.RampartHttpStructureValidator1_6;
import org.rampart.lang.java.builder.RampartHttpBuilder;

import java.util.List;
import java.util.Map;

import static org.rampart.lang.api.constants.RampartGeneralConstants.ACTION_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.INJECTION_KEY;
import static org.rampart.lang.api.constants.RampartHttpConstants.VALIDATE_KEY;

public class RampartHttpValidator1_6 extends RampartHttpValidator1_5 {

    private final RampartHttpInjectionTypeValidator1_6 injectionTypeValidator;

    public RampartHttpValidator1_6(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        httpStructureValidator = new RampartHttpStructureValidator1_6(visitorSymbolTable);
        httpValidationValidator = new RampartHttpValidationValidator1_6(visitorSymbolTable.get(VALIDATE_KEY.toString()));
        injectionTypeValidator = new RampartHttpInjectionTypeValidator1_6(visitorSymbolTable.get(INJECTION_KEY.toString()));
        actionValidator = new RampartHttpActionValidator1_6(visitorSymbolTable.get(ACTION_KEY.toString()));
    }

    @Override
    public RampartHttpBuilder validate() throws InvalidRampartRuleException {
        super.validate();
        builder.addInjectionType(injectionTypeValidator.validateInjectionType());
        injectionTypeValidator.validateForActions(builder.getRampartAction());
        List<RampartHttpFeaturePattern> securityFeatures = builder.getDeclaredSecurityFeatures();
        if (securityFeatures.size() != 1) {
            if (securityFeatures.size() == 0) {
                throw new IllegalStateException(
                        "should not happen - no security feature identified from RAMPART http rule");
            }
            // Only needs first two for a message
            throw new InvalidRampartRuleException(
                    "\"" + securityFeatures.get(0).getDeclarationTerm() + "\" and \"" + securityFeatures.get(1)
                            .getDeclarationTerm() + "\" cannot be declared together");
        }
        return builder;
    }

}
