package org.rampart.lang.impl.core.validators.v2;

import org.rampart.lang.api.*;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.impl.core.ValidationError;
import org.rampart.lang.impl.core.parsers.v2.RampartMetadataParser;
import org.rampart.lang.impl.core.validators.RampartMetadataEntryValidator;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.rampart.lang.api.constants.RampartGeneralConstants.CREATION_TIME_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.LOG_KEY;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RampartMetadataValidatorTest {
    private static final RampartNamedValue ENTRY1 =
            newRampartNamedValue(
                    newRampartConstant("foo"),
                    newRampartString("bar"));
    private static final RampartNamedValue ENTRY2 =
            newRampartNamedValue(
                    CREATION_TIME_KEY,
                    newRampartString("Aug 24 00:15:59 2021"));

    private RampartNamedValue createCompoundedLoggableEntry(RampartObject... entries) {
        return newRampartNamedValue(LOG_KEY, newRampartList(entries));
    }

    private RampartList createMultiLoggableEntries(RampartObject... entries) {
        RampartNamedValue[] loggableEntries = new RampartNamedValue[entries.length];
        for (int i = 0; i < loggableEntries.length; i++) {
            loggableEntries[i] = newRampartNamedValue(LOG_KEY, newRampartList(entries[i]));
        }
        return newRampartList(loggableEntries);
    }

    private RampartMetadataValidator createMetadataValidator(RampartList metadataList) {
        return new RampartMetadataValidator(metadataList, Collections.emptyMap());
    }

    @Test
    public void metadataDeclarationNotMandatory() throws ValidationError {
        RampartMetadataValidator validator = createMetadataValidator(null);
        RampartMetadata metadata = validator.validateMetadata();
        assertThat(metadata.isEmpty(), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void metadataDeclarationCannotBeEmpty() {
        RampartMetadataValidator validator = createMetadataValidator(RampartList.EMPTY);
        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateMetadata());
        assertThat(thrown.getMessage(), equalTo("metadata declaration cannot be empty"));
    }

    @Test
    public void metadataDeclarationInvalidValues() {
        RampartMetadataValidator validator = createMetadataValidator(newRampartList(
                newRampartInteger(1), newRampartString("invalid-value")));
        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateMetadata());
        assertThat(thrown.getMessage(), equalTo("metadata entries can only be key value pairs"));
    }

    @Test
    public void singleMetadataEntry() throws ValidationError {
        RampartMetadataValidator validator = createMetadataValidator(newRampartList(ENTRY1));
        RampartMetadata metadata = validator.validateMetadata();
        assertThat(metadata.get(ENTRY1.getName()), equalTo(ENTRY1.getRampartObject()));
        assertThat(metadata.isLoggable(ENTRY1.getName()), equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void multipleMetadataEntry() throws ValidationError {
        RampartMetadataValidator validator = createMetadataValidator(newRampartList(ENTRY1, ENTRY2));
        RampartMetadata metadata = validator.validateMetadata();
        assertThat(metadata.get(ENTRY1.getName()), equalTo(ENTRY1.getRampartObject()));
        assertThat(metadata.isLoggable(ENTRY1.getName()), equalTo(RampartBoolean.FALSE));
        assertThat(metadata.get(ENTRY2.getName()), equalTo(ENTRY2.getRampartObject()));
        assertThat(metadata.isLoggable(ENTRY2.getName()), equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void duplicateMetadataEntry() {
        RampartMetadataValidator validator = createMetadataValidator(newRampartList(ENTRY1, ENTRY1));
        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateMetadata());
        assertThat(thrown.getMessage(), equalTo("duplicate metadata entry \"" + ENTRY1.getName() + "\" detected"));
    }

    @Test
    public void duplicateMetadataEntryLoggable() {
        RampartMetadataValidator validator = createMetadataValidator(
                newRampartList(createCompoundedLoggableEntry(ENTRY1), createCompoundedLoggableEntry(ENTRY1)));
        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateMetadata());
        assertThat(thrown.getMessage(), equalTo("duplicate metadata entry \"" + ENTRY1.getName() + "\" detected"));
    }

    @Test
    public void duplicateMetadataEntryDescriptiveAndLoggable() {
        RampartMetadataValidator validator = createMetadataValidator(
                newRampartList(ENTRY1, createCompoundedLoggableEntry(ENTRY1)));
        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateMetadata());
        assertThat(thrown.getMessage(), equalTo("duplicate metadata entry \"" + ENTRY1.getName() + "\" detected"));
    }

    @Test
    public void singleLoggableMetadata() throws ValidationError {
        RampartMetadataValidator validator = createMetadataValidator(
                newRampartList(createCompoundedLoggableEntry(ENTRY1)));
        RampartMetadata metadata = validator.validateMetadata();
        assertThat(metadata.get(ENTRY1.getName()), equalTo(ENTRY1.getRampartObject()));
        assertThat(metadata.isLoggable(ENTRY1.getName()), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void singleRecursiveLoggableMetadata() throws ValidationError {
        RampartMetadataValidator validator = createMetadataValidator(
                createMultiLoggableEntries(
                        newRampartNamedValue(LOG_KEY, ENTRY1.getRampartObject())));
        RampartMetadata metadata = validator.validateMetadata();
        assertThat(metadata.get(LOG_KEY), equalTo(ENTRY1.getRampartObject()));
        assertThat(metadata.isLoggable(LOG_KEY), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void multipleCompoundedLoggableMetadata() throws ValidationError {
        RampartMetadataValidator validator = createMetadataValidator(
                newRampartList(createCompoundedLoggableEntry(ENTRY1, ENTRY2)));
        RampartMetadata metadata = validator.validateMetadata();
        assertThat(metadata.get(ENTRY1.getName()), equalTo(ENTRY1.getRampartObject()));
        assertThat(metadata.isLoggable(ENTRY1.getName()), equalTo(RampartBoolean.TRUE));
        assertThat(metadata.get(ENTRY2.getName()), equalTo(ENTRY2.getRampartObject()));
        assertThat(metadata.isLoggable(ENTRY2.getName()), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void multipleLoggableMetadataEntries() throws ValidationError {
        RampartMetadataValidator validator = createMetadataValidator(createMultiLoggableEntries(ENTRY1, ENTRY2));
        RampartMetadata metadata = validator.validateMetadata();
        assertThat(metadata.get(ENTRY1.getName()), equalTo(ENTRY1.getRampartObject()));
        assertThat(metadata.isLoggable(ENTRY1.getName()), equalTo(RampartBoolean.TRUE));
        assertThat(metadata.get(ENTRY2.getName()), equalTo(ENTRY2.getRampartObject()));
        assertThat(metadata.isLoggable(ENTRY2.getName()), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void singleDescriptiveAndLoggableMetadata() throws ValidationError {
        RampartMetadataValidator validator = createMetadataValidator(
                newRampartList(ENTRY1, createCompoundedLoggableEntry(ENTRY2)));
        RampartMetadata metadata = validator.validateMetadata();
        assertThat(metadata.get(ENTRY1.getName()), equalTo(ENTRY1.getRampartObject()));
        assertThat(metadata.isLoggable(ENTRY1.getName()), equalTo(RampartBoolean.FALSE));
        assertThat(metadata.get(ENTRY2.getName()), equalTo(ENTRY2.getRampartObject()));
        assertThat(metadata.isLoggable(ENTRY2.getName()), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void singleDescriptiveMetadataCalledLog() throws ValidationError {
        RampartMetadataValidator validator = createMetadataValidator(newRampartList(
                newRampartNamedValue(LOG_KEY, ENTRY1.getRampartObject())));
        RampartMetadata metadata = validator.validateMetadata();
        assertThat(metadata.get(LOG_KEY), equalTo(ENTRY1.getRampartObject()));
        assertThat(metadata.isLoggable(LOG_KEY), equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void invalidLoggableMetadata() {
        RampartMetadataValidator validator = createMetadataValidator(
                createMultiLoggableEntries(
                        newRampartString("invalid-entry")));
        ValidationError thrown = assertThrows(ValidationError.class,
                () -> validator.validateMetadata());
        assertThat(thrown.getMessage(), equalTo("metadata entries can only be key value pairs"));
    }

    @Test
    public void validateStandardizedDescriptiveMetadata() throws ValidationError {
        final RampartConstant customFieldKey = newRampartConstant("MY_KEY");
        RampartNamedValue metadataEntry = newRampartNamedValue(
                customFieldKey, newRampartString("MY_VALUE"));
        final RampartObject newValue = newRampartString("REPLACED");
        final Map<RampartConstant, RampartMetadataEntryValidator> entryValidator =
            Collections.singletonMap(
                customFieldKey,
                new RampartMetadataEntryValidator() {
                    @Override
                    public RampartObject validateValue(RampartConstant entryKey, RampartObject value) {
                        return newValue;
                    }
                }
            );
        final RampartMetadata metadata = RampartMetadataParser.parseMetadata(newRampartList(metadataEntry), entryValidator);
        assertThat(newValue, equalTo(metadata.get(customFieldKey)));
    }

    @Test
    public void validateStandardizedLoggableMetadata() throws ValidationError {
        final RampartConstant customFieldKey = newRampartConstant("MY_KEY");
        RampartNamedValue metadataEntry = newRampartNamedValue(
                customFieldKey, newRampartString("MY_VALUE"));
        final RampartString replacedValue = newRampartString("REPLACED");
        final Map<RampartConstant, RampartMetadataEntryValidator> entryValidator =
            Collections.singletonMap(
                customFieldKey,
                new RampartMetadataEntryValidator() {
                    @Override
                    public RampartObject validateValue(RampartConstant entryKey, RampartObject value) {
                        return replacedValue;
                    }
                }
            );
        final RampartMetadata metadata = RampartMetadataParser.parseMetadata(
                createMultiLoggableEntries(metadataEntry), entryValidator);
        assertThat(replacedValue, equalTo(metadata.get(customFieldKey)));
    }
}
