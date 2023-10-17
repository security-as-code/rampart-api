package org.rampart.lang.impl.http.validators.v1;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.http.RampartHttpIOType;
import org.rampart.lang.api.http.RampartHttpValidationType;
import org.rampart.lang.impl.core.RampartVersionImpl;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.impl.core.validators.RuleNameValidator;
import org.rampart.lang.java.builder.RampartHttpBuilder;

import java.util.Map;
/**
 * Class to validate an RAMPART http rule containing features introduced at RAMPART/1.4
 */
public class RampartHttpValidator1_4 implements Validatable<RampartHttpBuilder, InvalidRampartRuleException> {
    protected final RampartHttpBuilder builder;

    protected RampartHttpStructureValidatorUpTo1_5 httpStructureValidator;
    private final RuleNameValidator ruleNameValidator;
    private final RampartUriValidatorUpTo1_5 uriValidator;
    protected RampartHttpValidationValidatorUpTo1_5 httpValidationValidator;
    protected RampartHttpActionValidatorUpTo1_5 actionValidator;

    public RampartHttpValidator1_4(Map<String, RampartList> visitorSymbolTable) {
        builder = new RampartHttpBuilder(RampartVersionImpl.v1_5);
        httpStructureValidator = new RampartHttpStructureValidatorUpTo1_5(visitorSymbolTable);
        ruleNameValidator = new RuleNameValidator(visitorSymbolTable.get(HTTP_KEY.toString()));
        uriValidator = new RampartUriValidatorUpTo1_5(visitorSymbolTable);
        httpValidationValidator = new RampartHttpValidationValidatorUpTo1_5(visitorSymbolTable.get(VALIDATE_KEY.toString()));
        actionValidator = new RampartHttpActionValidatorUpTo1_5(visitorSymbolTable.get(ACTION_KEY.toString()));
    }

    //@Override
    public RampartHttpBuilder validate() throws InvalidRampartRuleException {
        RampartHttpIOType httpIOType = httpStructureValidator.validateHttpStructure();
        builder.addHttpIOType(httpIOType);
        builder.addRuleName(ruleNameValidator.validateRuleName());
        builder.addUriPaths(uriValidator.validateUriValues(httpIOType));
        RampartHttpValidationType httpValidationType =
                httpValidationValidator.validateHttpValidationType();
        builder.addHttpValidationType(httpValidationType);
        builder.addHttpValidationMap(httpValidationValidator.validateHttpValidationValues());
        builder.addAction(actionValidator.validateHttpAction(httpValidationType));
        return builder;
    }
}
