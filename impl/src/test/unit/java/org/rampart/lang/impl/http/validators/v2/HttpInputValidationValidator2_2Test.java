package org.rampart.lang.impl.http.validators.v2;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.http.RampartHttpInputValidation;
import org.rampart.lang.api.http.RampartHttpValidationType;
import org.rampart.lang.api.http.matchers.RampartHttpMethodMatcher;
import org.rampart.lang.api.http.matchers.RampartPatternMatcher;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartNamedValue;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpInputValidationValidator2_2Test extends HttpInputValidationValidator2_1Test {
    @Override
    protected Constructor<HttpInputValidationValidator2_2> getHttpInputValidatiorValidatorConstructor()
            throws Exception {
        return HttpInputValidationValidator2_2.class.getDeclaredConstructor(RampartList.class);
    }

    @Test
    public void methodValidationTypeMatchingGET() throws Exception {
        RampartList values = newRampartList(
                METHOD_KEY,
                newRampartNamedValue(IS_KEY, RampartHttpMethodMatcher.GET.getName()));
        RampartHttpInputValidation inputValidation =
                getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues();

        assertAll(() -> {
            assertThat(inputValidation.getType(), equalTo(RampartHttpValidationType.METHOD));
            assertThat(inputValidation.getBuiltInMatchers(), equalTo(newRampartList(RampartHttpMethodMatcher.GET)));
        });
    }

    @Test
    public void methodValidationTypeMatchingGETandPOST() throws Exception {
        RampartList values = newRampartList(
                METHOD_KEY,
                newRampartNamedValue(IS_KEY, newRampartList(
                        RampartHttpMethodMatcher.GET.getName(),
                        RampartHttpMethodMatcher.POST.getName())));
        RampartHttpInputValidation inputValidation =
                getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues();

        assertAll(() -> {
            assertThat(inputValidation.getType(), equalTo(RampartHttpValidationType.METHOD));
            assertThat(inputValidation.getBuiltInMatchers(), equalTo(newRampartList(RampartHttpMethodMatcher.GET, RampartHttpMethodMatcher.POST)));
        });
    }

    @Test
    public void methodValidationTypeUnsupportedMatcher() {
        RampartList values = newRampartList(
                METHOD_KEY,
                newRampartNamedValue(IS_KEY, RampartPatternMatcher.ALPHANUMERIC.getName()));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("matcher \"alphanumeric\" is incompatible with \"method\" validation type"));
    }

    @Test
    public void methodValidationTypeHasNoTargets() throws Exception {
        RampartList values = newRampartList(
                METHOD_KEY,
                newRampartNamedValue(IS_KEY, RampartHttpMethodMatcher.GET.getName()));
        RampartHttpInputValidation inputValidation =
                getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues();

        assertThat(inputValidation.hasTargets(), equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void methodValidationTypeUnsupportedTargets() {
        RampartList values = newRampartList(
                newRampartNamedValue(METHOD_KEY, RampartHttpMethodMatcher.GET.getName()),
                newRampartNamedValue(IS_KEY, RampartHttpMethodMatcher.GET));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("unrecognized parameter \"method: GET\" to the \"validate\" declaration"));
    }

    @Test
    public void methodValidationTypeUnsupportedOmitsMatchers() {
        RampartList values = newRampartList(
                METHOD_KEY,
                newRampartNamedValue(OMITS_KEY, newRampartString("GET")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("parameter \"method\" does not support \"omits\""));
    }

    @Test
    public void methodValidationTypeWithOtherValidationTypes() {
        RampartList values = newRampartList(
                METHOD_KEY,
                newRampartNamedValue(COOKIES_KEY, newRampartList(newRampartString("cookie-name"))),
                newRampartNamedValue(IS_KEY, newRampartList(
                        RampartHttpMethodMatcher.GET,
                        RampartPatternMatcher.INTEGER.getName())));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("\"method\" and \"cookies\" detected - a single validation type"
                + " parameter is allowed for \"validate\" declaration"));
    }

    @Test
    public void methodValidationTypeEmptyTargets() {
        RampartList values = newRampartList(
                newRampartNamedValue(METHOD_KEY, RampartList.EMPTY),
                newRampartNamedValue(IS_KEY, RampartPatternMatcher.INTEGER.getName()));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("unrecognized parameter \"method: []\" to the \"validate\" declaration"));
    }

    @Test
    public void methodValidationTypeUnsupportedRegexMatcher() {
        RampartString regex = newRampartString("g*");
        RampartList values = newRampartList(
                METHOD_KEY,
                newRampartNamedValue(IS_KEY, regex));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("regex patterns are incompatible with \"method\" validation type"));
    }

    @Test
    public void methodValidationTypeWithEmptyMatcherList() {
        RampartList values = newRampartList(
                METHOD_KEY,
                newRampartNamedValue(IS_KEY, RampartList.EMPTY));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("\"is\" parameter must have a non empty list of values"));
    }

    @Test
    public void methodValidationTypeWithNoMatchers() {
        RampartList values = newRampartList(METHOD_KEY);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("\"validate\" declaration must contain a key value pair with key \"is\""));
    }

    @Test
    public void methodValidationTypeIsRampartString() {
        RampartList values = newRampartList(
                METHOD_KEY.asRampartString(),
                newRampartNamedValue(IS_KEY, RampartHttpMethodMatcher.GET.getName()));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("invalid \"validate\" declaration parameter \"method\" - only constants and key value pairs are allowed"));
    }

    @Test
    public void methodValidationTypeMethodMatchersAndOmitsMatchers() {
        RampartList values = newRampartList(
                METHOD_KEY,
                newRampartNamedValue(IS_KEY, RampartHttpMethodMatcher.GET.getName()),
                newRampartNamedValue(OMITS_KEY, newRampartString("PUT")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("parameter \"method\" does not support \"omits\""));
    }

    @Test
    public void cookiesValidationTypeMethodMatchers() {
        RampartList values = newRampartList(
                newRampartNamedValue(COOKIES_KEY, newRampartString("cookie-name")),
                newRampartNamedValue(IS_KEY, RampartHttpMethodMatcher.GET.getName()));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("matcher \"GET\" is incompatible with \"cookies\" validation type"));
    }

    @Test
    public void headersValidationTypeMethodMatchers() {
        RampartList values = newRampartList(
                newRampartNamedValue(HEADERS_KEY, newRampartString("set-content-type")),
                newRampartNamedValue(IS_KEY, RampartHttpMethodMatcher.GET.getName()));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("matcher \"GET\" is incompatible with \"headers\" validation type"));
    }

    @Test
    public void parametersValidationTypeMethodMatchers() {
        RampartList values = newRampartList(
                newRampartNamedValue(PARAMETERS_KEY, newRampartString("query")),
                newRampartNamedValue(IS_KEY, RampartHttpMethodMatcher.GET.getName()));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("matcher \"GET\" is incompatible with \"parameters\" validation type"));
    }

    @Test
    public void requestValidationTypePathTargetWithMethodMatchers() {
        RampartList values = newRampartList(
                newRampartNamedValue(REQUEST_KEY, PATH_KEY),
                newRampartNamedValue(IS_KEY, RampartHttpMethodMatcher.GET.getName()));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("parameter \"request\" does not support \"is\" key"));
    }

}
