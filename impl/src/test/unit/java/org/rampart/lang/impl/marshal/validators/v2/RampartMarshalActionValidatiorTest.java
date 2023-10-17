package org.rampart.lang.impl.marshal.validators.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.rampart.lang.api.core.RampartActionType.DETECT;
import static org.rampart.lang.api.core.RampartActionType.PROTECT;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartSeverity;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

public class RampartMarshalActionValidatiorTest {

    private Map<String, RampartList> symbolTable;
    private static final RampartString LOG_MESSAGE = newRampartString("log message");
    private RampartMarshalActionValidator2_0 actionValidator;

    @BeforeEach
    public void setUp() {
        symbolTable = new HashMap<>();
        actionValidator = new RampartMarshalActionValidator2_0(symbolTable);
    }

    @Test
    public void validateActionTypeInvalidActionType() {
        assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());
    }

    @Test
    public void validateActionTypeAllow() {
        symbolTable.put(ALLOW_KEY.toString(), RampartList.EMPTY);

        assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());
    }

    @Test
    public void validateActionTypeAllowWithMessageAndSeverity() {
        symbolTable.put(ALLOW_KEY.toString(), newRampartList(
                newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
                newRampartInteger(5)));

        assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());
    }

    @Test
    public void validateActionTypeProtectValidActionType() throws InvalidRampartRuleException {
        symbolTable.put(PROTECT_KEY.toString(), RampartList.EMPTY);
        assertThat(actionValidator.validateRampartAction().getActionType(), equalTo(PROTECT));
    }

    @Test
    public void validateActionTypeDetectValidActionType() throws InvalidRampartRuleException {
        symbolTable.put(DETECT_KEY.toString(), newRampartList(
                newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE)));
        assertThat(actionValidator.validateRampartAction().getActionType(), equalTo(DETECT));
    }

    @Test
    public void missingActionKeysFromRuleDeclaration() {
        symbolTable.put("invalidAction", newRampartList(newRampartInteger(2)));

        assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());
    }

    @Test
    public void protectWithSeverityNumberMatchesType() throws InvalidRampartRuleException {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(2))));
        RampartAction action = actionValidator.validateRampartAction();
        assertThat(action.getSeverity(), equalTo(RampartSeverity.LOW));
    }

    @Test
    public void stringSeverityMapsToInvalidSeverityType() {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(SEVERITY_KEY, newRampartString("low"))));

        assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());
    }

    @Test
    public void invalidStringForSeverity() {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(SEVERITY_KEY, newRampartString("invalid string"))));

        assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

    }

    @Test
    public void severityOutsideValidRangePositive() {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(11))));

        assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());
    }

    @Test
    public void severityOutsideValidRangeNegative() {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(-1))));

        assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());
    }

    @Test
    public void emptyStringLogMessageSetsShouldLog() throws InvalidRampartRuleException {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(MESSAGE_KEY, newRampartString(""))));

        RampartAction action = actionValidator.validateRampartAction();
        assertThat(action.shouldLog(), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void missingLogMessageShouldNotLog() throws InvalidRampartRuleException {
        symbolTable.put(PROTECT_KEY.toString(), RampartList.EMPTY);

        RampartAction action = actionValidator.validateRampartAction();
        assertThat(action.shouldLog(), equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void nonEmptyStringLogMessageSetsShouldLogToTrue() throws InvalidRampartRuleException {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE)));

        RampartAction action = actionValidator.validateRampartAction();
        assertThat(action.shouldLog(), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void invalidActionIntegerParameter() {
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
