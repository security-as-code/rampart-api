package org.rampart.lang.impl.sanitization.validators.v2;

import static org.rampart.lang.api.core.RampartRuleType.SANITIZATION;
import static org.rampart.lang.api.constants.RampartGeneralConstants.OS_KEY;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.http.RampartHttpIOType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.impl.core.validators.v2.RampartRuleStructureValidator2_0Plus;
import org.rampart.lang.impl.core.validators.RuleNameValidator;
import org.rampart.lang.impl.core.validators.TargetOSValidator;
import org.rampart.lang.impl.http.validators.v2.HttpTypeValidator;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;
import org.rampart.lang.java.builder.RampartSanitizationBuilder;

import java.util.Map;

public class RampartSanitizationValidator2_3 implements Validatable<RampartSanitizationBuilder, InvalidRampartRuleException> {

    protected final RampartSanitizationBuilder builder;
    private final TargetOSValidator targetOSValidator;
    protected final RampartRuleStructureValidator2_0Plus structureValidator;
    private final RuleNameValidator ruleNameValidator;
    private final HttpTypeValidator httpTypeValidator;
    protected RampartUndeterminedValidator2_3 undeterminedValidator;
    protected RampartIgnoreValidator2_3 ignoreValidator;
    private final RampartSanitizationActionValidator actionValidator;

    public RampartSanitizationValidator2_3(
            Map<String, RampartList> visitorSymbolTable) {
        builder = new RampartSanitizationBuilder();
        targetOSValidator = new TargetOSValidator(
                RampartInterpreterUtils.findRampartNamedValue(OS_KEY, visitorSymbolTable.get(SANITIZATION.getName().toString())));
        structureValidator = new RampartRuleStructureValidator2_0Plus(visitorSymbolTable, SANITIZATION);
        ruleNameValidator = new RuleNameValidator(visitorSymbolTable.get(SANITIZATION.getName().toString()));
        httpTypeValidator = new HttpTypeValidator(visitorSymbolTable);
        undeterminedValidator = new RampartUndeterminedValidator2_3(visitorSymbolTable);
        ignoreValidator = new RampartIgnoreValidator2_3(visitorSymbolTable);
        actionValidator = new RampartSanitizationActionValidator(visitorSymbolTable);
    }

    //@Override
    public RampartSanitizationBuilder validate() throws InvalidRampartRuleException {
        structureValidator.feedValidators(
                httpTypeValidator,
                undeterminedValidator,
                ignoreValidator,
                actionValidator);
        RampartString ruleName = ruleNameValidator.validateRuleName();
        structureValidator.validateDeclarations(ruleName);
        RampartHttpIOType httpIOType = httpTypeValidator.validateHttpType();
        if (!RampartHttpIOType.REQUEST.equals(httpIOType)) {
            throw new InvalidRampartRuleException(
                    httpIOType.getName().asRampartString().formatted()
                    + " is not valid directive for RampartSanitization. Must be "
                    + RampartHttpIOType.REQUEST.getName().asRampartString().formatted());
        }
        return builder.addRuleName(ruleName)
                      .addAction(actionValidator.validateRampartAction())
                      .addTargetOSList(targetOSValidator.validateTargetOSList())
                      .addHttpIOType(httpTypeValidator.validateHttpType())
                      .addUriPaths(httpTypeValidator.validateUriPaths())
                      .addUndetermined(undeterminedValidator.validate())
                      .addIgnore(ignoreValidator.validate());
    }

}
