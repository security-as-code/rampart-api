package org.rampart.lang.impl.patch.validators.v2;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.patch.RampartPatchType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

import static org.rampart.lang.java.RampartPrimitives.newRampartInteger;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("deprecation")
public class RampartLocationValidatorUpTo2_0Test {
    private Map<String, RampartList> symbolTable;

    @BeforeEach
    public void setup() {
        symbolTable = new LinkedHashMap<>();
    }

    @Test
    public void entryWithNoParameterValidatesSuccessfully() throws InvalidRampartRuleException {
        symbolTable.put(RampartPatchType.ENTRY.getName().toString(), RampartList.EMPTY);
        new RampartLocationValidatorUpTo2_0(symbolTable).validateLocationSpecifier(RampartPatchType.ENTRY);
    }

    @Test
    public void entryWithNonEmptyParameterThrowsException() {
        String locationKey = RampartPatchType.ENTRY.getName().toString();
        symbolTable.put(locationKey, newRampartList(newRampartInteger(1)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartLocationValidatorUpTo2_0(symbolTable).validateLocationSpecifier(RampartPatchType.ENTRY));

        assertThat(thrown.getMessage(), equalTo(locationKey + " location must not contain any parameters"));
    }

    @Test
    public void entryWithStringParameterThrowsException() {
        String locationKey = RampartPatchType.ENTRY.getName().toString();
        symbolTable.put(locationKey, newRampartList(newRampartString("entry")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartLocationValidatorUpTo2_0(symbolTable).validateLocationSpecifier(RampartPatchType.ENTRY));

        assertThat(thrown.getMessage(), equalTo(locationKey + " location must not contain any parameters"));
    }

    @Test
    public void lineWithIntegerParameterValidatesSuccessfully() throws InvalidRampartRuleException {
        symbolTable.put(RampartPatchType.LINE.getName().toString(), newRampartList(newRampartInteger(1)));
        new RampartLocationValidatorUpTo2_0(symbolTable).validateLocationSpecifier(RampartPatchType.LINE);
    }

    @Test
    public void callWithStringParameterValidatesSuccessfully() throws InvalidRampartRuleException {
        symbolTable.put(RampartPatchType.CALL.getName().toString(), newRampartList(newRampartString("com/foo/bar.fn()V")));
        new RampartLocationValidatorUpTo2_0(symbolTable).validateLocationSpecifier(RampartPatchType.CALL);
    }

    @Test
    public void callWithNoParameterThrowsException() {
        String locationKey = RampartPatchType.CALL.getName().toString();
        symbolTable.put(locationKey, null);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartLocationValidatorUpTo2_0(symbolTable).validateLocationSpecifier(RampartPatchType.CALL));

        assertThat(thrown.getMessage(), equalTo("call location must contain a string as a parameter"));
    }

    @Test
    public void callWithIntegerParameterThrowsException() {
        symbolTable.put(RampartPatchType.CALL.getName().toString(), newRampartList(newRampartInteger(1)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartLocationValidatorUpTo2_0(symbolTable).validateLocationSpecifier(RampartPatchType.CALL));

        assertThat(thrown.getMessage(), equalTo("call location must contain a string as a parameter"));
    }

    @Test
    public void lineWithStringParameterThrowsException() {
        symbolTable.put(RampartPatchType.LINE.getName().toString(), newRampartList(newRampartString("com/foo/bar.fn()V")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartLocationValidatorUpTo2_0(symbolTable).validateLocationSpecifier(RampartPatchType.LINE));

        assertThat(thrown.getMessage(), equalTo("line location must contain an integer as a parameter"));
    }

}
