package org.rampart.lang.impl.http.validators.v2;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Constructor;

import org.rampart.lang.api.http.matchers.RampartPatternMatcher;
import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.http.RampartHttpInputValidation;
import org.rampart.lang.api.http.RampartHttpValidationType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import matchers.RampartListMatcher;

public class HttpInputValidationValidator2_0Test {

    protected Constructor<? extends HttpInputValidationValidator2_0> getHttpInputValidatiorValidatorConstructor()
            throws Exception {
        return HttpInputValidationValidator2_0.class.getDeclaredConstructor(RampartList.class);
    }

    @Test
    public void httpInputValidationIsNotMandatory() throws Exception {
        assertThat(getHttpInputValidatiorValidatorConstructor().newInstance((RampartObject) null)
                .validateHttpValidationValues(), is(nullValue()));
    }

    @Test
    public void emptyValidateDeclaration() {
        assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(RampartList.EMPTY).validateHttpValidationValues(), "must be a non empty list of values");
    }

    @Test
    public void validInputValidationForSingleParameterSingleMatcher() throws Exception {
        RampartList validValues = newRampartList(
                newRampartNamedValue(PARAMETERS_KEY, newRampartString("query")),
                newRampartNamedValue(IS_KEY, RampartPatternMatcher.HTML_TEXT.getName()));
        RampartHttpInputValidation inputValidation =
                getHttpInputValidatiorValidatorConstructor().newInstance(validValues).validateHttpValidationValues();

        assertAll(() -> {
            assertThat(inputValidation.getType(), equalTo(RampartHttpValidationType.PARAMETERS));
            assertThat(inputValidation.getBuiltInMatchers(), equalTo(newRampartList(RampartPatternMatcher.HTML_TEXT)));
            assertThat(inputValidation.getTargets(), equalTo(newRampartList(newRampartString("query"))));
        });
    }

    @Test
    public void validInputValidationForSingleHeaderSingleMatcher() throws Exception {
        RampartList validValues = newRampartList(
                newRampartNamedValue(HEADERS_KEY, newRampartString("set-content-type")),
                newRampartNamedValue(IS_KEY, RampartPatternMatcher.HTML_TEXT.getName()));
        RampartHttpInputValidation inputValidation =
                getHttpInputValidatiorValidatorConstructor().newInstance(validValues).validateHttpValidationValues();

        assertAll(() -> {
            assertThat(inputValidation.getType(), equalTo(RampartHttpValidationType.HEADERS));
            assertThat(inputValidation.getBuiltInMatchers(), equalTo(newRampartList(RampartPatternMatcher.HTML_TEXT)));
            assertThat(inputValidation.getTargets(), equalTo(newRampartList(newRampartString("set-content-type"))));
        });
    }

    @Test
    public void validInputValidationForSingleCookieSingleMatcher() throws Exception {
        RampartList validValues = newRampartList(
                newRampartNamedValue(COOKIES_KEY, newRampartString("cookie-name")),
                newRampartNamedValue(IS_KEY, RampartPatternMatcher.INTEGER.getName()));
        RampartHttpInputValidation inputValidation =
                getHttpInputValidatiorValidatorConstructor().newInstance(validValues).validateHttpValidationValues();

        assertAll(() -> {
            assertThat(inputValidation.getType(), equalTo(RampartHttpValidationType.COOKIES));
            assertThat(inputValidation.getBuiltInMatchers(), equalTo(newRampartList(RampartPatternMatcher.INTEGER)));
            assertThat(inputValidation.getTargets(), equalTo(newRampartList(newRampartString("cookie-name"))));
        });
    }

    @Test
    public void validInputValidationForMultipleCookiesSingleMatcher() throws Exception {
        RampartList validValues = newRampartList(
                newRampartNamedValue(COOKIES_KEY, newRampartList(newRampartString("size"), newRampartString("age"))),
                newRampartNamedValue(IS_KEY, RampartPatternMatcher.INTEGER.getName()));
        RampartHttpInputValidation inputValidation =
                getHttpInputValidatiorValidatorConstructor().newInstance(validValues).validateHttpValidationValues();

        assertAll(() -> {
            assertThat(inputValidation.getType(), equalTo(RampartHttpValidationType.COOKIES));
            assertThat(inputValidation.getBuiltInMatchers(), equalTo(newRampartList(RampartPatternMatcher.INTEGER)));
            assertThat(inputValidation.getTargets(),
                    equalTo(newRampartList(newRampartString("size"), newRampartString("age"))));
        });
    }

    @Test
    public void validInputValidationForSingleCookieMultipleMatchers() throws Exception {
        RampartList validValues = newRampartList(
                newRampartNamedValue(COOKIES_KEY, newRampartList(newRampartString("size"))),
                newRampartNamedValue(IS_KEY, newRampartList(
                        RampartPatternMatcher.INTEGER.getName(),
                        RampartPatternMatcher.HTML_TEXT.getName())));
        RampartHttpInputValidation inputValidation =
                getHttpInputValidatiorValidatorConstructor().newInstance(validValues).validateHttpValidationValues();

        assertAll(() -> {
            assertThat(inputValidation.getType(), equalTo(RampartHttpValidationType.COOKIES));
            assertThat(inputValidation.getBuiltInMatchers(),
                    RampartListMatcher.containsInAnyOrder(RampartPatternMatcher.INTEGER, RampartPatternMatcher.HTML_TEXT));
            assertThat(inputValidation.getTargets(), equalTo(newRampartList(newRampartString("size"))));
        });
    }

    @Test
    public void validInputValidationForMultipleCookiesMultipleMatchers() throws Exception {
        RampartList validValues = newRampartList(
                newRampartNamedValue(COOKIES_KEY, newRampartList(newRampartString("size"), newRampartString("age"))),
                newRampartNamedValue(IS_KEY, newRampartList(
                        RampartPatternMatcher.INTEGER.getName(),
                        RampartPatternMatcher.HTML_TEXT.getName())));
        RampartHttpInputValidation inputValidation =
                getHttpInputValidatiorValidatorConstructor().newInstance(validValues).validateHttpValidationValues();

        assertAll(() -> {
            assertThat(inputValidation.getType(), equalTo(RampartHttpValidationType.COOKIES));
            assertThat(inputValidation.getBuiltInMatchers(),
                    RampartListMatcher.containsInAnyOrder(RampartPatternMatcher.INTEGER, RampartPatternMatcher.HTML_TEXT));
            assertThat(inputValidation.getTargets(),
                    RampartListMatcher.containsInAnyOrder(newRampartString("size"), newRampartString("age")));
        });
    }

    @Test
    public void noInputValidationType() {
        RampartList values = newRampartList(
                newRampartNamedValue(IS_KEY, RampartPatternMatcher.INTEGER.getName()));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(),
                equalTo("\"validate\" must contain at least one key value pair with any of the keys: \"cookies\", \"parameters\" and \"headers\""));
    }

    @Test
    public void noMatchers() {
        RampartList values = newRampartList(
                newRampartNamedValue(COOKIES_KEY, newRampartList(newRampartString("size"))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("\"validate\" must contain a key value pair with key \"is\""));
    }

    @Test
    public void validInputValidationForSingleCookieMultipleMatcherDeclarations() {
        RampartList validValues = newRampartList(
                newRampartNamedValue(COOKIES_KEY, newRampartList(newRampartString("age"))),
                newRampartNamedValue(IS_KEY, RampartPatternMatcher.INTEGER.getName()),
                newRampartNamedValue(IS_KEY, RampartPatternMatcher.SQL_NO_DOUBLE_QUOTES.getName()));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(validValues).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("duplicate parameter \"is\" detected for \"validate\" declaration"));
    }

    @Test
    public void validInputValidationForSingleCookieDuplicateMatchers() {
        RampartList validValues = newRampartList(
                newRampartNamedValue(COOKIES_KEY, newRampartList(newRampartString("age"))),
                newRampartNamedValue(IS_KEY, newRampartList(
                        RampartPatternMatcher.INTEGER.getName(),
                        RampartPatternMatcher.INTEGER.getName())),
                newRampartNamedValue(IS_KEY, RampartPatternMatcher.SQL_NO_DOUBLE_QUOTES.getName()));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(validValues).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("duplicate entry \"integer\" to matchers in \"is\" parameter"));
    }

    @Test
    public void unsupportedMatcher() {
        RampartList validValues = newRampartList(
                newRampartNamedValue(COOKIES_KEY, newRampartString("cookie-name")),
                newRampartNamedValue(IS_KEY, newRampartConstant("do not know")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(validValues).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("invalid matcher \"do not know\" for \"is\" parameter"));
    }

    @Test
    public void unsupportedNameValuePair() {
        RampartList validValues = newRampartList(
                newRampartNamedValue(COOKIES_KEY, newRampartString("cookie-name")),
                newRampartNamedValue(newRampartConstant("foo"), newRampartConstant("bar")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(validValues).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("unrecognized parameter \"foo: bar\" to the \"validate\" declaration"));
    }

    @Test
    public void multipleInputValidationTypes() {
        RampartList values = newRampartList(
                newRampartNamedValue(PARAMETERS_KEY, newRampartList(newRampartString("parameter-name"))),
                newRampartNamedValue(COOKIES_KEY, newRampartList(newRampartString("cookie-name"))),
                newRampartNamedValue(IS_KEY, RampartPatternMatcher.INTEGER.getName()));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(),
                equalTo("\"parameters\" and \"cookies\" detected - a single validation type parameter is allowed for \"validate\" declaration"));
    }

    @Test
    public void emptyHttpValidationValueList() {
        RampartList values = newRampartList(
                newRampartNamedValue(COOKIES_KEY, RampartList.EMPTY),
                newRampartNamedValue(IS_KEY, RampartPatternMatcher.INTEGER.getName()));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("\"cookies\" parameter must have a non empty list of values"));
    }

    @Test
    public void invalidTypeParameter() {
        RampartList values = newRampartList(
                newRampartNamedValue(COOKIES_KEY, newRampartList(newRampartInteger(5))),
                newRampartNamedValue(IS_KEY, RampartPatternMatcher.INTEGER.getName()));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("list value must be a quoted string"));
    }

    @Test
    public void emptyTypeParameterInsideList() {
        RampartList values = newRampartList(
                newRampartNamedValue(COOKIES_KEY, newRampartList(newRampartString(""))),
                newRampartNamedValue(IS_KEY, RampartPatternMatcher.INTEGER.getName()));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("list value must be a non empty string"));
    }

    @Test
    public void emptyTypeParameter() {
        RampartList values = newRampartList(
                newRampartNamedValue(COOKIES_KEY, newRampartString("")),
                newRampartNamedValue(IS_KEY, RampartPatternMatcher.INTEGER.getName()));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("list value must be a non empty string"));
    }

    @Test
    public void emptyMatcherValuesList() {
        RampartList values = newRampartList(
                newRampartNamedValue(COOKIES_KEY, newRampartList(newRampartString("cookie-name"))),
                newRampartNamedValue(IS_KEY, RampartList.EMPTY));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("\"is\" parameter must have a non empty list of values"));
    }

    @Test
    public void invalidMatcherValueTypeInsideList() {
        RampartList values = newRampartList(
                newRampartNamedValue(COOKIES_KEY, newRampartList(newRampartString("cookie-name"))),
                newRampartNamedValue(IS_KEY, newRampartList(newRampartInteger(5))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("\"" + IS_KEY + "\" parameter can only contain strings and constant values"));
    }

    @Test
    public void invalidConstantTypeParameter() {
        RampartList values = newRampartList(
                newRampartNamedValue(COOKIES_KEY, newRampartConstant("cookie-name")),
                newRampartNamedValue(IS_KEY, RampartPatternMatcher.INTEGER.getName()));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("\"cookie-name\" must be a string literal or a list of string literals"));
    }

    @Test
    public void validForSingleCookieRegexMatcher() throws Exception {
        RampartString regex = newRampartString("[a-z].*");
        RampartList values = newRampartList(
                newRampartNamedValue(COOKIES_KEY, newRampartList(newRampartString("cookie-name"))),
                newRampartNamedValue(IS_KEY, newRampartList(regex)));
        RampartHttpInputValidation inputValidation =
                getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues();

        assertAll(() -> {
            assertThat(inputValidation.getType(), equalTo(RampartHttpValidationType.COOKIES));
            assertThat(inputValidation.getRegexPattern(), equalTo(regex));
            assertThat(inputValidation.hasRegexPattern(), equalTo(RampartBoolean.TRUE));
            assertThat(inputValidation.getTargets(), RampartListMatcher.containsInAnyOrder(newRampartString("cookie-name")));
        });
    }

    @Test
    public void invalidRegexMatcher() {
        String invalidRegex = "!@#$%%^&&**(())_";

        RampartList values = newRampartList(
                newRampartNamedValue(COOKIES_KEY, newRampartList(newRampartString("cookie-name"))),
                newRampartNamedValue(IS_KEY, newRampartList(newRampartString(invalidRegex))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("\"" + invalidRegex + "\" is an invalid regex matcher"));
    }

    @Test
    public void validateDoesNotSupportConstantTypes() {
        RampartList values = newRampartList(newRampartConstant("cookie"));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("unrecognized parameter \"cookie\" to the \"validate\" declaration"));
    }
}
