package org.rampart.lang.impl.http.validators.v2;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

public class AuthenticateValidatorTest {

    @Test
    public void missingAuthenticateDeclaration() throws InvalidRampartRuleException {
        assertThat(new AuthenticateValidator(null).validateAuthenticate(), equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void emptyAuthenticateDeclaration() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new AuthenticateValidator(RampartList.EMPTY).validateAuthenticate());

        assertThat(thrown.getMessage(),
                equalTo("\"" + AUTHENTICATE_KEY + "\" declaration must be followed by a non empty list"));
    }

    @Test
    public void authenticateDeclarationWithInteger() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new AuthenticateValidator(newRampartList(newRampartInteger(2))).validateAuthenticate());

        assertThat(thrown.getMessage(),
                equalTo("\"" + AUTHENTICATE_KEY + "\" declaration list entries must be constants"));
    }

    @Test
    public void authenticateDeclarationWithUserConstant() throws InvalidRampartRuleException {
        assertThat(new AuthenticateValidator(newRampartList(USER_KEY)).validateAuthenticate(), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void authenticateDeclarationWithInvalidConstant() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new AuthenticateValidator(newRampartList(
                    newRampartConstant("session"))).validateAuthenticate());

        assertThat(thrown.getMessage(), equalTo("constant \"" + USER_KEY + "\" is the only valid parameter to the \""
                + AUTHENTICATE_KEY + "\" declaration"));
    }

    @Test
    public void authenticateDeclarationWithValidAndInvalidConstants() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new AuthenticateValidator(newRampartList(
                    USER_KEY,
                    newRampartConstant("session"))).validateAuthenticate());

        assertThat(thrown.getMessage(), equalTo("constant \"" + USER_KEY + "\" is the only valid parameter to the \""
                + AUTHENTICATE_KEY + "\" declaration"));
    }

    @Test
    public void authenticateDeclarationWithNamedValuePair() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new AuthenticateValidator(newRampartList(
                    newRampartNamedValue(
                        newRampartConstant("userId"),
                        newRampartInteger(300))))
        .validateAuthenticate());

        assertThat(thrown.getMessage(),
                equalTo("\"" + AUTHENTICATE_KEY + "\" declaration list entries must be constants"));
    }
}
