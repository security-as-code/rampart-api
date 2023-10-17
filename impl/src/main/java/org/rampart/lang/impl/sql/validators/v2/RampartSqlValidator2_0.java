package org.rampart.lang.impl.sql.validators.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.OS_KEY;
import static org.rampart.lang.api.constants.RampartSqlConstants.SQL_KEY;

import java.util.Map;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.impl.core.validators.RampartActionValidator;
import org.rampart.lang.impl.core.validators.RuleNameValidator;
import org.rampart.lang.impl.core.validators.v2.RampartInputValidator;
import org.rampart.lang.impl.core.validators.v2.RampartRuleStructureValidator2_0Plus;
import org.rampart.lang.impl.core.validators.TargetOSValidator;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;
import org.rampart.lang.java.builder.RampartSqlBuilder;

public class RampartSqlValidator2_0 implements Validatable<RampartSqlBuilder, InvalidRampartRuleException> {

    protected final RampartSqlBuilder builder;
    protected final RampartRuleStructureValidator2_0Plus structureValidator;
    private final RuleNameValidator ruleNameValidator;
    private final RampartSqlVendorValidator vendorValidator;
    private final RampartInputValidator inputValidator;
    private final RampartSqlInjectionTypeValidator injectionTypeValidator;
    protected RampartActionValidator actionValidator;
    private final TargetOSValidator targetOSValidator;

    public RampartSqlValidator2_0(Map<String, RampartList> visitorSymbolTable) {
        builder = new RampartSqlBuilder();
        this.targetOSValidator = new TargetOSValidator(
                RampartInterpreterUtils.findRampartNamedValue(OS_KEY, visitorSymbolTable.get(SQL_KEY.toString())));
        structureValidator = new RampartRuleStructureValidator2_0Plus(visitorSymbolTable, RampartRuleType.SQL);
        ruleNameValidator = new RuleNameValidator(visitorSymbolTable.get(SQL_KEY.toString()));
        vendorValidator = new RampartSqlVendorValidator(visitorSymbolTable);
        inputValidator = new RampartInputValidator(visitorSymbolTable);
        injectionTypeValidator = new RampartSqlInjectionTypeValidator(visitorSymbolTable);
        actionValidator = new RampartSqlActionValidator(visitorSymbolTable);
    }

    //@Override
    public RampartSqlBuilder validate() throws InvalidRampartRuleException {
        structureValidator.feedValidators(
                vendorValidator,
                inputValidator,
                injectionTypeValidator,
                actionValidator);
        RampartString ruleName = ruleNameValidator.validateRuleName();
        structureValidator.validateDeclarations(ruleName);
        return builder.addRuleName(ruleName)
                      .addVendor(vendorValidator.validateVendor())
                      .addDataInput(inputValidator.validateDataInputs())
                      .addInjectionType(injectionTypeValidator.validateInjectionType())
                      .addAction(actionValidator.validateRampartAction())
                      .addTargetOSList(targetOSValidator.validateTargetOSList());
    }

}
