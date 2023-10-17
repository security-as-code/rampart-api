package org.rampart.lang.impl.sanitization.validators.v2;

import static org.rampart.lang.api.constants.RampartSanitizationConstants.ATTRIBUTE_KEY;
import static org.rampart.lang.api.constants.RampartSanitizationConstants.IGNORE_KEY;
import static org.rampart.lang.api.constants.RampartSanitizationConstants.PAYLOAD_KEY;
import static org.rampart.lang.api.constants.RampartSanitizationConstants.URI_KEY;
import static org.rampart.lang.java.RampartPrimitives.newRampartNamedValue;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class RampartIgnoreValidator2_3Test {

    private static final RampartString URI = newRampartString("/api/test");

    private static final RampartList PAYLOADS = newRampartList(
            newRampartString("*/*;q=0.8"),
            newRampartString("application/xml;q=0.9")
    );

    private static final RampartList ATTRIBUTES = newRampartList(
            newRampartString("email"),
            newRampartString("username")
    );

    private HashMap<String, RampartList> symbolTable;
    private RampartIgnoreValidator2_3 ignoreValidator;

    @BeforeEach
    public void setup() {
        symbolTable = new HashMap<>();
        ignoreValidator = new RampartIgnoreValidator2_3(symbolTable);
    }

    @Test
    public void successful_fullConfiguration() throws InvalidRampartRuleException {
        symbolTable.put(IGNORE_KEY.toString(), newRampartList(
                newRampartNamedValue(PAYLOAD_KEY, PAYLOADS),
                newRampartNamedValue(ATTRIBUTE_KEY, ATTRIBUTES)
        ));

        ignoreValidator.validate();
    }

    @Test
    public void successful_validUriAndPayload() throws InvalidRampartRuleException {
        symbolTable.put(IGNORE_KEY.toString(), newRampartList(
                newRampartNamedValue(URI_KEY, URI),
                newRampartNamedValue(PAYLOAD_KEY, PAYLOADS)
        ));

        ignoreValidator.validate();
    }

    @Test
    public void successful_validPayloadOnly() throws InvalidRampartRuleException {
        symbolTable.put(IGNORE_KEY.toString(), newRampartList(
                newRampartNamedValue(PAYLOAD_KEY, PAYLOADS)
        ));

        ignoreValidator.validate();
    }

    @Test
    public void successful_validUriAndAttribute() throws InvalidRampartRuleException {
        symbolTable.put(IGNORE_KEY.toString(), newRampartList(
                newRampartNamedValue(URI_KEY, URI),
                newRampartNamedValue(ATTRIBUTE_KEY, ATTRIBUTES)
        ));

        ignoreValidator.validate();
    }

    @Test
    public void successful_validAttributeOnly() throws InvalidRampartRuleException {
        symbolTable.put(IGNORE_KEY.toString(), newRampartList(
                newRampartNamedValue(ATTRIBUTE_KEY, ATTRIBUTES)
        ));

        ignoreValidator.validate();
    }

    @Test
    public void ThrowsException_invalidListForIgnoreDirective() {
        symbolTable.put(IGNORE_KEY.toString(), newRampartList(newRampartString("invalid")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> ignoreValidator.validate());

        assertThat(thrown.getMessage(), equalTo("Invalid Sanitization rule configuration. Please ensure at least one of 'payload', or 'attribute' is configured correctly."));
    }

    @Test
    public void ThrowsException_incorrectObjectForDirective() {
        symbolTable.put(IGNORE_KEY.toString(), newRampartList(newRampartString("this object should be an RampartList")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> ignoreValidator.validate());

        assertThat(thrown.getMessage(), equalTo("Invalid Sanitization rule configuration. Please ensure at least one of 'payload', or 'attribute' is configured correctly."));
    }

    @Test
    public void ThrowsException_invalidRampartListForDirective() {
        symbolTable.put(IGNORE_KEY.toString(), newRampartList(
                newRampartNamedValue(URI_KEY, URI),
                null,
                null
        ));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> ignoreValidator.validate());

        assertThat(thrown.getMessage(), equalTo(
                "Invalid Sanitization rule configuration."
                + " Too many parameters. Only expecting a maximum of two"
                + " parameters for payload and/or attribute"));
    }

    @Test
    public void ThrowsException_invalidRampartListForDirectiveNoURI() {
        symbolTable.put(IGNORE_KEY.toString(), newRampartList(
                null, null
        ));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> ignoreValidator.validate());

        assertThat(thrown.getMessage(), equalTo(
                "Invalid Sanitization rule configuration. " +
                "Please ensure at least one of " +
                "'payload', or 'attribute' is configured correctly."));
    }

    @Test
    public void ThrowsException_invalidPayload() {
        symbolTable.put(IGNORE_KEY.toString(), newRampartList(
                newRampartNamedValue(PAYLOAD_KEY, null)
        ));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> ignoreValidator.validate());

        assertThat(thrown.getMessage(), equalTo("\"payload\" declaration in the sanitize rule must be a list of string values."));
    }

    @Test
    public void ThrowsException_invalidAttribute() {
        symbolTable.put(IGNORE_KEY.toString(), newRampartList(
                newRampartNamedValue(ATTRIBUTE_KEY, null)
        ));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> ignoreValidator.validate());

        assertThat(thrown.getMessage(), equalTo("\"attribute\" declaration in the sanitize rule must be a list of string values."));
    }

}

