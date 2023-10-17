package org.rampart.lang.impl.core.validators.v1;

import static org.rampart.lang.java.RampartPrimitives.newRampartConstant;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.rampart.lang.api.RampartList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("deprecation")
public class RampartRuleStructureValidatorUpTo1_5Test {

    private Map<String, RampartList> symbolTable;
    private RampartRuleStructureValidatorUpTo1_5 ruleStructureValidator;

    @BeforeEach
    public void setup() {
        symbolTable = new LinkedHashMap<>();
        ruleStructureValidator = null;
    }

    @Test
    public void containsAllWithKeySetContainingAllValues() {
        symbolTable.put("test1", RampartList.EMPTY);
        symbolTable.put("test2", RampartList.EMPTY);

        ruleStructureValidator = new RampartRuleStructureValidatorImpl(symbolTable);
        assertThat(ruleStructureValidator.keySetContainsAllKeys(
                newRampartList(newRampartString("test1"), newRampartString("test2"))), equalTo(true));
    }

    @Test
    public void containsAllWithKeySetNotContainingAllValues() {
        symbolTable.put("test1", RampartList.EMPTY);
        symbolTable.put("test2", RampartList.EMPTY);

        ruleStructureValidator = new RampartRuleStructureValidatorImpl(symbolTable);
        assertThat(ruleStructureValidator.keySetContainsAllKeys(
                newRampartList(newRampartString("test1"), newRampartString("test2"), newRampartString("test3"))), equalTo(false));
    }

    @Test
    public void keySetContainsInvalidKeyFalse() {
        symbolTable.put("test1", RampartList.EMPTY);
        symbolTable.put("test2", RampartList.EMPTY);

        ruleStructureValidator = new RampartRuleStructureValidatorImpl(symbolTable);
        assertThat(ruleStructureValidator.keySetContainsInvalidKey(
                newRampartList(newRampartConstant("test1"), newRampartConstant("test2"))), equalTo(false));
    }

    @Test
    public void keySetContainsInvalidKeyTrue() {
        symbolTable.put("test1", RampartList.EMPTY);
        symbolTable.put("test2", RampartList.EMPTY);

        ruleStructureValidator = new RampartRuleStructureValidatorImpl(symbolTable);
        assertThat(ruleStructureValidator.keySetContainsInvalidKey(
                newRampartList(newRampartConstant("test1"))), equalTo(true));
    }

    /**
     * RampartRuleStructureValidator is abstract and cannot be instantiated
     * Concrete class below is used instead
     */
    private static class RampartRuleStructureValidatorImpl extends RampartRuleStructureValidatorUpTo1_5 {
        RampartRuleStructureValidatorImpl(Map<String, RampartList> visitorSymbolTable) {
            super(visitorSymbolTable);
        }
    }
}
