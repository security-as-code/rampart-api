package org.rampart.lang.impl.patch.validators.v1;

import static org.rampart.lang.api.constants.RampartPatchConstants.*;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.patch.RampartPatchType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.impl.core.validators.RuleNameValidator;
import org.rampart.lang.impl.core.validators.v1.RampartCodeValidatorUpTo1_5;
import org.rampart.lang.java.builder.RampartPatchBuilder;
import org.rampart.lang.impl.patch.validators.v2.RampartFunctionValidatorUpTo2_0;
import org.rampart.lang.impl.patch.validators.v2.RampartLocationValidatorUpTo2_0;
import org.rampart.lang.impl.patch.validators.RampartPatchStructureValidator;

import java.util.Map;

/**
 * Class to validate an RAMPART patch containing features introduced at RAMPART/1.1
 * Note: This class is also used to validate legacy version 1.0 Rampart apps
 */
public class RampartPatchValidator1_1 implements Validatable<RampartPatchBuilder, InvalidRampartRuleException> {
    protected final RampartPatchBuilder builder;

    protected RampartPatchStructureValidator patchStructureValidator;
    private final RuleNameValidator ruleNameValidator;
    protected RampartFunctionValidatorUpTo2_0 functionValidator;
    protected RampartLocationValidatorUpTo2_0 locationValidator;
    protected RampartCodeValidatorUpTo1_5 codeValidator;

    protected RampartPatchType rulepatchType;

    public RampartPatchValidator1_1(Map<String, RampartList> visitorSymbolTable) {
        this.builder = new RampartPatchBuilder();
        this.patchStructureValidator = new RampartPatchStructureValidator(visitorSymbolTable);
        this.ruleNameValidator = new RuleNameValidator(visitorSymbolTable.get(PATCH_KEY.toString()));
        this.functionValidator = new RampartFunctionValidatorUpTo2_0(visitorSymbolTable.get(FUNCTION_KEY.toString()));
        this.codeValidator = new RampartCodeValidatorUpTo1_5(
                visitorSymbolTable.get(CODE_KEY.toString()), visitorSymbolTable.get(SOURCE_CODE_KEY.toString()));
        this.locationValidator = new RampartLocationValidatorUpTo2_0(visitorSymbolTable);
    }

    public RampartPatchBuilder validate() throws InvalidRampartRuleException {
        rulepatchType = patchStructureValidator.validatePatchStructure();
        builder.addLocationType(rulepatchType);
        builder.addRuleName(ruleNameValidator.validateRuleName());
        builder.addFunctionName(functionValidator.validateFunctionString());
        builder.addLocationParameter(locationValidator.validateLocationSpecifier(rulepatchType));
        builder.addCode(codeValidator.validateCodeBlock());
        return builder;
    }

}
