package org.rampart.lang.impl.core.validators.v2;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.impl.core.ValidationError;
import matchers.RampartListMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RampartListOrRampartStringEntryValidatorTest {
    private static final RampartConstant KEY = newRampartConstant("entry-key");
    private static final RampartString SINGLE_VALUE = newRampartString("test-value");
    private static final RampartList SIMPLE_LIST = newRampartList(SINGLE_VALUE);
    private RampartListOrRampartStringEntryValidator validator;

    @BeforeEach
    public void before() {
        validator = new RampartListOrRampartStringEntryValidator();
    }

    @Test
    public void validateCorrectKeyAndValidListOneValue() throws ValidationError {
        RampartObject value = validator.validateValue(
                        KEY,
                        SIMPLE_LIST);
        assertThat(value, is(instanceOf(RampartList.class)));
        assertThat((RampartList) value, RampartListMatcher.containsInAnyOrder(SINGLE_VALUE));
    }

    @Test
    public void validateCorrectKeyAndValidString() throws ValidationError {
        RampartObject value = validator.validateValue(
                        KEY,
                        SINGLE_VALUE);
        assertThat(value, is(instanceOf(RampartList.class)));
        assertThat((RampartList) value, RampartListMatcher.containsInAnyOrder(SINGLE_VALUE));
    }

    @Test
    public void validateCorrectKeyAndEmptyList() {
        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateValue(
                                KEY,
                                RampartList.EMPTY));
        assertThat(thrown.getMessage(), equalTo("metadata value of \"" + KEY
                + "\" key must be either a non empty string literal or a non empty list of non empty strings"));
    }

    @Test
    public void validateCorrectKeyAndEmptyString() {
        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateValue(
                                KEY,
                                newRampartString("")));
        assertThat(thrown.getMessage(), equalTo("metadata value of \"" + KEY
                + "\" key must be either a non empty string literal or a non empty list of non empty strings"));
    }

    @Test
    public void validateCorrectKeyAndInvalidValue() {
        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateValue(KEY, newRampartInteger(1)));
        assertThat(thrown.getMessage(), equalTo("metadata value of \"" + KEY
                + "\" key must be either a non empty string literal or a non empty list of non empty strings"));
    }

    @Test
    public void validateCorrectKeyAndListWithInvalidValue() {
        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateValue(
                                KEY,
                                newRampartList(newRampartInteger(1))));
        assertThat(thrown.getMessage(), equalTo("metadata value of \"" + KEY
                + "\" key must be either a non empty string literal or a non empty list of non empty strings"));
    }

    @Test
    public void validateCorrectKeyAndListWithInvalidandValidValue() {
        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateValue(
                                KEY,
                                newRampartList(SINGLE_VALUE, newRampartInteger(1))));
        assertThat(thrown.getMessage(), equalTo("metadata value of \"" + KEY
                + "\" key must be either a non empty string literal or a non empty list of non empty strings"));
    }

    @Test
    public void validateCorrectKeyAndListMultipleValidValues() throws ValidationError {
        RampartString value1 = newRampartString("value1");
        RampartString value2 = newRampartString("value2");
        RampartString value3 = newRampartString("value3");

        RampartObject value = validator.validateValue(
                                KEY,
                                newRampartList(value1, value2, value3));
        assertThat(value, is(instanceOf(RampartList.class)));
        assertThat((RampartList) value, RampartListMatcher.containsInAnyOrder(value1, value2, value3));
    }
}
