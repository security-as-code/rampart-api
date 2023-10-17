package org.rampart.lang.java.parser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.rampart.lang.java.InvalidRampartAppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rampart.lang.java.parser.RampartSymbolTable;

public class RampartSymbolTableTest {
    private static final String MAP_KEY = "key";
    private static final String MAP_VALUE = "ruleName";

    private RampartSymbolTable<String, Object> symbolTable;

    @BeforeEach
    public void setup() {
        symbolTable = new RampartSymbolTable<>();
    }

    @Test
    public void putWithExistingKeyThrowsException() {
        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class, () -> {
            symbolTable.put(MAP_KEY, MAP_VALUE);
            symbolTable.put(MAP_KEY, MAP_VALUE);
        });

        assertThat(thrown.getMessage(),
                equalTo("duplicate RAMPART object \"" + MAP_KEY + "\" specified in rule: " + MAP_VALUE));
    }

    @Test
    public void putWithNewKeyIsValid() {
        symbolTable.put(MAP_KEY, MAP_VALUE);
        symbolTable.put("different_key", MAP_VALUE);
    }
}
