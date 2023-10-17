package org.rampart.lang.impl.core.validators.v1;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;

import org.rampart.lang.api.RampartNamedValue;
import org.junit.jupiter.api.Test;

import org.rampart.lang.impl.core.InvalidRampartRuleException;

public class RampartCodeValidatorUpTo1_5Test {
    private static final RampartNamedValue VALID_CODEBLOCK_LANGUAGE = newRampartNamedValue(
            LANGUAGE_KEY, newRampartString("Java"));

    private static final RampartNamedValue INVALID_CODEBLOCK_LANGUAGE = newRampartNamedValue(
            LANGUAGE_KEY, newRampartString("Python"));

    private static final RampartNamedValue VALID_CODEBLOCK_IMPORTS = newRampartNamedValue(
            IMPORT_KEY, newRampartList(newRampartString("java.io.IOException")));

    private static final RampartNamedValue INVALID_CODEBLOCK_IMPORTS = newRampartNamedValue(
            IMPORT_KEY, newRampartList(newRampartInteger(2)));

    @Test
    public void codeValuesAndSourcecodeValidatesSuccessfully() throws InvalidRampartRuleException {
        new RampartCodeValidatorUpTo1_5(
                newRampartList(VALID_CODEBLOCK_LANGUAGE),
                newRampartList(newRampartString("SourceCode"))).validateCodeBlock();
    }

    @Test
    public void cSharpLanguageValidatesSuccessfully() throws InvalidRampartRuleException {
        new RampartCodeValidatorUpTo1_5(
                newRampartList(newRampartNamedValue(LANGUAGE_KEY, newRampartString("Csharp"))),
                newRampartList(newRampartString("SourceCode"))).validateCodeBlock();
    }

    @Test
    public void optionalImportsValidateSuccessfully() throws InvalidRampartRuleException {
        new RampartCodeValidatorUpTo1_5(
                newRampartList(VALID_CODEBLOCK_LANGUAGE, VALID_CODEBLOCK_IMPORTS),
                newRampartList(newRampartString("SourceCode"))).validateCodeBlock();
    }

    @Test
    public void unsupportedCodeBlockLanguageThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartCodeValidatorUpTo1_5(
                        newRampartList(INVALID_CODEBLOCK_LANGUAGE, VALID_CODEBLOCK_IMPORTS),
                        newRampartList(newRampartString("SourceCode"))).validateCodeBlock());

        assertThat(thrown.getMessage(), startsWith("unsupported language for patch, must be one of"));
    }

    @Test
    public void emptyLanguageStringThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartCodeValidatorUpTo1_5(
                        newRampartList(newRampartNamedValue(LANGUAGE_KEY, newRampartString("")), VALID_CODEBLOCK_IMPORTS),
                        newRampartList(newRampartString("SourceCode"))).validateCodeBlock());

        assertThat(thrown.getMessage(), equalTo("missing language definition from code block"));
    }

    @Test
    public void missingCodeBlockLanguageThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartCodeValidatorUpTo1_5(
                        newRampartList(newRampartNamedValue(LANGUAGE_KEY, null), VALID_CODEBLOCK_IMPORTS),
                        newRampartList(newRampartString("SourceCode"))).validateCodeBlock());

        assertThat(thrown.getMessage(), equalTo("missing language definition from code block"));
    }

    @Test
    public void invalidImportTypeThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartCodeValidatorUpTo1_5(
                        newRampartList(VALID_CODEBLOCK_LANGUAGE, INVALID_CODEBLOCK_IMPORTS),
                        newRampartList(newRampartString("SourceCode"))).validateCodeBlock());

        assertThat(thrown.getMessage(), equalTo("imports definition must be a list of strings"));
    }

    @Test
    public void importsIsNotAListThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartCodeValidatorUpTo1_5(
                        newRampartList(VALID_CODEBLOCK_LANGUAGE, newRampartNamedValue(IMPORT_KEY, newRampartString("java.io.IOException"))),
                        newRampartList(newRampartString("source code"))).validateCodeBlock());

        assertThat(thrown.getMessage(), equalTo("imports definition must be a list of strings"));
    }

    @Test
    public void emptyImportsEntryThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartCodeValidatorUpTo1_5(
                        newRampartList(VALID_CODEBLOCK_LANGUAGE, newRampartNamedValue(IMPORT_KEY, newRampartList(newRampartString("")))),
                        newRampartList(newRampartString("sourcecode"))).validateCodeBlock());

        assertThat(thrown.getMessage(), equalTo("import list entry must be a non-empty string literal"));
    }

    @Test
    public void validationCompletesWithNoOptionalImports() throws InvalidRampartRuleException {
        new RampartCodeValidatorUpTo1_5(
                newRampartList(VALID_CODEBLOCK_LANGUAGE),
                newRampartList(newRampartString("SourceCode"))).validateCodeBlock();
    }

    @Test
    public void emptySourcecodeBlockThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartCodeValidatorUpTo1_5(
                        newRampartList(VALID_CODEBLOCK_LANGUAGE),
                        newRampartList(newRampartString(""))).validateCodeBlock());

        assertThat(thrown.getMessage(), equalTo("source code block of patch must be a non-empty string literal"));
    }

    @Test
    public void nonRampartStringValueForSourcecodeThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartCodeValidatorUpTo1_5(
                        newRampartList(VALID_CODEBLOCK_LANGUAGE),
                        newRampartList(newRampartInteger(5))).validateCodeBlock());

        assertThat(thrown.getMessage(), equalTo("source code block of patch must be a non-empty string literal"));
    }
}
