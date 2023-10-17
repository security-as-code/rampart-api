package org.rampart.lang.impl.http.validators.v1;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.http.matchers.RampartHttpMethodMatcher;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.junit.jupiter.api.Test;

import static org.rampart.lang.api.http.RampartHttpValidationType.METHOD;
import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.impl.http.validators.v1.RampartHttpValidationValidatorUpTo1_5.VALID_INPUT_VALIDATION_TYPES;
import static org.rampart.lang.java.RampartPrimitives.newRampartNamedValue;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@SuppressWarnings("deprecation")
public class RampartHttpValidationValidator1_6Test extends RampartHttpValidationValidatorUpTo1_5Test {

    @Override
    protected RampartHttpValidationValidatorUpTo1_5 getValidator(RampartList validValues) {
        return new RampartHttpValidationValidator1_6(validValues);
    }

    @Test
    public void validateDeclarationNotMandatory() throws InvalidRampartRuleException {
        getValidator(null).validateHttpValidationType();
        getValidator(null).validateHttpValidationValues();
    }

    @Test
    public void validInputValidationEnforceValueAreValidatedSuccessfully()
            throws InvalidRampartRuleException {
        RampartList validValues = newRampartList(
                METHOD_KEY.asRampartString(),
                newRampartNamedValue(ENFORCE_KEY, newRampartList(
                        RampartHttpMethodMatcher.GET.getName().asRampartString())));
        getValidator(validValues).validateHttpValidationType();
    }

    @Test
    public void validInputValidationEnforceValueListAreValidatedSuccessfully()
            throws InvalidRampartRuleException {
        RampartList validValues = newRampartList(
                METHOD_KEY.asRampartString(),
                newRampartNamedValue(ENFORCE_KEY, newRampartList(
                        RampartHttpMethodMatcher.GET.getName().asRampartString(),
                        RampartHttpMethodMatcher.PUT.getName().asRampartString(),
                        RampartHttpMethodMatcher.POST.getName().asRampartString())));
        getValidator(validValues).validateHttpValidationType();
    }

    @Test
    public void validInputValidationListAllEnforceValuesAreValidatedSuccessfully()
            throws InvalidRampartRuleException {
        RampartList validValues = newRampartList(
                METHOD_KEY.asRampartString(), newRampartNamedValue(ENFORCE_KEY, newRampartList(
                        RampartHttpMethodMatcher.GET.getName().asRampartString(),
                        RampartHttpMethodMatcher.POST.getName().asRampartString(),
                        RampartHttpMethodMatcher.HEAD.getName().asRampartString(),
                        RampartHttpMethodMatcher.PUT.getName().asRampartString(),
                        RampartHttpMethodMatcher.DELETE.getName().asRampartString(),
                        RampartHttpMethodMatcher.CONNECT.getName().asRampartString(),
                        RampartHttpMethodMatcher.OPTIONS.getName().asRampartString(),
                        RampartHttpMethodMatcher.TRACE.getName().asRampartString(),
                        RampartHttpMethodMatcher.PATCH.getName().asRampartString())));
        getValidator(validValues).validateHttpValidationType();
    }

    @Test
    public void noInputValidationTypeThrowsException() {
        RampartList values = newRampartList(
                newRampartNamedValue(ENFORCE_KEY, newRampartList(RampartHttpMethodMatcher.GET.getName().asRampartString())));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getValidator(values).validateHttpValidationType());

        assertThat(thrown.getMessage(),
                equalTo("http validate values must contain one, and only one, of: " + newRampartList(
                        VALID_INPUT_VALIDATION_TYPES).addAll(newRampartList(METHOD_KEY))));
    }

    @Test
    public void methodNoEnforceParameter() throws InvalidRampartRuleException {
        RampartList values = newRampartList(METHOD_KEY.asRampartString());
        RampartHttpValidationValidatorUpTo1_5 validator = getValidator(values);
        validator.validateHttpValidationType();
        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class, validator::validateHttpValidationValues);

        assertThat(thrown.getMessage(), equalTo("a list of values must be specified after the enforce keyword"));
    }

    @Test
    public void invalidHttpValidationValueListThrowsException() {
        RampartList values = newRampartList(
                newRampartNamedValue(METHOD_KEY, RampartList.EMPTY),
                newRampartNamedValue(ENFORCE_KEY, RampartHttpMethodMatcher.GET.getName().asRampartString()));
        RampartHttpValidationValidatorUpTo1_5 validatorSpy = spy(getValidator(values));
        doReturn(METHOD).when(validatorSpy).getHttpValidationType();

        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class, validatorSpy::validateHttpValidationValues);

        assertThat(thrown.getMessage(),
                equalTo("http validation type \"" + METHOD_KEY + "\" must be a string literal"));
    }

    @Test
    public void emptyEnforceValuesListThrowsException() {
        RampartList values = newRampartList(
                METHOD_KEY.asRampartString(),
                newRampartNamedValue(ENFORCE_KEY, RampartList.EMPTY));
        RampartHttpValidationValidatorUpTo1_5 validatorSpy = spy(getValidator(values));
        doReturn(METHOD).when(validatorSpy).getHttpValidationType();

        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class, validatorSpy::validateHttpValidationValues);

        assertThat(thrown.getMessage(), equalTo("enforce value list cannot be empty"));
    }

    @Test
    public void invalidEnforceValuesTypeThrowsException() {
        RampartList values = newRampartList(
                METHOD_KEY.asRampartString(),
                newRampartNamedValue(ENFORCE_KEY, newRampartString("integer")));
        RampartHttpValidationValidatorUpTo1_5 validatorSpy = spy(getValidator(values));
        doReturn(METHOD).when(validatorSpy).getHttpValidationType();

        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class, validatorSpy::validateHttpValidationValues);

        assertThat(thrown.getMessage(), equalTo("a list of values must be specified after the enforce keyword"));
    }

    @Test
    public void invalidEnforceValuesThrowsException() {
        RampartList values = newRampartList(
                METHOD_KEY.asRampartString(),
                newRampartNamedValue(ENFORCE_KEY, newRampartList(newRampartString("integer"))));
        RampartHttpValidationValidatorUpTo1_5 validatorSpy = spy(getValidator(values));
        doReturn(METHOD).when(validatorSpy).getHttpValidationType();

        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class, validatorSpy::validateHttpValidationValues);

        assertThat(thrown.getMessage(), equalTo("\"integer\" is an invalid enforcement type"));
    }

    @Test
    public void multipleInputValidationTypesThrowsException() {
        RampartList values = newRampartList(
                newRampartNamedValue(PARAMETER_KEY, newRampartList(newRampartString("parameter-name"))),
                newRampartNamedValue(COOKIE_KEY, newRampartList(newRampartString("cookie-name"))),
                newRampartNamedValue(ENFORCE_KEY, newRampartString("integer")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getValidator(values).validateHttpValidationType());

        assertThat(thrown.getMessage(),
                equalTo("http validate values must contain one, and only one, of: " + newRampartList(
                        VALID_INPUT_VALIDATION_TYPES).addAll(newRampartList(METHOD_KEY))));
    }

}
