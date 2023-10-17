package org.rampart.lang.impl.sanitization.validators.v2;

import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;

import java.util.HashMap;
import java.util.Map;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RampartSanitizationActionValidatorTest {

    private Map<String, RampartList> symbolTable;
    private static final RampartString LOG_MESSAGE = newRampartString("log message");
    private RampartSanitizationActionValidator actionValidator;

    @BeforeEach
    public void setUp() {
        symbolTable = new HashMap<>();
        actionValidator = new RampartSanitizationActionValidator(symbolTable);
    }

    @Test
    public void detectActionValidatedSuccessfully() throws InvalidRampartRuleException {
        symbolTable.put(DETECT_KEY.toString(), newRampartList(
                newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(5))));
        actionValidator.validateRampartAction();
    }

    @Test
    public void protectActionValidatedSuccessfully() throws InvalidRampartRuleException {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(5))));

        actionValidator.validateRampartAction();
    }

    @Test
    public void allowActionThrowsException() {
        symbolTable.put(ALLOW_KEY.toString(), newRampartList(
                newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(5))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(),
                   equalTo("RAMPART \"sanitization\" action is missing. Must be one of: " + actionValidator.allowedKeys()));
    }

    @Test
    public void invalidActionThrowsException() {
        symbolTable.put("invalidAction", newRampartList(
                newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(5))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(),
                   equalTo("RAMPART \"sanitization\" action is missing. Must be one of: " + actionValidator.allowedKeys()));
    }

    @Test
    public void multipleValidActionsThrowsException() {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(5))));
        symbolTable.put(DETECT_KEY.toString(), newRampartList());

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(), equalTo(
                "actions \"detect\" and \"protect\" are declared. Declaration of more than one action type is not allowed."));
    }

    @Test
    public void validActionAndInvalidActionSuccessful() throws InvalidRampartRuleException {
        symbolTable.put("invalidAction", newRampartList());
        symbolTable.put(DETECT_KEY.toString(), newRampartList(
                newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(5))));
        actionValidator.validateRampartAction();
    }

    @Test
    public void notSupportedActionThrowsException() {
        symbolTable.put(CORRECT_KEY.toString(), newRampartList(
                newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(5))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(),
                   equalTo("RAMPART \"sanitization\" action is missing. Must be one of: " + actionValidator.allowedKeys()));
    }

    @Test
    public void invalidActionNumberParameter() {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
                newRampartInteger(5)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(), equalTo("parameter \"5\" to the action \"protect\" is not supported"));
    }

    @Test
    public void invalidActionNamedValueParameter() {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
                newRampartNamedValue(newRampartConstant("make"), newRampartString("coffee"))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(), equalTo("parameter \"make: \"coffee\"\" to the action \"protect\" is not supported"));
    }

}
