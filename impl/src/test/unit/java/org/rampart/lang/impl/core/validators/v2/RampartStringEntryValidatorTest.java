package org.rampart.lang.impl.core.validators.v2;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.impl.core.ValidationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.rampart.lang.java.RampartPrimitives.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RampartStringEntryValidatorTest {
    private static final RampartConstant KEY = newRampartConstant("entry-key");
    private static final RampartString TEST_VALUE = newRampartString("test-value");
    private RampartStringEntryValidator validator;

    @BeforeEach
    public void before() {
        validator = new RampartStringEntryValidator();
    }

    @Test
    public void validateCorrectKeyAndValidValue() throws ValidationError {
        RampartObject value = validator.validateValue(KEY, TEST_VALUE);
        assertThat(value, equalTo(TEST_VALUE));
    }

    @Test
    public void validateCorrectKeyAndInvalidValue() {
        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateValue(KEY, newRampartInteger(1)));
        assertThat(thrown.getMessage(), equalTo("metadata value of \"" + KEY + "\" key must be a string literal"));
    }

    @Test
    public void validateCorrectKeyAndEmptyValue() {
        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateValue(KEY, newRampartString("")));
        assertThat(thrown.getMessage(), equalTo("metadata value of \"" + KEY + "\" key cannot be empty"));
    }
}
