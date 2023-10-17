package org.rampart.lang.impl.patch.validators;

import static org.rampart.lang.api.constants.RampartPatchConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartNamedValue;
import org.rampart.lang.api.patch.RampartPatchType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RampartOccurrencesValidatorTest {
    private static final RampartPatchType LOCATION_TYPE = RampartPatchType.CALL;
    private static final String LOCATION_PARAMETER = "com/foo/bar.fn()V";

    private Map<String, RampartList> symbolTable;
    
    @BeforeEach
    public void setup() {
        symbolTable = new LinkedHashMap<>();
    }

    @Test
    public void occurrencesListValidatesSuccessfully() throws InvalidRampartRuleException {
        RampartNamedValue occurrences = newRampartNamedValue(OCCURRENCES_KEY, newRampartList(newRampartInteger(1)));
        initSymbolTableWithOccurrences(occurrences);
        new RampartOccurrencesValidator(symbolTable).validateOccurrencesValues(RampartPatchType.CALL);
    }

    @Test
    public void noOccurrencesValidatesSuccessfully() throws InvalidRampartRuleException {
        new RampartOccurrencesValidator(symbolTable).validateOccurrencesValues(RampartPatchType.CALL);
    }

    @Test
    public void unexpectedRampartTypeThrowsException() {
        RampartNamedValue occurrences = newRampartNamedValue(OCCURRENCES_KEY, newRampartInteger(2));
        initSymbolTableWithOccurrences(occurrences);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartOccurrencesValidator(symbolTable).validateOccurrencesValues(RampartPatchType.CALL));

        assertThat(thrown.getMessage(), containsString("must be an RampartList and not"));
    }

    @Test
    public void emptyOccurrenceListThrowsException() {
        RampartNamedValue occurrences = newRampartNamedValue(OCCURRENCES_KEY, RampartList.EMPTY);
        initSymbolTableWithOccurrences(occurrences);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartOccurrencesValidator(symbolTable).validateOccurrencesValues(RampartPatchType.CALL));

        assertThat(thrown.getMessage(), containsString("must be a non empty RampartList"));
    }

    @Test
    public void duplicateOccurrenceValueThrowsException() {
        RampartNamedValue occurrences = newRampartNamedValue(
                OCCURRENCES_KEY,
                newRampartList(newRampartInteger(2), newRampartInteger(2)));
        initSymbolTableWithOccurrences(occurrences);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartOccurrencesValidator(symbolTable).validateOccurrencesValues(RampartPatchType.CALL));

        assertThat(thrown.getMessage(), endsWith("duplicate occurrence [2]"));
    }

    @Test
    public void invalidOccurrenceTypeThrowsException() {
        RampartNamedValue occurrences = newRampartNamedValue(
                OCCURRENCES_KEY,
                newRampartList(newRampartString("invalid")));
        initSymbolTableWithOccurrences(occurrences);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartOccurrencesValidator(symbolTable).validateOccurrencesValues(RampartPatchType.CALL));

        assertThat(thrown.getMessage(), endsWith("occurrence [invalid] is not an integer"));
    }

    @Test
    public void negativeOccurrenceValueThrowsException() {
        RampartNamedValue occurrences = newRampartNamedValue(
                OCCURRENCES_KEY,
                newRampartList(newRampartInteger(-1)));
        initSymbolTableWithOccurrences(occurrences);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartOccurrencesValidator(symbolTable).validateOccurrencesValues(RampartPatchType.CALL));

        assertThat(thrown.getMessage(), endsWith("invalid occurrence index: -1. Valid occurrence indexes begin at 1"));
    }

    private void initSymbolTableWithOccurrences(RampartNamedValue occurrences) {
        symbolTable.put(LOCATION_TYPE.getName().toString(),
                newRampartList(newRampartString(LOCATION_PARAMETER), occurrences));
    }
}
