package org.rampart.lang.impl.core.validators.v2;

import static org.rampart.lang.java.RampartPrimitives.newRampartConstant;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import org.rampart.lang.impl.core.validators.FirstClassRuleObjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RampartRuleStructureValidator2_0Test {
    private static final RampartConstant ALLOW_KEY1 = newRampartConstant("key1");
    private static final RampartConstant ALLOW_KEY2 = newRampartConstant("key2");
    private static final RampartConstant ALLOW_KEY3 = newRampartConstant("key3");

    private RampartRuleStructureValidator2_0Plus ruleStructureValidator;
    private FirstClassRuleObjectValidator validator1;
    private FirstClassRuleObjectValidator validator2;
    private FirstClassRuleObjectValidator validator3;

    private RampartString TEST_RULE_NAME = newRampartString("test rule");

    @BeforeEach
    public void setup() {
        validator1 = mock(FirstClassRuleObjectValidator.class);
        validator2 = mock(FirstClassRuleObjectValidator.class);
        validator3 = mock(FirstClassRuleObjectValidator.class);

        HashMap<String, RampartList> symbolTable = new HashMap<>();
        symbolTable.put(ALLOW_KEY1.toString(), mock(RampartList.class));
        symbolTable.put(ALLOW_KEY2.toString(), mock(RampartList.class));
        symbolTable.put(ALLOW_KEY3.toString(), mock(RampartList.class));
        ruleStructureValidator = new RampartRuleStructureValidator2_0Plus(symbolTable, RampartRuleType.PATCH);
    }

    @Test
    public void oneValidatorNoAllowedKeys() {
        when(validator1.allowedKeys()).thenReturn(Collections.emptyList());

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> {
            ruleStructureValidator.feedValidators(validator1);
            ruleStructureValidator.validateDeclarations(TEST_RULE_NAME);
        });

        assertThat(thrown.getMessage(), equalTo(
                "\"key1\" is not a recognized declaration in rule \"test rule\""));
    }

    @Test
    public void oneValidatorAllAllowedKeys() throws InvalidRampartRuleException {
        when(validator1.allowedKeys()).thenReturn(Arrays.asList(ALLOW_KEY1, ALLOW_KEY2, ALLOW_KEY3));
        ruleStructureValidator.feedValidators(validator1);
        ruleStructureValidator.validateDeclarations(TEST_RULE_NAME);
    }

    @Test
    public void oneValidatorSingleAllowedKey() {
        when(validator1.allowedKeys()).thenReturn(Collections.singletonList(ALLOW_KEY1));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> {
            ruleStructureValidator.feedValidators(validator1);
            ruleStructureValidator.validateDeclarations(TEST_RULE_NAME);
        });

        assertThat(thrown.getMessage(), equalTo(
                "\"key2\" is not a recognized declaration in rule \"test rule\""));
    }

    @Test
    public void oneValidatorSomeAllowedKey() {
        when(validator1.allowedKeys()).thenReturn(Arrays.asList(ALLOW_KEY1, ALLOW_KEY2));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> {
            ruleStructureValidator.feedValidators(validator1);
            ruleStructureValidator.validateDeclarations(TEST_RULE_NAME);
        });

        assertThat(thrown.getMessage(), equalTo(
                "\"key3\" is not a recognized declaration in rule \"test rule\""));
    }

    @Test
    public void multipleValidatorsNoAllowedKeys() {
        when(validator1.allowedKeys()).thenReturn(Collections.emptyList());
        when(validator2.allowedKeys()).thenReturn(Collections.emptyList());
        when(validator3.allowedKeys()).thenReturn(Collections.emptyList());

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> {
            ruleStructureValidator.feedValidators(validator1, validator2, validator3);
            ruleStructureValidator.validateDeclarations(TEST_RULE_NAME);
        });

        assertThat(thrown.getMessage(), equalTo(
                "\"key1\" is not a recognized declaration in rule \"test rule\""));
    }

    @Test
    public void multipleValidatorsSingleAllowedKey() {
        when(validator1.allowedKeys()).thenReturn(Collections.singletonList(ALLOW_KEY1));
        when(validator2.allowedKeys()).thenReturn(Collections.emptyList());
        when(validator3.allowedKeys()).thenReturn(Collections.emptyList());

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> {
            ruleStructureValidator.feedValidators(validator1, validator2, validator3);
            ruleStructureValidator.validateDeclarations(TEST_RULE_NAME);
        });

        assertThat(thrown.getMessage(), equalTo(
                "\"key2\" is not a recognized declaration in rule \"test rule\""));
    }

    @Test
    public void multipleValidatorsOneAllowedKeyPerValidator() throws InvalidRampartRuleException {
        when(validator1.allowedKeys()).thenReturn(Collections.singletonList(ALLOW_KEY1));
        when(validator2.allowedKeys()).thenReturn(Collections.singletonList(ALLOW_KEY2));
        when(validator3.allowedKeys()).thenReturn(Collections.singletonList(ALLOW_KEY3));

        ruleStructureValidator.feedValidators(validator1, validator2, validator3);
        ruleStructureValidator.validateDeclarations(TEST_RULE_NAME);
    }

    @Test
    public void multipleValidatorsAllAllowedKeySingleValidator() throws InvalidRampartRuleException {
        when(validator1.allowedKeys()).thenReturn(Arrays.asList(ALLOW_KEY1, ALLOW_KEY2, ALLOW_KEY3));
        when(validator2.allowedKeys()).thenReturn(Collections.emptyList());
        when(validator3.allowedKeys()).thenReturn(Collections.emptyList());

        ruleStructureValidator.feedValidators(validator1, validator2, validator3);
        ruleStructureValidator.validateDeclarations(TEST_RULE_NAME);
    }

    @Test
    public void multipleValidatorsAllAllowedKeysShared() throws InvalidRampartRuleException {
        when(validator1.allowedKeys()).thenReturn(Arrays.asList(ALLOW_KEY1, ALLOW_KEY2));
        when(validator2.allowedKeys()).thenReturn(Arrays.asList(ALLOW_KEY2, ALLOW_KEY3));
        when(validator3.allowedKeys()).thenReturn(Arrays.asList(ALLOW_KEY3, ALLOW_KEY1));

        ruleStructureValidator.feedValidators(validator1, validator2, validator3);
        ruleStructureValidator.validateDeclarations(TEST_RULE_NAME);
    }

}
