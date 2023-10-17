package org.rampart.lang.impl.core.validators.v2;

import org.rampart.lang.api.RampartFloat;
import org.rampart.lang.api.RampartNamedValue;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartCvss;
import org.rampart.lang.impl.core.ValidationError;
import org.rampart.lang.java.builder.RampartCvssBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RampartCvssEntryValidatorTest {
    private static final RampartNamedValue VALID_SCORE = newRampartNamedValue(SCORE_KEY, newRampartFloat(8.2f));
    private static final RampartNamedValue VALID_VECTOR = newRampartNamedValue(VECTOR_KEY, newRampartString("CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:N/A:N"));
    private static final RampartNamedValue VALID_VERSION = newRampartNamedValue(VERSION_KEY, newRampartFloat(3.1f));
    private RampartCvssEntryValidator validator;

    @BeforeEach
    public void before() {
        validator = new RampartCvssEntryValidator();
    }

    @Test
    public void validateCorrectKeyAndInvalidValue() {
        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateValue(
                                CVSS_KEY,
                                newRampartInteger(1)));
        assertThat(thrown.getMessage(),
                equalTo("metadata value of \"" + CVSS_KEY + "\" key must be a list"));
    }

    @Test
    public void validateCorrectKeyAndFullCvss() throws ValidationError {
        RampartCvss cvss =
                new RampartCvssBuilder()
                        .addScore((RampartFloat) VALID_SCORE.getRampartObject())
                        .addVector((RampartString) VALID_VECTOR.getRampartObject())
                        .addVersion((RampartFloat) VALID_VERSION.getRampartObject())
                        .createRampartObject();
        RampartObject value = validator.validateValue(
                        CVSS_KEY,
                        newRampartList(VALID_SCORE, VALID_VECTOR, VALID_VERSION));
        assertThat(value, equalTo(cvss));
    }

    @Test
    public void validateCorrectKeyAndMissingScore() {
        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateValue(
                                CVSS_KEY,
                                newRampartList(VALID_VECTOR, VALID_VERSION)));
        assertThat(thrown.getMessage(),
                equalTo("metadata entry \"" + CVSS_KEY + "\" is missing the following configs: [score]"));
    }

    @Test
    public void validateCorrectKeyAndMissingVector() {
        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateValue(
                                CVSS_KEY,
                                newRampartList(VALID_SCORE, VALID_VERSION)));
        assertThat(thrown.getMessage(),
                equalTo("metadata entry \"" + CVSS_KEY + "\" is missing the following configs: [vector]"));
    }

    @Test
    public void validateCorrectKeyAndMissingVersion() {
        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateValue(
                                CVSS_KEY,
                                newRampartList(VALID_SCORE, VALID_VECTOR)));
        assertThat(thrown.getMessage(),
                equalTo("metadata entry \"" + CVSS_KEY + "\" is missing the following configs: [version]"));
    }

    @Test
    public void validateCorrectKeyAndFullCvssWithInvalidConfig() {
        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateValue(
                                CVSS_KEY,
                                newRampartList(
                                        VALID_SCORE,
                                        VALID_VECTOR,
                                        VALID_VERSION,
                                        newRampartNamedValue(newRampartConstant("unsupported"), newRampartInteger(3)))));
        assertThat(thrown.getMessage(),
                equalTo("invalid config (unsupported: 3) for \"" + CVSS_KEY + "\" metadata entry"));
    }

    @Test
    public void validateCorrectKeyAndFullCvssWithInvalidScore() {
        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateValue(
                                CVSS_KEY,
                                newRampartList(
                                        newRampartNamedValue(SCORE_KEY, newRampartString("1")),
                                        VALID_VECTOR,
                                        VALID_VERSION)));
        assertThat(thrown.getMessage(),
                equalTo("invalid config (score: \"1\") for \"" + CVSS_KEY + "\" metadata entry"));
    }

    @Test
    public void validateCorrectKeyAndFullCvssWithInvalidVector() {
        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateValue(
                                CVSS_KEY,
                                newRampartList(
                                        VALID_SCORE,
                                        newRampartNamedValue(VECTOR_KEY, newRampartConstant("CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:N/A:N")),
                                        VALID_VERSION)));
        assertThat(thrown.getMessage(),
                equalTo("invalid config (vector: CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:N/A:N) for \"" + CVSS_KEY + "\" metadata entry"));
    }

    @Test
    public void validateCorrectKeyAndFullCvssWithInvalidVersion() {
        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateValue(
                                CVSS_KEY,
                                newRampartList(
                                        VALID_SCORE,
                                        VALID_VECTOR,
                                        newRampartNamedValue(VERSION_KEY, newRampartInteger(3)))));
        assertThat(thrown.getMessage(),
                equalTo("invalid config (version: 3) for \"" + CVSS_KEY + "\" metadata entry"));
    }
}
