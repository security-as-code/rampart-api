package org.rampart.lang.impl.http.validators.v2;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.lang.reflect.Constructor;

import org.rampart.lang.api.http.matchers.RampartPatternMatcher;
import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.http.RampartHttpInputValidation;
import org.rampart.lang.api.http.RampartHttpValidationType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import matchers.RampartListMatcher;

public class HttpInputValidationValidator2_1Test extends HttpInputValidationValidator2_0Test {

    @Override
    protected Constructor<? extends HttpInputValidationValidator2_1> getHttpInputValidatiorValidatorConstructor()
            throws Exception {
        return HttpInputValidationValidator2_1.class.getDeclaredConstructor(RampartList.class);
    }

    @Test
    public void validForRequestPathTargetSingleOmitRule() throws Exception {
        RampartString omitString = newRampartString("~");

        RampartList values = newRampartList(
                newRampartNamedValue(REQUEST_KEY, PATH_KEY),
                newRampartNamedValue(OMITS_KEY, newRampartList(omitString)));

        RampartHttpInputValidation inputValidation =
                getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues();

        assertAll(() -> {
            assertThat(inputValidation.getType(), equalTo(RampartHttpValidationType.REQUEST));
            assertThat(inputValidation.getTargets(), RampartListMatcher.containsInAnyOrder(PATH_KEY));
            assertThat(inputValidation.hasOmitRule(), equalTo(RampartBoolean.TRUE));
            assertThat(inputValidation.getOmitRules(), RampartListMatcher.containsInAnyOrder(omitString));
        });
    }

    @Test
    public void validForRequestPathTargetMultipleOmitRules() throws Exception {
        RampartString omitString1 = newRampartString("~");
        RampartString omitString2 = newRampartString("omitted string");

        RampartList values = newRampartList(
                newRampartNamedValue(REQUEST_KEY, PATH_KEY),
                newRampartNamedValue(OMITS_KEY, newRampartList(omitString1, omitString2)));

        RampartHttpInputValidation inputValidation =
                getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues();

        assertAll(() -> {
            assertThat(inputValidation.hasOmitRule(), equalTo(RampartBoolean.TRUE));
            assertThat(inputValidation.getOmitRules(), RampartListMatcher.containsInAnyOrder(omitString1, omitString2));
        });
    }

    @Test
    public void invalidForRequestURLTarget() {
        RampartList values = newRampartList(
                newRampartNamedValue(REQUEST_KEY, newRampartConstant("url")),
                newRampartNamedValue(OMITS_KEY, newRampartList(newRampartString("omitted string"))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("\"request\" parameter only supports the target \"path\""));
    }

    @Test
    public void requestPathTargetInvalidType() {
        RampartList values = newRampartList(
                newRampartNamedValue(REQUEST_KEY, newRampartString("path")),
                newRampartNamedValue(OMITS_KEY, newRampartString("omitted string")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("\"request\" parameter must contain a constant or a list of constants"));
    }

    @Test
    public void requestPathTargetInvalidTypeInList() {
        RampartList values = newRampartList(
                newRampartNamedValue(REQUEST_KEY, newRampartList(newRampartString("path"))),
                newRampartNamedValue(OMITS_KEY, newRampartString("omitted string")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("\"request\" parameter must contain a list of constants"));
    }

    @Test
    public void requestPathTargetInvalidOmitRuleType() {
        RampartList values = newRampartList(
                newRampartNamedValue(REQUEST_KEY, PATH_KEY),
                newRampartNamedValue(OMITS_KEY, newRampartConstant("omitted string")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(),
                equalTo("\"omits\" parameter value \"omitted string\" must be a string literal or a list of string literals"));
    }

    @Test
    public void requestPathTargetNoOmitsParameter() {
        RampartList values = newRampartList(
                newRampartNamedValue(REQUEST_KEY, PATH_KEY));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("\"validate\" must contain a key value pair with key \"omits\" key"));
    }

    @Test
    public void requestPathTargetInvalidValidOmitRuleTypes() {
        RampartList values = newRampartList(
                newRampartNamedValue(REQUEST_KEY, PATH_KEY),
                newRampartNamedValue(OMITS_KEY, newRampartList(newRampartString("~"), newRampartConstant("omitted string"))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("entry of \"omits\" parameter list must be a string literal"));
    }

    @Test
    public void requestPathTargetWithBuiltInMatchers() {
        RampartList values = newRampartList(
                newRampartNamedValue(REQUEST_KEY, PATH_KEY),
                newRampartNamedValue(IS_KEY, newRampartList(RampartPatternMatcher.INTEGER.getName())));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("parameter \"request\" does not support \"is\" key"));
    }

    @Test
    public void requestPathTargetWithRegex() {
        RampartList values = newRampartList(
                newRampartNamedValue(REQUEST_KEY, PATH_KEY),
                newRampartNamedValue(IS_KEY, newRampartString("~")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("parameter \"request\" does not support \"is\" key"));
    }

    @Test
    public void httpParametersTargetWithOmitsRule() {
        RampartList values = newRampartList(
                newRampartNamedValue(PARAMETERS_KEY, newRampartString("param1")),
                newRampartNamedValue(OMITS_KEY, newRampartString("~")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("parameter \"parameters\" does not support \"omits\" key"));
    }

    @Test
    public void httpParametersTargetWithOmitsRuleAndBuiltInMatcher() {
        RampartList values = newRampartList(
                newRampartNamedValue(PARAMETERS_KEY, newRampartString("param1")),
                newRampartNamedValue(OMITS_KEY, newRampartString("~")),
                newRampartNamedValue(IS_KEY, RampartPatternMatcher.INTEGER.getName()));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("parameter \"parameters\" does not support \"omits\" key"));
    }

    @Test
    public void requestPathTargetWithOmitsRuleAndBuiltInMatcher() {
        RampartList values = newRampartList(
                newRampartNamedValue(REQUEST_KEY, PATH_KEY),
                newRampartNamedValue(OMITS_KEY, newRampartString("~")),
                newRampartNamedValue(IS_KEY, RampartPatternMatcher.INTEGER.getName()));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getHttpInputValidatiorValidatorConstructor().newInstance(values).validateHttpValidationValues());

        assertThat(thrown.getMessage(), equalTo("parameter \"request\" does not support \"is\" key"));
    }

}
