package org.rampart.lang.impl.sanitization.validators.v2;

import static org.rampart.lang.api.constants.RampartSanitizationConstants.LOGGING_KEY;
import static org.rampart.lang.api.constants.RampartSanitizationConstants.OFF_KEY;
import static org.rampart.lang.api.constants.RampartSanitizationConstants.SAFE_KEY;
import static org.rampart.lang.api.constants.RampartSanitizationConstants.UNDETERMINED_KEY;
import static org.rampart.lang.api.constants.RampartSanitizationConstants.UNSAFE_KEY;
import static org.rampart.lang.api.constants.RampartSanitizationConstants.VALUES_KEY;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class RampartUndeterminedValidator2_3Test {

    private HashMap<String, RampartList> symbolTable;
    private RampartUndeterminedValidator2_3 undeterminedValidator;

    @BeforeEach
    public void setup() {
        symbolTable = new HashMap<>();
        undeterminedValidator = new RampartUndeterminedValidator2_3(symbolTable);
    }

    @Test
    public void successful_valuesSafe() throws InvalidRampartRuleException {
        symbolTable.put(UNDETERMINED_KEY.toString(), newRampartList(
                newRampartNamedValue(VALUES_KEY, SAFE_KEY)
        ));

        undeterminedValidator.validate();
    }

    @Test
    public void successful_valuesUnsafe() throws InvalidRampartRuleException {
        symbolTable.put(UNDETERMINED_KEY.toString(), newRampartList(
                newRampartNamedValue(VALUES_KEY, UNSAFE_KEY)
        ));

        undeterminedValidator.validate();
    }

    @Test
    public void ThrowsException_invalidDirective() {
        symbolTable.put("Invalid", newRampartList(
                newRampartNamedValue(VALUES_KEY, UNSAFE_KEY),
                newRampartNamedValue(LOGGING_KEY, OFF_KEY)
        ));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> undeterminedValidator.validate());

        assertThat(thrown.getMessage(), equalTo("missing mandatory \"undetermined\" declaration"));
    }

    @Test
    public void ThrowsException_invalidValues() {
        symbolTable.put(UNDETERMINED_KEY.toString(), newRampartList(
                newRampartNamedValue(VALUES_KEY, newRampartConstant("foo"))
        ));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> undeterminedValidator.validate());

        assertThat(thrown.getMessage(), equalTo(
                "\"values\" declaration in sanitize rule only supports \"safe\"" +
                " or \"unsafe\" constants as parameters"));
    }

}
