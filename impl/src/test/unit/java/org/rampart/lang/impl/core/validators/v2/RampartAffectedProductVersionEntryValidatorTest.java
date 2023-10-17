package org.rampart.lang.impl.core.validators.v2;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartNamedValue;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAffectedProductVersion;
import org.rampart.lang.impl.core.ValidationError;
import org.rampart.lang.java.builder.RampartAffectedProductVersionBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RampartAffectedProductVersionEntryValidatorTest {
    private static final RampartString AFFECTED_VERSION = newRampartString("2.0.0");
    private static final RampartNamedValue RANGE1 =
            newRampartNamedValue(RANGE_KEY, newRampartList(
                    newRampartNamedValue(FROM_KEY, newRampartString("1.0.0")),
                    newRampartNamedValue(TO_KEY, newRampartString("3.2.0"))
            ));
    private static final RampartNamedValue RANGE2 =
            newRampartNamedValue(RANGE_KEY, newRampartList(
                    newRampartNamedValue(FROM_KEY, newRampartString("1.2")),
                    newRampartNamedValue(TO_KEY, newRampartString("3.4"))
            ));
    private static final RampartNamedValue RANGE3 =
            newRampartNamedValue(RANGE_KEY, newRampartList(
                    newRampartNamedValue(FROM_KEY, newRampartString("7u12")),
                    newRampartNamedValue(TO_KEY, newRampartString("7u181"))
            ));
    private static final RampartNamedValue FOREIGN_OBJECT =
            newRampartNamedValue(newRampartConstant("unsupported"), newRampartString("undefined"));
    private RampartAffectedProductVersionEntryValidator validator;

    @BeforeEach
    public void before() {
        validator = new RampartAffectedProductVersionEntryValidator();
    }

    @Test
    public void validateCorrectKeyAndValidStringValue() throws ValidationError {
        RampartAffectedProductVersion apv =
                new RampartAffectedProductVersionBuilder().setVersion(AFFECTED_VERSION).createRampartObject();
        RampartObject value = validator.validateValue(
                        AFFECTED_PRODUCT_VERSION_KEY,
                        AFFECTED_VERSION);
        assertThat(value, equalTo(apv));
    }

    @Test
    public void validateCorrectKeyAndInvalidValue() {
        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateValue(
                                AFFECTED_PRODUCT_VERSION_KEY,
                                newRampartInteger(1)));
        assertThat(thrown.getMessage(), equalTo("metadata value of \"" + AFFECTED_PRODUCT_VERSION_KEY
                + "\" key must be a list of ranges or a single string literal"));
    }

    @Test
    public void validateCorrectKeyAndSingleRange() throws ValidationError {
        RampartAffectedProductVersion apv =
                new RampartAffectedProductVersionBuilder()
                        .addRange(
                                newRampartString("1.0.0"),
                                newRampartString("3.2.0"))
                        .createRampartObject();
        RampartObject value = validator.validateValue(
                        AFFECTED_PRODUCT_VERSION_KEY,
                        newRampartList(RANGE1));
        assertThat(value, equalTo(apv));
    }

    @Test
    public void validateCorrectKeyAndMultipleRanges() throws ValidationError {
        RampartAffectedProductVersion apv =
                new RampartAffectedProductVersionBuilder()
                        .addRange(
                                newRampartString("1.0.0"),
                                newRampartString("3.2.0"))
                        .addRange(
                                newRampartString("1.2"),
                                newRampartString("3.4"))
                        .addRange(
                                newRampartString("7u12"),
                                newRampartString("7u181"))
                        .createRampartObject();
        RampartObject value = validator.validateValue(
                        AFFECTED_PRODUCT_VERSION_KEY,
                        newRampartList(RANGE1, RANGE2, RANGE3));
        assertThat(value, equalTo(apv));
    }

    @Test
    public void validateCorrectKeyRangeForeignValue() {
        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateValue(
                                AFFECTED_PRODUCT_VERSION_KEY,
                                newRampartList(RANGE1, FOREIGN_OBJECT)));
        assertThat(thrown.getMessage(),
                equalTo("only key value pairs with \"" + RANGE_KEY + "\" as key are allowed within \""
                        + AFFECTED_PRODUCT_VERSION_KEY + "\""));
    }

    @Test
    public void validateCorrectKeyRangeWithForeignValue() {
        RampartNamedValue rangeWithForeignObject = newRampartNamedValue(
                RANGE1.getName(),
                ((RampartList) RANGE1.getRampartObject()).addAll(newRampartList(FOREIGN_OBJECT)));
        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateValue(
                                AFFECTED_PRODUCT_VERSION_KEY,
                                newRampartList(rangeWithForeignObject)));
        assertThat(thrown.getMessage(),
                equalTo("\"" + RANGE_KEY + "\" config must be comprised of 2 key value pairs"));
    }

    @Test
    public void validateCorrectKeyRangeWithMissingFrom() {
        RampartNamedValue rangeMissingLimit =
                newRampartNamedValue(RANGE_KEY, newRampartList(
                        FOREIGN_OBJECT,
                        newRampartNamedValue(TO_KEY, newRampartString("3.2.0"))));

        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateValue(
                                AFFECTED_PRODUCT_VERSION_KEY,
                                newRampartList(rangeMissingLimit)));
        assertThat(thrown.getMessage(),
                equalTo("missing mandatory \"from\" parameter in \"" + RANGE_KEY + "\" config"));
    }

    @Test
    public void validateCorrectKeyRangeWithMissingTo() {
        RampartNamedValue rangeMissingLimit =
                newRampartNamedValue(RANGE_KEY, newRampartList(
                        FOREIGN_OBJECT,
                        newRampartNamedValue(FROM_KEY, newRampartString("3.2.0"))));

        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateValue(
                                AFFECTED_PRODUCT_VERSION_KEY,
                                newRampartList(rangeMissingLimit)));
        assertThat(thrown.getMessage(),
                equalTo("missing mandatory \"to\" parameter in \"" + RANGE_KEY + "\" config"));
    }

    @Test
    public void validateCorrectKeyRangeWithFromWrongValueType() {
        RampartNamedValue rangeLimitInvalidValue =
                newRampartNamedValue(RANGE_KEY, newRampartList(
                        FOREIGN_OBJECT,
                        newRampartNamedValue(FROM_KEY, newRampartFloat(1.2f))));

        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateValue(
                                AFFECTED_PRODUCT_VERSION_KEY,
                                newRampartList(rangeLimitInvalidValue)));
        assertThat(thrown.getMessage(),
                equalTo("\"from\" in \"" + RANGE_KEY + "\" config must contain a string value"));
    }
}
