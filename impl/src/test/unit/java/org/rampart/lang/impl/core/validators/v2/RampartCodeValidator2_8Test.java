package org.rampart.lang.impl.core.validators.v2;

import org.rampart.lang.api.RampartNamedValue;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.junit.jupiter.api.Test;

import static org.rampart.lang.api.constants.RampartGeneralConstants.IMPORT_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.LANGUAGE_KEY;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RampartCodeValidator2_8Test {
    private static final RampartNamedValue VALID_CODEBLOCK_LANGUAGE = newRampartNamedValue(
            LANGUAGE_KEY, newRampartConstant("Java"));

    private static final RampartNamedValue INVALID_CODEBLOCK_LANGUAGE = newRampartNamedValue(
            LANGUAGE_KEY, newRampartConstant("Python"));

    private static final RampartNamedValue JAVASCRIPT_CODEBLOCK = newRampartNamedValue(
            LANGUAGE_KEY, newRampartConstant("javascript"));

    private static final RampartNamedValue VALID_JAVASCRIPT_IMPORTS = newRampartNamedValue(
            IMPORT_KEY, newRampartList(newRampartString("import { export1 } from \"module-name\";")));


    @Test
    public void codeValuesAndSourcecodeValidatesSuccessfully() throws InvalidRampartRuleException {
        new RampartCodeValidator2_8(
                newRampartList(VALID_CODEBLOCK_LANGUAGE),
                newRampartList(newRampartString("SourceCode"))).validateCodeBlock();
    }

    @Test
    public void cSharpLanguageValidatesSuccessfully() throws InvalidRampartRuleException {
        new RampartCodeValidator2_8(
                newRampartList(newRampartNamedValue(LANGUAGE_KEY, newRampartConstant("Csharp"))),
                newRampartList(newRampartString("SourceCode"))).validateCodeBlock();
    }

    @Test
    public void javascriptLanguageAndSourcecodeValidatesSuccessfully() throws InvalidRampartRuleException {
        new RampartCodeValidator2_8(
                newRampartList(JAVASCRIPT_CODEBLOCK),
                newRampartList(newRampartString("var x=1;"))).validateCodeBlock();
    }

    @Test
    public void javascriptLanguageAndImportsAndSourcecodeValidatesSuccessfully() throws InvalidRampartRuleException {
        new RampartCodeValidator2_8(
                newRampartList(JAVASCRIPT_CODEBLOCK, VALID_JAVASCRIPT_IMPORTS),
                newRampartList(newRampartString("var x=1;"))).validateCodeBlock();
    }

    @Test
    public void unsupportedCodeBlockLanguageThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartCodeValidator2_8(
                        newRampartList(INVALID_CODEBLOCK_LANGUAGE),
                        newRampartList(newRampartString("SourceCode"))).validateCodeBlock());

        assertThat(thrown.getMessage(), startsWith("unsupported language for patch, must be one of"));
    }

}
