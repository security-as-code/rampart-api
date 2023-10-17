package org.rampart.lang.impl.http.validators.v1;

import static org.rampart.lang.api.http.RampartHttpValidationType.*;
import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.impl.http.validators.v1.RampartHttpValidationValidatorUpTo1_5.VALID_INPUT_VALIDATION_TYPES;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import org.rampart.lang.api.http.matchers.RampartHttpMethodMatcher;
import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

import java.util.Arrays;

@SuppressWarnings("deprecation")
public class RampartHttpValidationValidatorUpTo1_5Test {

    protected RampartHttpValidationValidatorUpTo1_5 getValidator(RampartList validValues) {
        return new RampartHttpValidationValidatorUpTo1_5(validValues);
    }

    @Test
    public void validInputValidationValuesAreValidatedSuccessfully()
            throws InvalidRampartRuleException {
        RampartList validValues = newRampartList(
                newRampartNamedValue(COOKIE_KEY, newRampartList(newRampartString("cookie-name"))),
                newRampartNamedValue(ENFORCE_KEY, newRampartList(newRampartString("integer"))));
        getValidator(validValues).validateHttpValidationType();
    }

    @Test
    public void validCsrfValidationValuesAreValidatedSuccessfully()
            throws InvalidRampartRuleException {
        RampartList validValues = newRampartList(
                newRampartNamedValue(CSRF_KEY, newRampartList(ORIGINS_KEY.asRampartString())),
                newRampartNamedValue(HOSTS_KEY, newRampartList(newRampartString("ws11.rampart.lan"))));
        getValidator(validValues).validateHttpValidationType();
    }

    @Test
    public void csrfValidationValuesAreValidatedSuccessfullyWithoutHosts()
            throws InvalidRampartRuleException {
        RampartList validValues = newRampartList(
                newRampartNamedValue(CSRF_KEY, newRampartList(ORIGINS_KEY.asRampartString())));
        RampartHttpValidationValidatorUpTo1_5 validatorSpy = spy(getValidator(validValues));
        doReturn(CSRF).when(validatorSpy).getHttpValidationType();
        validatorSpy.validateHttpValidationValues();
    }

    @Test
    public void csrfValidationWithInvalidHostsTypeThrowsException() {
        RampartList validValues = newRampartList(
                newRampartNamedValue(CSRF_KEY, newRampartList(ORIGINS_KEY.asRampartString())),
                newRampartNamedValue(HOSTS_KEY, newRampartString("ws11.rampart.lan")));
        RampartHttpValidationValidatorUpTo1_5 validatorSpy = spy(getValidator(validValues));
        doReturn(CSRF).when(validatorSpy).getHttpValidationType();

        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class, validatorSpy::validateHttpValidationValues);

        assertThat(thrown.getMessage(), equalTo("a list of values must be specified after the hosts keyword"));
    }

    @Test
    public void noInputValidationTypeThrowsException() {
        RampartList values = newRampartList(
                newRampartNamedValue(ENFORCE_KEY, newRampartString("integer")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getValidator(values).validateHttpValidationType());

        assertThat(thrown.getMessage(), equalTo("http validate values must contain one, and only one, of: "
                + Arrays.toString(VALID_INPUT_VALIDATION_TYPES)));
    }

    @Test
    public void multipleInputValidationTypesThrowsException() {
        RampartList values = newRampartList(
                newRampartNamedValue(PARAMETER_KEY, newRampartList(newRampartString("parameter-name"))),
                newRampartNamedValue(COOKIE_KEY, newRampartList(newRampartString("cookie-name"))),
                newRampartNamedValue(ENFORCE_KEY, newRampartString("integer")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpValidationValidatorUpTo1_5(values).validateHttpValidationType());

        assertThat(thrown.getMessage(), equalTo("http validate values must contain one, and only one, of: "
                + Arrays.toString(VALID_INPUT_VALIDATION_TYPES)));
    }

    @Test
    public void noInputValidationParameterListThrowsException() {
        RampartList values = newRampartList(
                newRampartNamedValue(COOKIE_KEY, newRampartString("cookie-name")),
                newRampartNamedValue(ENFORCE_KEY, newRampartString("integer")));
        RampartHttpValidationValidatorUpTo1_5 validatorSpy = spy(getValidator(values));
        doReturn(HTTP_COOKIE).when(validatorSpy).getHttpValidationType();

        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class, validatorSpy::validateHttpValidationValues);

        assertThat(thrown.getMessage(),
                equalTo("a list of values must be specified after the http validation type"));
    }

    @Test
    public void emptyHttpValidationValueListThrowsException() {
        RampartList values = newRampartList(
                newRampartNamedValue(COOKIE_KEY, RampartList.EMPTY),
                newRampartNamedValue(ENFORCE_KEY, newRampartString("integer")));
        RampartHttpValidationValidatorUpTo1_5 validatorSpy = spy(getValidator(values));
        doReturn(HTTP_COOKIE).when(validatorSpy).getHttpValidationType();

        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class, validatorSpy::validateHttpValidationValues);

        assertThat(thrown.getMessage(), equalTo("values list cannot be empty"));
    }

    @Test
    public void invalidTypeParameterThrowsException() {
        RampartList values = newRampartList(
                newRampartNamedValue(COOKIE_KEY, newRampartList(newRampartInteger(5))),
                newRampartNamedValue(ENFORCE_KEY, newRampartString("integer")));
        RampartHttpValidationValidatorUpTo1_5 validatorSpy = spy(getValidator(values));
        doReturn(HTTP_COOKIE).when(validatorSpy).getHttpValidationType();

        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class, validatorSpy::validateHttpValidationValues);

        assertThat(thrown.getMessage(), equalTo("all list values must a quoted String"));
    }

    @Test
    public void emptyTypeParameterThrowsException() {
        RampartList values = newRampartList(
                newRampartNamedValue(COOKIE_KEY, newRampartList(newRampartString(""))),
                newRampartNamedValue(ENFORCE_KEY, newRampartString("integer")));
        RampartHttpValidationValidatorUpTo1_5 validatorSpy = spy(getValidator(values));
        doReturn(HTTP_COOKIE).when(validatorSpy).getHttpValidationType();

        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class, validatorSpy::validateHttpValidationValues);

        assertThat(thrown.getMessage(), equalTo("list value cannot be an empty String"));
    }

    @Test
    public void emptyEnforceValuesListThrowsException() {
        RampartList values = newRampartList(
                newRampartNamedValue(COOKIE_KEY, newRampartList(newRampartString("cookie-name"))),
                newRampartNamedValue(ENFORCE_KEY, RampartList.EMPTY));
        RampartHttpValidationValidatorUpTo1_5 validatorSpy = spy(getValidator(values));
        doReturn(HTTP_COOKIE).when(validatorSpy).getHttpValidationType();

        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class, validatorSpy::validateHttpValidationValues);

        assertThat(thrown.getMessage(), equalTo("enforce value list cannot be empty"));
    }

    @Test
    public void invalidEnforceValuesTypeThrowsException() {
        RampartList values = newRampartList(
                newRampartNamedValue(COOKIE_KEY, newRampartList(newRampartString("cookie-name"))),
                newRampartNamedValue(ENFORCE_KEY, newRampartInteger(5)));
        RampartHttpValidationValidatorUpTo1_5 validatorSpy = spy(getValidator(values));
        doReturn(HTTP_COOKIE).when(validatorSpy).getHttpValidationType();

        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class, validatorSpy::validateHttpValidationValues);

        assertThat(thrown.getMessage(), equalTo("a list of values must be specified after the enforce keyword"));
    }

    @Test
    public void invalidEnforceValueTypeThrowsException() {
        RampartList values = newRampartList(
                newRampartNamedValue(COOKIE_KEY, newRampartList(newRampartString("cookie-name"))),
                newRampartNamedValue(ENFORCE_KEY, newRampartList(newRampartInteger(5))));
        RampartHttpValidationValidatorUpTo1_5 validatorSpy = spy(getValidator(values));
        doReturn(HTTP_COOKIE).when(validatorSpy).getHttpValidationType();

        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class, validatorSpy::validateHttpValidationValues);

        assertThat(thrown.getMessage(), equalTo("enforce list entry value must be a quoted String"));
    }

    @Test
    public void invalidRegexAsEnforceValueThrowsException() {
        String invalidRegex = "!@#$%%^&&**(())_";
        RampartList values = newRampartList(
                newRampartNamedValue(COOKIE_KEY, newRampartList(newRampartString("cookie-name"))),
                newRampartNamedValue(ENFORCE_KEY, newRampartList(newRampartString(invalidRegex))));
        RampartHttpValidationValidatorUpTo1_5 validatorSpy = spy(getValidator(values));
        doReturn(HTTP_COOKIE).when(validatorSpy).getHttpValidationType();

        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class, validatorSpy::validateHttpValidationValues);

        assertThat(thrown.getMessage(), equalTo("\"" + invalidRegex + "\" is an invalid enforcement type"));
    }

    @Test
    public void invalidEnforceValueThrowsException() {
        RampartList values = newRampartList(
                newRampartNamedValue(COOKIE_KEY, newRampartList(newRampartString("cookie-name"))),
                newRampartNamedValue(ENFORCE_KEY, newRampartList(RampartHttpMethodMatcher.GET.getName().asRampartString())));
        RampartHttpValidationValidatorUpTo1_5 validatorSpy = spy(getValidator(values));
        doReturn(HTTP_COOKIE).when(validatorSpy).getHttpValidationType();

        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class, validatorSpy::validateHttpValidationValues);

        assertThat(thrown.getMessage(), equalTo("\"GET\" is an invalid enforcement type"));
    }

    @Test
    public void whitespaceOnlyRegexThrowsException() {
        String invalidRegex = " \t";
        RampartList values = newRampartList(
                newRampartNamedValue(COOKIE_KEY, newRampartList(newRampartString("cookie-name"))),
                newRampartNamedValue(ENFORCE_KEY, newRampartList(newRampartString(invalidRegex))));
        RampartHttpValidationValidatorUpTo1_5 validatorSpy = spy(getValidator(values));
        doReturn(HTTP_COOKIE).when(validatorSpy).getHttpValidationType();

        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class, validatorSpy::validateHttpValidationValues);

        assertThat(thrown.getMessage(), equalTo("enforce list entry value must not be an empty String"));
    }

    @Test
    public void invalidCsrfProtectionTypeThrowsException() {
        String invalidCSRFProtectionType = "invalid-csrf-protection-type";
        RampartList values = newRampartList(
                newRampartNamedValue(CSRF_KEY, newRampartList(newRampartString(invalidCSRFProtectionType))));
        RampartHttpValidationValidatorUpTo1_5 validatorSpy = spy(getValidator(values));
        doReturn(CSRF).when(validatorSpy).getHttpValidationType();

        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class, validatorSpy::validateHttpValidationValues);

        assertThat(thrown.getMessage(),
                equalTo("\"" + invalidCSRFProtectionType + "\" is an unsupported csrf protection type"));
    }

    @Test
    public void invalidHostsTypeThrowsException() {
        RampartList validValues = newRampartList(
                newRampartNamedValue(CSRF_KEY, newRampartList(ORIGINS_KEY.asRampartString())),
                newRampartNamedValue(HOSTS_KEY, newRampartList(newRampartInteger(2))));
        RampartHttpValidationValidatorUpTo1_5 validatorSpy = spy(getValidator(validValues));
        doReturn(CSRF).when(validatorSpy).getHttpValidationType();

        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class, validatorSpy::validateHttpValidationValues);

        assertThat(thrown.getMessage(), equalTo("all list values must a quoted String"));
    }

    @Test
    public void emptyHostsListThrowsException() {
        RampartList validValues = newRampartList(
                newRampartNamedValue(CSRF_KEY, newRampartList(ORIGINS_KEY.asRampartString())),
                newRampartNamedValue(HOSTS_KEY, RampartList.EMPTY));
        RampartHttpValidationValidatorUpTo1_5 validatorSpy = spy(getValidator(validValues));
        doReturn(CSRF).when(validatorSpy).getHttpValidationType();

        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class, validatorSpy::validateHttpValidationValues);

        assertThat(thrown.getMessage(), equalTo("hosts value list cannot be empty"));
    }


}
