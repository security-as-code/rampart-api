package org.rampart.lang.impl.core.validators.v2;

import static org.rampart.lang.api.core.RampartActionType.ALLOW;
import static org.rampart.lang.api.core.RampartActionType.DETECT;
import static org.rampart.lang.api.core.RampartActionType.PROTECT;
import static org.rampart.lang.api.constants.RampartGeneralConstants.*;

import static org.rampart.lang.java.RampartPrimitives.*;
import static org.mockito.Mockito.spy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.api.core.RampartSeverity;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

public class RampartActionValidator2_0PlusTest {

    private Map<String, RampartList> symbolTable;
    private static final RampartString EMPTY_MESSAGE = newRampartString("");
    private static final RampartString LOG_MESSAGE = newRampartString("log message");
    private RampartActionValidator2_0PlusTester actionValidator;

    @BeforeEach
    public void setUp() {
        symbolTable = new HashMap<>();
        actionValidator = spy(new RampartActionValidator2_0PlusTester(symbolTable));
    }

    @Test
    public void missingAction() {
        assertThrows(InvalidRampartRuleException.class, () -> actionValidator.validateRampartAction());
    }

    @Test
    public void loggingOffForAllowAction() throws InvalidRampartRuleException {
        symbolTable.put(ALLOW_KEY.toString(), RampartList.EMPTY);
        assertThat(actionValidator.validateRampartAction().getActionType(), equalTo(ALLOW));
    }

    @Test
    public void loggingOffForProtectAction() throws InvalidRampartRuleException {
        symbolTable.put(PROTECT_KEY.toString(), RampartList.EMPTY);
        assertThat(actionValidator.validateRampartAction().getActionType(), equalTo(PROTECT));
    }

    @Test
    public void detectActionWithLogMessage() throws InvalidRampartRuleException {
        symbolTable.put(DETECT_KEY.toString(), newRampartList(
                newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE)));
        assertThat(actionValidator.validateRampartAction().getActionType(), equalTo(DETECT));
    }

    @Test
    public void detectActionLoggingOffThrowsException() {
        symbolTable.put(DETECT_KEY.toString(), RampartList.EMPTY);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(), equalTo("\"detect\" action must declare message"));
    }

    @Test
    public void missingActionKeysFromRuleDeclaration() {
        symbolTable.put("invalidAction", newRampartList(newRampartInteger(2)));

        assertThrows(InvalidRampartRuleException.class, () -> actionValidator.validateRampartAction());
    }

    @Test
    public void defaultSeverityIsUnknown() throws InvalidRampartRuleException {
        symbolTable.put(PROTECT_KEY.toString(), RampartList.EMPTY);
        RampartAction action = actionValidator.validateRampartAction();
        assertThat(action.getSeverity(), equalTo(RampartSeverity.UNKNOWN));
    }

    @Test
    public void loggingOnWithNoMessage() throws InvalidRampartRuleException {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(MESSAGE_KEY, EMPTY_MESSAGE)));
        RampartAction action = actionValidator.validateRampartAction();

        assertThat(action.getLogMessage(), equalTo(EMPTY_MESSAGE));
        assertThat(action.shouldLog(), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void loggingOnWithMessage() throws InvalidRampartRuleException {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE)));
        RampartAction action = actionValidator.validateRampartAction();

        assertThat(action.getLogMessage(), equalTo(LOG_MESSAGE));
        assertThat(action.shouldLog(), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void loggingOffShouldNotLog() throws InvalidRampartRuleException {
        symbolTable.put(PROTECT_KEY.toString(), RampartList.EMPTY);

        RampartAction action = actionValidator.validateRampartAction();
        assertThat(action.shouldLog(), equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void invalidLogMessageFormat() {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(MESSAGE_KEY, newRampartInteger(1))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(), equalTo("value for the message must be a string literal"));
    }

    @Test
    public void invalidSeverityType() {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
                newRampartNamedValue(SEVERITY_KEY, newRampartString("Low"))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(), equalTo("\"severity\" is malformed, must be an integer or a constant"));
    }

    @Test
    public void severityWithinIntegerBounds() throws InvalidRampartRuleException {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(5))));
        RampartAction action = actionValidator.validateRampartAction();

        assertThat(action.getSeverity(), equalTo(RampartSeverity.MEDIUM));
    }

    @Test
    public void severityOutsideIntegerLowerBounds() {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(-1))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(), equalTo("\"severity\" must be in the range of 0-10 (inclusive)"));
    }

    @Test
    public void severityOutsideIntegerUpperBounds() {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(11))));

        assertThrows(InvalidRampartRuleException.class, () -> actionValidator.validateRampartAction());
    }

    @Test
    public void severityWithinSetKeywords() throws InvalidRampartRuleException {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(SEVERITY_KEY, newRampartConstant("low"))));
        RampartAction action = actionValidator.validateRampartAction();

        assertThat(action.getSeverity(), equalTo(RampartSeverity.LOW));
    }

    @Test
    public void undefinedSeverity() throws InvalidRampartRuleException {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(SEVERITY_KEY, newRampartConstant("undefined"))));
        RampartAction action = actionValidator.validateRampartAction();

        assertThat(action.getSeverity(), equalTo(RampartSeverity.UNKNOWN));
    }

    private static class RampartActionValidator2_0PlusTester extends RampartActionValidator2_0Plus {
        public RampartActionValidator2_0PlusTester(Map<String, RampartList> visitorSymbolTable) {
            super(visitorSymbolTable, RampartRuleType.UNKNOWN);
        }

        @Override
        public List<RampartConstant> allowedKeys() {
            return Arrays.asList(PROTECT_KEY, ALLOW_KEY, DETECT_KEY, CORRECT_KEY);
        }
    }

    @Test
    public void invalidActionAttribute() {
        symbolTable.put(PROTECT_KEY.toString(),
                newRampartList(
                        newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
                        newRampartString("mistake")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(), equalTo("parameter \"mistake\" to the action \"protect\" is not supported"));
    }
}
