package org.rampart.lang.impl.http.validators.v2;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;

import java.util.Map;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.impl.core.RampartVersionImpl;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.impl.core.validators.RampartActionValidator;
import org.rampart.lang.impl.core.validators.RuleNameValidator;
import org.rampart.lang.impl.core.validators.v2.RampartInputValidator;
import org.rampart.lang.impl.core.validators.TargetOSValidator;
import org.rampart.lang.impl.http.RampartCsrfOptions2_5Minus;
import org.rampart.lang.java.builder.RampartHttpBuilder;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;

public class RampartHttpValidator2_0 implements Validatable<RampartHttpBuilder, InvalidRampartRuleException> {

    protected final RampartHttpBuilder builder;
    protected RampartHttpStructureValidator2_0 structureValidator;
    private final RuleNameValidator ruleNameValidator;
    private final HttpTypeValidator typeValidator;
    private final RampartInputValidator inputValidator;
    protected HttpInputValidationValidator2_0 inputValidationValidator;
    protected OpenRedirectValidator2_0 openRedirectValidator;
    private final AuthenticateValidator authenticateValidator;
    protected HttpCsrfValidator csrfValidator;
    protected HttpXssValidator2_0 xssValidator;
    protected RampartActionValidator actionValidator;
    private final TargetOSValidator targetOSValidator;

    public RampartHttpValidator2_0(Map<String, RampartList> visitorSymbolTable) {
        builder = new RampartHttpBuilder(RampartVersionImpl.v2_0);
        structureValidator = new RampartHttpStructureValidator2_0(visitorSymbolTable, builder);
        ruleNameValidator = new RuleNameValidator(visitorSymbolTable.get(HTTP_KEY.toString()));
        typeValidator = new HttpTypeValidator(visitorSymbolTable);
        inputValidator = new RampartInputValidator(visitorSymbolTable);
        inputValidationValidator = new HttpInputValidationValidator2_0(visitorSymbolTable.get(VALIDATE_KEY.toString()));
        openRedirectValidator = new OpenRedirectValidator2_0(visitorSymbolTable);
        authenticateValidator = new AuthenticateValidator(visitorSymbolTable.get(AUTHENTICATE_KEY.toString()));
        csrfValidator = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus());
        xssValidator = new HttpXssValidator2_0(visitorSymbolTable);
        actionValidator = new HttpActionValidator2_0(visitorSymbolTable);
        targetOSValidator = new TargetOSValidator(RampartInterpreterUtils
                .findRampartNamedValue(OS_KEY, visitorSymbolTable.get(HTTP_KEY.toString())));
    }

    // @Override
    public RampartHttpBuilder validate() throws InvalidRampartRuleException {
        structureValidator.feedValidators(
                typeValidator,
                inputValidator,
                inputValidationValidator,
                openRedirectValidator,
                authenticateValidator,
                csrfValidator,
                xssValidator,
                actionValidator);
        RampartString ruleName = ruleNameValidator.validateRuleName();
        structureValidator.validateDeclarations(ruleName);
        builder.addRuleName(ruleName)
                .addHttpIOType(typeValidator.validateHttpType())
                .addUriPaths(typeValidator.validateUriPaths())
                .addHttpInputValidation(inputValidationValidator.validateHttpValidationValues())
                .addOpenRedirect(openRedirectValidator.validateRedirect())
                .addAuthenticate(authenticateValidator.validateAuthenticate())
                .addDataInputs(inputValidator.validateDataInputs())
                .addCsrf(csrfValidator.validateCsrfConfiguration())
                .addXss(xssValidator.validateXssConfiguration())
                .addAction(actionValidator.validateRampartAction())
                .addTargetOSList(targetOSValidator.validateTargetOSList());
        structureValidator.crossValidate(builder);
        return builder;
    }

}
