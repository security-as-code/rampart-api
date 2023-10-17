package org.rampart.lang.impl.http.validators.v2;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.http.RampartOpenRedirect;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OpenRedirectValidator2_2Test {

    private HashMap<String, RampartList> symbolTable;

    @BeforeEach
    public void setup() {
        symbolTable = new HashMap<>();
    }

    @Test
    public void missingOpenRedirectDeclaration() throws InvalidRampartRuleException {
        assertThat(new OpenRedirectValidator2_2(symbolTable).validateRedirectDeclaration(), nullValue());
    }

    @Test
    public void emptyOpenRedirectDeclaration() throws InvalidRampartRuleException {
        symbolTable.put(OPEN_REDIRECT_KEY.toString(), RampartList.EMPTY);
        assertThat(new OpenRedirectValidator2_2(symbolTable).validateRedirectDeclaration(), not(nullValue()));
    }

    @Test
    public void openRedirectDeclarationWithConstant() {
        symbolTable.put(OPEN_REDIRECT_KEY.toString(), newRampartList(newRampartConstant("on-redirect")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new OpenRedirectValidator2_2(symbolTable).validateRedirectDeclaration());

        assertThat(thrown.getMessage(), equalTo("invalid parameter \"on-redirect\" for \"open-redirect\" declaration"));
    }

    @Test
    public void openRedirectDeclarationEmptyOptions() {
        symbolTable.put(OPEN_REDIRECT_KEY.toString(), newRampartList(
                newRampartNamedValue(OPTIONS_KEY, RampartList.EMPTY)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new OpenRedirectValidator2_2(symbolTable).validateRedirectDeclaration());

        assertThat(thrown.getMessage(), equalTo("\"" + OPTIONS_KEY + "\" parameter must be followed by a non empty list"));
    }

    @Test
    public void openRedirectDeclarationWithExcludeSubdomainsOption() throws Exception {
        symbolTable.put(OPEN_REDIRECT_KEY.toString(), newRampartList(
                newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(EXCLUDE_KEY, SUBDOMAINS_KEY)))));

        RampartOpenRedirect redirectHandle =
                new OpenRedirectValidator2_2(symbolTable).validateRedirectDeclaration();
        assertThat(redirectHandle.shouldExcludeSubdomains(), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void openRedirectDeclarationWithUnrecognizedOption() {
        symbolTable.put(OPEN_REDIRECT_KEY.toString(), newRampartList(
                newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(
                                newRampartConstant("unsupported"),
                                newRampartConstant("undefined"))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new OpenRedirectValidator2_2(symbolTable).validateRedirectDeclaration());

        assertThat(thrown.getMessage(), equalTo("option \"unsupported\" is unsupported for \"options\" parameter"));
    }

    @Test
    public void openRedirectDeclarationWithUnrecognizedExcludeOptionValue() {
        symbolTable.put(OPEN_REDIRECT_KEY.toString(), newRampartList(
                newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(
                                EXCLUDE_KEY,
                                newRampartConstant("undefined"))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new OpenRedirectValidator2_2(symbolTable).validateRedirectDeclaration());

        assertThat(thrown.getMessage(), equalTo("incorrect value \"undefined\" for option \"exclude\""));
    }

    @Test
    public void openRedirectDeclarationWithInvalidExcludeOptionValueType() {
        symbolTable.put(OPEN_REDIRECT_KEY.toString(), newRampartList(
                newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(
                                EXCLUDE_KEY,
                                newRampartString("subdomains"))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new OpenRedirectValidator2_2(symbolTable).validateRedirectDeclaration());

        assertThat(thrown.getMessage(), equalTo("incorrect value \"subdomains\" for option \"exclude\""));
    }
}
