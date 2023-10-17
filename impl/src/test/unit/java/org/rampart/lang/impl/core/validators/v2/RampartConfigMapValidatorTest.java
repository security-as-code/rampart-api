package org.rampart.lang.impl.core.validators.v2;

import static org.rampart.lang.java.RampartPrimitives.*;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.rampart.lang.api.RampartNamedValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.impl.core.RampartOptions;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import matchers.RampartListMatcher;

public class RampartConfigMapValidatorTest {

    private static final RampartNamedValue OPTION1 = newRampartNamedValue(newRampartConstant("option1key"), newRampartConstant("option1value"));
    private static final RampartNamedValue OPTION2 = newRampartNamedValue(newRampartConstant("option2key"), newRampartConstant("option2value"));
    private static final RampartNamedValue OPTION3 = newRampartNamedValue(newRampartConstant("option3key"), newRampartConstant("option3value"));
    private static final RampartConstant DEFAULT_VALUE = newRampartConstant("value");
    private static final RampartConstant SUPPORTED_TARGET = newRampartConstant("target");

    private static final RampartOptions ALLOWED_OPTIONS = new RampartOptions() {
        @Override
        public ConfigValueValidator getOptionValidator(RampartConstant config) {
            if (OPTION1.getName().equals(config)) {
                return obj -> OPTION1.getRampartObject().equals(obj) ? obj : null;
            } else if (OPTION2.getName().equals(config)) {
                return obj -> OPTION2.getRampartObject().equals(obj) ? obj : null;
            } else if (OPTION3.getName().equals(config)) {
                return obj -> OPTION3.getRampartObject().equals(obj) ? obj : null;
            } else {
                return null;
            }
        }

        @Override
        public RampartObject getDefaults(RampartConstant config) {
            return DEFAULT_VALUE;
        }

        @Override
        public List<RampartConstant> getAllConfigsForTarget(RampartConstant target) {
            return SUPPORTED_TARGET.equals(target) ? Arrays.asList(OPTION1.getName(), OPTION2.getName()) : Collections.emptyList();
        }
    };

    @Test
    public void validateOptionsWithEmptyMap() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> new RampartConfigMapValidator(RampartList.EMPTY, mock(RampartOptions.class)).validateOptions());

        assertThat(thrown.getMessage(), equalTo("\"options\" parameter must be followed by a non empty list"));
    }

    @Test
    public void validateOptionsWithNoOptions() throws InvalidRampartRuleException {
        RampartConfigMapValidator validator = new RampartConfigMapValidator(null, mock(RampartOptions.class));
        validator.validateOptions();
        assertThat(validator.getValidatedOptions(SUPPORTED_TARGET), equalTo(RampartList.EMPTY));
    }

    @Test
    public void validateOptionsWithSingleOption() throws InvalidRampartRuleException {
        RampartConfigMapValidator validator = new RampartConfigMapValidator(newRampartList(OPTION1), ALLOWED_OPTIONS);
        validator.validateOptions();
        assertThat(validator.getValidatedOptions(SUPPORTED_TARGET), RampartListMatcher.containsInAnyOrder(OPTION1));
    }

    @Test
    public void validateOptionsWithMultipleOptions() throws InvalidRampartRuleException {
        RampartConfigMapValidator validator = new RampartConfigMapValidator(newRampartList(OPTION1, OPTION2), ALLOWED_OPTIONS);
        validator.validateOptions();
        assertThat(validator.getValidatedOptions(SUPPORTED_TARGET), RampartListMatcher.containsInAnyOrder(OPTION1, OPTION2));
    }

    @Test
    public void validateOptionsWithNonOption() {
        RampartConfigMapValidator validator = new RampartConfigMapValidator(newRampartList(
                newRampartNamedValue(newRampartConstant("undefined"), newRampartConstant("unsupported"))), ALLOWED_OPTIONS);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, validator::validateOptions);

        assertThat(thrown.getMessage(), equalTo("option \"undefined\" is unsupported for \"options\" parameter"));
    }

    @Test
    public void validateOptionsWithNonValidAndValidOptions() {
        RampartConfigMapValidator validator = new RampartConfigMapValidator(newRampartList(
                OPTION1,
                newRampartNamedValue(newRampartConstant("undefined"), newRampartConstant("unsupported")),
                OPTION2), ALLOWED_OPTIONS);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, validator::validateOptions);

        assertThat(thrown.getMessage(), equalTo("option \"undefined\" is unsupported for \"options\" parameter"));
    }

    @Test
    public void validateOptionsUnspecifiedOptionsGetDefaults() throws InvalidRampartRuleException {
        RampartConfigMapValidator validator = new RampartConfigMapValidator(newRampartList(OPTION1), ALLOWED_OPTIONS);
        validator.validateOptions();
        assertThat(validator.getValidatedOptions(SUPPORTED_TARGET), RampartListMatcher.containsInAnyOrder(
                OPTION1,
                newRampartNamedValue(OPTION2.getName(), DEFAULT_VALUE)));
    }

    @Test
    public void validateOptionsDuplicate() {
        RampartConfigMapValidator validator = new RampartConfigMapValidator(newRampartList(OPTION1, OPTION2, OPTION1), ALLOWED_OPTIONS);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, validator::validateOptions);

        assertThat(thrown.getMessage(), equalTo("duplicate configuration key \"option1key\" detected in \"options\" parameter"));
    }

    @Test
    public void validateOptionsInvalidOptionType() {
        RampartConfigMapValidator validator = new RampartConfigMapValidator(newRampartList(DEFAULT_VALUE), ALLOWED_OPTIONS);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, validator::validateOptions);

        assertThat(thrown.getMessage(), equalTo("\"options\" parameter list entries must be name value pairs"));
    }

    @Test
    public void validateOptionsWithInvalidOptionType() {
        RampartConfigMapValidator validator = new RampartConfigMapValidator(newRampartList(OPTION1, DEFAULT_VALUE, OPTION2), ALLOWED_OPTIONS);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, validator::validateOptions);

        assertThat(thrown.getMessage(), equalTo("\"options\" parameter list entries must be name value pairs"));
    }

    @Test
    public void validateOptionsUnsupportedValue() {
        RampartConfigMapValidator validator = new RampartConfigMapValidator(newRampartList(
                newRampartNamedValue(OPTION1.getName(), newRampartInteger(4))), ALLOWED_OPTIONS);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, validator::validateOptions);

        assertThat(thrown.getMessage(), equalTo("incorrect value \"4\" for option \"option1key\""));
    }

    @Test
    public void getValidatedOptionsAllSupportedOptions() throws InvalidRampartRuleException {
        RampartConfigMapValidator validator = new RampartConfigMapValidator(newRampartList(OPTION1), ALLOWED_OPTIONS);
        validator.validateOptions();
        validator.getValidatedOptions(SUPPORTED_TARGET);
    }

    @Test
    public void getValidatedOptionsSupportedAndNonSupportedOptions() {
        RampartConfigMapValidator validator = new RampartConfigMapValidator(newRampartList(OPTION1, OPTION3), ALLOWED_OPTIONS);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> {
            validator.validateOptions();
            validator.getValidatedOptions(SUPPORTED_TARGET);
        });

        assertThat(thrown.getMessage(), equalTo("unsupported config \"option3key\" for \"options\" parameter in target \"target\""));
    }

    @Test
    public void getValidatedOptionsNonSupportedOption() {
        RampartConfigMapValidator validator = new RampartConfigMapValidator(newRampartList(OPTION3, OPTION2), ALLOWED_OPTIONS);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> {
            validator.validateOptions();
            validator.getValidatedOptions(SUPPORTED_TARGET);
        });

        assertThat(thrown.getMessage(), equalTo("unsupported config \"option3key\" for \"options\" parameter in target \"target\""));
    }

    @Test
    public void getValidatedOptionsUnknownTarget() {
        RampartConfigMapValidator validator = new RampartConfigMapValidator(newRampartList(OPTION1), ALLOWED_OPTIONS);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> {
            validator.validateOptions();
            validator.getValidatedOptions(newRampartConstant("made up"));
        });

        assertThat(thrown.getMessage(), equalTo("unsupported config \"option1key\" for \"options\" parameter in target \"made up\""));
    }

}
