package org.rampart.lang.impl.http.validators.v2;

import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.rampart.lang.api.constants.RampartHttpConstants.*;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

public class OpenRedirectValidator2_0Test {

    private HashMap<String, RampartList> symbolTable;

    @BeforeEach
    public void setup() {
        symbolTable = new HashMap<>();
    }

    @Test
    public void missingOpenRedirectDeclaration() throws InvalidRampartRuleException {
        assertThat(new OpenRedirectValidator2_0(symbolTable).validateRedirect(), equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void emptyOpenRedirectDeclaration() throws InvalidRampartRuleException {
        symbolTable.put(OPEN_REDIRECT_KEY.toString(), RampartList.EMPTY);
        assertThat(new OpenRedirectValidator2_0(symbolTable).validateRedirect(), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void openRedirectDeclarationWithInteger() {
        symbolTable.put(OPEN_REDIRECT_KEY.toString(), newRampartList(newRampartInteger(2)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new OpenRedirectValidator2_0(symbolTable).validateRedirect());

        assertThat(thrown.getMessage(), equalTo("\"" + OPEN_REDIRECT_KEY + "\" must be an empty declaration"));
    }

    @Test
    public void openRedirectDeclarationWithConstant() {
        symbolTable.put(OPEN_REDIRECT_KEY.toString(), newRampartList(newRampartConstant("on-redirect")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new OpenRedirectValidator2_0(symbolTable).validateRedirect());

        assertThat(thrown.getMessage(), equalTo("\"" + OPEN_REDIRECT_KEY + "\" must be an empty declaration"));
    }

    @Test
    public void openRedirectDeclarationWithNamedValuePair() {
        symbolTable.put(OPEN_REDIRECT_KEY.toString(), newRampartList(
                newRampartNamedValue(newRampartConstant("status-code"), newRampartInteger(300))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new OpenRedirectValidator2_0(symbolTable).validateRedirect());

        assertThat(thrown.getMessage(), equalTo("\"" + OPEN_REDIRECT_KEY + "\" must be an empty declaration"));
    }
}
