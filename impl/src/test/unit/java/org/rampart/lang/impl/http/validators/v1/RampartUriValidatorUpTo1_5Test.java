package org.rampart.lang.impl.http.validators.v1;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.rampart.lang.api.http.RampartHttpIOType.REQUEST;

import org.rampart.lang.api.RampartInteger;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("deprecation")
public class RampartUriValidatorUpTo1_5Test {
    private Map<String, RampartList> symbolTable;

    @BeforeEach
    public void setup() {
        symbolTable = new LinkedHashMap<>();
    }

    @Test
    public void validUriValueIsValidatedSuccessfully() throws InvalidRampartRuleException {
        symbolTable.put(REQUEST_KEY.toString(), newRampartList(newRampartString("/webapp/index.jsp")));
        new RampartUriValidatorUpTo1_5(symbolTable).validateUriValues(REQUEST);
    }

    @Test
    public void namedUriIsValidatedSuccessfully() throws InvalidRampartRuleException {
        symbolTable.put(REQUEST_KEY.toString(), newRampartList(newRampartNamedValue(
                URI_KEY, newRampartList(newRampartString("/webapp/index.jsp")))));
        new RampartUriValidatorUpTo1_5(symbolTable).validateUriValues(REQUEST);
    }

    @Test
    public void noUriIsValid() throws InvalidRampartRuleException {
        symbolTable.put(REQUEST_KEY.toString(), RampartList.EMPTY);
        new RampartUriValidatorUpTo1_5(symbolTable).validateUriValues(REQUEST);
    }

    @Test
    public void invalidUriTypeThrowsException() {
        RampartInteger invalidUriType = newRampartInteger(1);
        symbolTable.put(REQUEST_KEY.toString(), newRampartList(newRampartNamedValue(
                URI_KEY, newRampartList(newRampartString("/webapp/index.jsp"), invalidUriType))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartUriValidatorUpTo1_5(symbolTable).validateUriValues(REQUEST));

        assertThat(thrown.getMessage(), equalTo("uri value: \"" + invalidUriType + "\" must be a quoted string value"));
    }

    @Test
    public void absoluteUriThrowsException() {
        symbolTable.put(REQUEST_KEY.toString(), newRampartList(newRampartString("https://localhost:8080/webapp/index.jsp")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartUriValidatorUpTo1_5(symbolTable).validateUriValues(REQUEST));

        assertThat(thrown.getMessage(),
                equalTo("\"https://localhost:8080/webapp/index.jsp\" is not a valid relative URI"));
    }

    @Test
    public void emptyUriThrowsException() {
        symbolTable.put(REQUEST_KEY.toString(), newRampartList(newRampartString("")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartUriValidatorUpTo1_5(symbolTable).validateUriValues(REQUEST));

        assertThat(thrown.getMessage(), equalTo("\"\" is not a valid relative URI"));
    }

    @Test
    public void emptyUriNamedValueListThrowsException() {
        symbolTable.put(REQUEST_KEY.toString(), newRampartList(newRampartNamedValue(URI_KEY, RampartList.EMPTY)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartUriValidatorUpTo1_5(symbolTable).validateUriValues(REQUEST));

        assertThat(thrown.getMessage(), equalTo("named uri list cannot be empty"));
    }

    @Test
    public void invalidNamedValueTypeThrowsException() {
        symbolTable.put(REQUEST_KEY.toString(), newRampartList(newRampartNamedValue(URI_KEY, newRampartInteger(3))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartUriValidatorUpTo1_5(symbolTable).validateUriValues(REQUEST));

        assertThat(thrown.getMessage(), equalTo("named uri declaration must be followed by a list of relative URIs"));
    }

    @Test
    public void invalidUriContainingIllegalCharactersThrowsException() {
        String invalidUri = "/asdfas$?/path";
        symbolTable.put(REQUEST_KEY.toString(), newRampartList(newRampartString(invalidUri)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartUriValidatorUpTo1_5(symbolTable).validateUriValues(REQUEST));

        assertThat(thrown.getMessage(), equalTo("\"" + invalidUri + "\" is not a valid relative URI"));
    }
}
