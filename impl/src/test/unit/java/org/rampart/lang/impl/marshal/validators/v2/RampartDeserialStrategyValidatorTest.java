package org.rampart.lang.impl.marshal.validators.v2;

import static org.rampart.lang.api.constants.RampartMarshalConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

public class RampartDeserialStrategyValidatorTest {

    private static final String TEST_KEY = "test";

    private HashMap<String, RampartList> symbolTable;
    private RampartDeserialStrategyValidator2_0 validator;

    @BeforeEach
    public void setup() {
        symbolTable = new HashMap<>();
        validator = new RampartDeserialStrategyValidator2_0(symbolTable);
    }

    @Test
    public void rceAndDosStrategiesAreMissing() {
        symbolTable.put(TEST_KEY, mock(RampartList.class));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> validator.validateStrategy());

        assertThat(thrown.getMessage(),
                equalTo("RAMPART marshal rules must contain at least one of \"rce\" or \"dos\" declarations"));
    }

    @Test
    public void noKeysDeclared() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> validator.validateStrategy());

        assertThat(thrown.getMessage(),
                equalTo("RAMPART marshal rules must contain at least one of \"rce\" or \"dos\" declarations"));
    }

    @Test
    public void bothRceAndDosStrategiesDeclared() {
        symbolTable.put(RCE_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(DOS_KEY.toString(), RampartList.EMPTY);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> validator.validateStrategy());

        assertThat(thrown.getMessage(),
                equalTo("RAMPART marshal rules must contain only one of \"rce\" or \"dos\" declarations"));
    }

    @Test
    public void rceStrategyDeclared() throws InvalidRampartRuleException {
        symbolTable.put(RCE_KEY.toString(), RampartList.EMPTY);
        validator.validateStrategy();
    }

    @Test
    public void dosStrategyDeclared() throws InvalidRampartRuleException {
        symbolTable.put(DOS_KEY.toString(), RampartList.EMPTY);
        validator.validateStrategy();
    }

    @Test
    public void rceStrategyDeclaredWithParameters() {
        symbolTable.put(RCE_KEY.toString(), newRampartList(newRampartConstant("resources")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> validator.validateStrategy());

        assertThat(thrown.getMessage(), equalTo("declaration of \"rce\" in marshal rule cannot declare parameters"));
    }

    @Test
    public void dosStrategyDeclaredWithParameters() {
        symbolTable.put(DOS_KEY.toString(), newRampartList(newRampartInteger(1)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> validator.validateStrategy());

        assertThat(thrown.getMessage(), equalTo("declaration of \"dos\" in marshal rule cannot declare parameters"));
    }
}
