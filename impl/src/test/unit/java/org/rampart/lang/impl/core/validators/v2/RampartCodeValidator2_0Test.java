package org.rampart.lang.impl.core.validators.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import org.rampart.lang.impl.core.InvalidRampartRuleException;

public class RampartCodeValidator2_0Test {

    @Test
    public void validSourceLanguageForRampartConstant() throws InvalidRampartRuleException {
        new RampartCodeValidator2_0(
                newRampartList(newRampartNamedValue(
                        LANGUAGE_KEY, JAVA_KEY)),
                newRampartList(newRampartString("SourceCode")))
        .validateCodeBlock();
    }

    @Test
    public void validSourceLanguageForRampartString() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> new RampartCodeValidator2_0(
                newRampartList(newRampartNamedValue(
                        LANGUAGE_KEY, newRampartString("Java"))),
                newRampartList(newRampartString("SourceCode")))
        .validateCodeBlock());

        assertThat(thrown.getMessage(), equalTo("language definition must be a constant"));
    }

    @Test
    public void invalidSourceLanguageForRampartConstant() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> new RampartCodeValidator2_0(
            newRampartList(newRampartNamedValue(
                LANGUAGE_KEY,
                newRampartConstant("Python"))),
                newRampartList(newRampartString("SourceCode")))
        .validateCodeBlock());

        assertThat(thrown.getMessage(), startsWith("unsupported language for patch"));
    }
}
