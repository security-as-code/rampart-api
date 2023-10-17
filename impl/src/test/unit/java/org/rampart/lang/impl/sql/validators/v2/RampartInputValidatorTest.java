package org.rampart.lang.impl.sql.validators.v2;

import static org.rampart.lang.api.constants.RampartSqlConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartInput;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.v2.RampartInputValidator;

public class RampartInputValidatorTest {

    private Map<String, RampartList> symbolTable;

    @BeforeEach
    public void setUp() {
        symbolTable = new HashMap<>();
    }

    @Test
    public void noInputElementDefaults() throws InvalidRampartRuleException {
        assertThat(new RampartInputValidator(symbolTable).validateDataInputs(), equalTo(RampartList.EMPTY));
    }

    @Test
    public void allValidInputs() throws InvalidRampartRuleException {
        symbolTable.put(INPUT_KEY.toString(), newRampartList(
                DATABASE_KEY,
                HTTP_KEY,
                DESERIALIZATION_KEY));

        assertThat(new RampartInputValidator(symbolTable).validateDataInputs(), equalTo(
                newRampartList(
                        RampartInput.DATABASE,
                        RampartInput.HTTP,
                        RampartInput.DESERIALIZATION)));
    }

    @Test
    public void emptyInputs() {
        symbolTable.put(INPUT_KEY.toString(), RampartList.EMPTY);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartInputValidator(symbolTable).validateDataInputs());

        assertThat(thrown.getMessage(),
                equalTo("\"" + INPUT_KEY + "\" declaration must be followed by a non empty list"));
    }

    @Test
    public void oneOfValidInputs() throws InvalidRampartRuleException {
        symbolTable.put(INPUT_KEY.toString(), newRampartList(HTTP_KEY));
        assertThat(new RampartInputValidator(symbolTable).validateDataInputs(), equalTo(newRampartList(RampartInput.HTTP)));
    }

    @Test
    public void invalidInputs() {
        symbolTable.put(INPUT_KEY.toString(), newRampartList(
                newRampartConstant("miscelaneous"),
                newRampartConstant("foreign")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartInputValidator(symbolTable).validateDataInputs());

        assertThat(thrown.getMessage(), equalTo("unrecognized parameter \"miscelaneous\" for RAMPART rule"));
    }

    @Test
    public void mixedValidInvalidInputs() {
        symbolTable.put(INPUT_KEY.toString(), newRampartList(
                HTTP_KEY,
                newRampartConstant("foreign"),
                newRampartConstant("database")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartInputValidator(symbolTable).validateDataInputs());

        assertThat(thrown.getMessage(), equalTo("unrecognized parameter \"foreign\" for RAMPART rule"));
    }

    @Test
    public void invalidInputType() {
        symbolTable.put(INPUT_KEY.toString(), newRampartList(newRampartString("database")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartInputValidator(symbolTable).validateDataInputs());

        assertThat(thrown.getMessage(), equalTo("\"" + INPUT_KEY + "\" declaration list entries must be constants"));
    }

    @Test
    public void invalidInputFormat() {
        symbolTable.put(INPUT_KEY.toString(), newRampartList(
                newRampartInteger(0)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartInputValidator(symbolTable).validateDataInputs());

        assertThat(thrown.getMessage(), equalTo("\"" + INPUT_KEY + "\" declaration list entries must be constants"));
    }

    @Test
    public void invalidInputFormatMixedValidInput() {
        symbolTable.put(INPUT_KEY.toString(), newRampartList(
                newRampartInteger(1), HTTP_KEY));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartInputValidator(symbolTable).validateDataInputs());

        assertThat(thrown.getMessage(), equalTo("\"" + INPUT_KEY + "\" declaration list entries must be constants"));
    }

}
