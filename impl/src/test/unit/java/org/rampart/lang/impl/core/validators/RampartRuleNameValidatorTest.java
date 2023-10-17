package org.rampart.lang.impl.core.validators;

import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

public class RampartRuleNameValidatorTest {
    private static final RampartString SAMPLE_RULE_NAME = newRampartString("Sample Rule Name");

    @Test
    public void validationPassesWithValidInput() throws InvalidRampartRuleException {
        RampartString validatedRampartString = new RuleNameValidator(
                newRampartList(SAMPLE_RULE_NAME)).validateRuleName();
        assertThat(validatedRampartString, equalTo(SAMPLE_RULE_NAME));

    }

    @Test
    public void nullValueThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> new RuleNameValidator(RampartList.EMPTY).validateRuleName());

        assertThat(thrown.getMessage(), equalTo("Rule name is missing"));
    }

    @Test
    public void emptyRuleNameStringThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> new RuleNameValidator(newRampartList(newRampartString(""))).validateRuleName());

        assertThat(thrown.getMessage(), equalTo("Rule name is missing"));
    }
}
