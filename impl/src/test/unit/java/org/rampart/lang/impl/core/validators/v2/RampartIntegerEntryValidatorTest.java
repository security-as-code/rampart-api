package org.rampart.lang.impl.core.validators.v2;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.impl.core.ValidationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RampartIntegerEntryValidatorTest {
    private static final RampartConstant KEY = newRampartConstant("entry-key");
    private RampartIntegerEntryValidator validator;

    @BeforeEach
    public void before() {
        validator = new RampartIntegerEntryValidator();
    }

    @Test
    public void validateCorrectKeyAndValidValue() throws ValidationError {
        RampartObject value = validator.validateValue(KEY, newRampartInteger(1));
        assertThat(value, equalTo(newRampartInteger(1)));
    }

    @Test
    public void validateCorrectKeyAndInvalidValue() {
        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateValue(
                        KEY, newRampartString("invalid-value")));
        assertThat(thrown.getMessage(), equalTo("metadata value of \"" + KEY + "\" key must be an integer"));
    }
}
