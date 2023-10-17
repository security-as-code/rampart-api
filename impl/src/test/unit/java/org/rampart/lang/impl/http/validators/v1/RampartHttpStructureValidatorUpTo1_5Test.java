package org.rampart.lang.impl.http.validators.v1;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.impl.http.validators.v1.RampartHttpStructureValidatorUpTo1_5.VALID_HTTP_SYMBOL_TABLE_KEYS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rampart.lang.impl.http.validators.v1.RampartHttpStructureValidatorUpTo1_5;

@SuppressWarnings("deprecation")
public class RampartHttpStructureValidatorUpTo1_5Test {
    private Map<String, RampartList> symbolTable;

    @BeforeEach
    public void setup() {
        symbolTable = new LinkedHashMap<>();
    }

    @Test
    public void validKeySetIsValidatedSuccessfully() throws InvalidRampartRuleException {
        symbolTable.put(HTTP_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(REQUEST_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(VALIDATE_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(ACTION_KEY.toString(), RampartList.EMPTY);
        new RampartHttpStructureValidatorUpTo1_5(symbolTable).validateHttpStructure();
    }

    @Test
    public void invalidKeyThrowsException() {
        String invalidKey = "bla";
        symbolTable.put(invalidKey, RampartList.EMPTY);
        symbolTable.put(HTTP_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(VALIDATE_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(ACTION_KEY.toString(), RampartList.EMPTY);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpStructureValidatorUpTo1_5(symbolTable).validateHttpStructure());

        assertThat(thrown.getMessage(), equalTo(
                "unsupported element found. All http elements must be one of: " + VALID_HTTP_SYMBOL_TABLE_KEYS));
    }

    @Test
    public void missingHttpIOTypeThrowsException() {
        symbolTable.put(HTTP_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(VALIDATE_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(ACTION_KEY.toString(), RampartList.EMPTY);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpStructureValidatorUpTo1_5(symbolTable).validateHttpStructure());

        assertThat(thrown.getMessage(), equalTo("RAMPART http rules must contain a \"request\" value"));
    }

    @Test
    public void validateWithoutActionThrowsException() {
        symbolTable.put(HTTP_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(REQUEST_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(VALIDATE_KEY.toString(), RampartList.EMPTY);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpStructureValidatorUpTo1_5(symbolTable).validateHttpStructure());

        assertThat(thrown.getMessage(), equalTo("RAMPART http rules must contain both \"action\" and \"validate\""));
    }
}
