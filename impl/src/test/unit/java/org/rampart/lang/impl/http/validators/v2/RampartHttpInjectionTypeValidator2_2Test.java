package org.rampart.lang.impl.http.validators.v2;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.http.RampartHttpInjectionType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.junit.jupiter.api.Test;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RampartHttpInjectionTypeValidator2_2Test {

    @Test
    public void injectionDeclarationNotMandatory() throws InvalidRampartRuleException {
        new RampartHttpInjectionTypeValidator2_2(null).validateInjection();
    }

    @Test
    public void emptyInjectionDeclaration() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpInjectionTypeValidator2_2(RampartList.EMPTY).validateInjection());

        assertThat(thrown.getMessage(),
                equalTo("\"" + INJECTION_KEY + "\" declaration must be followed by a non empty list"));
    }

    @Test
    public void injectionDeclarationWithHeadersRampartString() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpInjectionTypeValidator2_2(newRampartList(HEADERS_KEY.asRampartString())).validateInjection());

        assertThat(thrown.getMessage(),
                equalTo("\"" + INJECTION_KEY + "\" declaration list entries must be constants"));
    }

    @Test
    public void injectionDeclarationWithHeadersConstant() throws InvalidRampartRuleException {
        RampartHttpInjectionType injectionType =
                new RampartHttpInjectionTypeValidator2_2(newRampartList(HEADERS_KEY)).validateInjection();
        assertThat(injectionType, equalTo(RampartHttpInjectionType.HEADERS));
    }

    @Test
    public void injectionDeclarationWithCookiesConstant() throws InvalidRampartRuleException {
        RampartHttpInjectionType injectionType =
                new RampartHttpInjectionTypeValidator2_2(newRampartList(COOKIES_KEY)).validateInjection();
        assertThat(injectionType, equalTo(RampartHttpInjectionType.COOKIES));
    }

    @Test
    public void injectionDeclarationWithTwoConstants() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpInjectionTypeValidator2_2(newRampartList(HEADERS_KEY, COOKIES_KEY)).validateInjection());

        assertThat(thrown.getMessage(),
                equalTo("only one parameter can be specified to the \"injection\" declaration"));
    }

    @Test
    public void injectionDeclarationWithInvalidConstant() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpInjectionTypeValidator2_2(newRampartList(newRampartConstant("method"))).validateInjection());

        assertThat(thrown.getMessage(), equalTo("unrecognized parameter \"method\" in the \"injection\" declaration"));
    }

    @Test
    public void injectionDeclarationWithNamedValuePair() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpInjectionTypeValidator2_2(newRampartList(
                    newRampartNamedValue(
                            HEADERS_KEY,
                            newRampartList(newRampartString("Content-type")))))
                    .validateInjection());

        assertThat(thrown.getMessage(),
                equalTo("\"" + INJECTION_KEY + "\" declaration list entries must be constants"));
    }
}
