package org.rampart.lang.impl.core.validators.v1;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartSeverity;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

@SuppressWarnings("deprecation")
public class RampartActionValidatorUpTo1_6Test {
    private static final RampartString EMPTY_STRING = newRampartString("");
    private static final RampartString LOG_MESSAGE = newRampartString("logMessage");

    @Test
    public void validationPassesGivenValidInput() throws InvalidRampartRuleException {
        new RampartActionValidatorUpTo1_6(newRampartList(
                newRampartNamedValue(PROTECT_KEY, LOG_MESSAGE),
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(5))))
                .validateRampartAction();
    }

    @Test
    public void missingActionKeyValuePairThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartActionValidatorUpTo1_6(newRampartList(newRampartInteger(2))).validateRampartAction());

        assertThat(thrown.getMessage(), equalTo("action declaration must begin with an action-type:"
                + " \"log message\" key pair"));
    }

    @Test
    public void invalidActionTypeThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartActionValidatorUpTo1_6(newRampartList(
                    newRampartNamedValue(newRampartConstant("invalid type"), LOG_MESSAGE),
                    newRampartInteger(5))).validateRampartAction());

        assertThat(thrown.getMessage(), equalTo("unknown rampart action type specified: invalid type"));

    }

    @Test
    public void nullLogMessageThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartActionValidatorUpTo1_6(newRampartList(
                    newRampartNamedValue(PROTECT_KEY, null),
                    newRampartInteger(5))).validateRampartAction());

        assertThat(thrown.getMessage(), equalTo("action type must supply a log message"));
    }

    @Test
    public void invalidLogMessageTypeThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartActionValidatorUpTo1_6(newRampartList(
                    newRampartNamedValue(PROTECT_KEY, newRampartInteger(2)),
                    newRampartInteger(5))).validateRampartAction());

        assertThat(thrown.getMessage(), equalTo("action type must supply a log message"));
    }

    @Test
    public void noSeveritySpecifiedIsValid() throws InvalidRampartRuleException {
        new RampartActionValidatorUpTo1_6(newRampartList(newRampartNamedValue(PROTECT_KEY, LOG_MESSAGE)))
                .validateRampartAction();
    }

    @Test
    public void stringSeverityMapsToCorrectSeverityType() throws InvalidRampartRuleException {
        RampartAction validatedAction = new RampartActionValidatorUpTo1_6(newRampartList(
                newRampartNamedValue(PROTECT_KEY, LOG_MESSAGE),
                newRampartNamedValue(SEVERITY_KEY, newRampartString("low"))))
                .validateRampartAction();
        assertThat(validatedAction.getSeverity(), equalTo(RampartSeverity.LOW));
    }

    @Test
    public void invalidStringMapsToUnknownSeverity() throws InvalidRampartRuleException {
        RampartAction validatedAction = new RampartActionValidatorUpTo1_6(newRampartList(
                newRampartNamedValue(PROTECT_KEY, LOG_MESSAGE),
                newRampartNamedValue(SEVERITY_KEY, newRampartString("invalid string"))))
                .validateRampartAction();
        assertThat(validatedAction.getSeverity(), equalTo(RampartSeverity.UNKNOWN));
    }

    @Test
    public void severityOutsideValidRangePositiveThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartActionValidatorUpTo1_6(newRampartList(
                    newRampartNamedValue(PROTECT_KEY, LOG_MESSAGE),
                    newRampartNamedValue(SEVERITY_KEY, newRampartInteger(11))))
                    .validateRampartAction());

        assertThat(thrown.getMessage(), equalTo("severity must be in the range of 0-10 (inclusive)"));
    }

    @Test
    public void severityOutsideValidRangeNegativeThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartActionValidatorUpTo1_6(newRampartList(
                    newRampartNamedValue(PROTECT_KEY, LOG_MESSAGE),
                    newRampartNamedValue(SEVERITY_KEY, newRampartInteger(-1))))
                    .validateRampartAction());

        assertThat(thrown.getMessage(), equalTo("severity must be in the range of 0-10 (inclusive)"));
    }

    @Test
    public void validIntegerValueMapsToSeverityCorrectly() throws InvalidRampartRuleException {
        RampartAction validatedAction = new RampartActionValidatorUpTo1_6(newRampartList(
                newRampartNamedValue(PROTECT_KEY, LOG_MESSAGE),
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(1))))
                .validateRampartAction();
        assertThat(validatedAction.getSeverity(), equalTo(RampartSeverity.LOW));
    }

    @Test
    public void emptyStringLogMessageSetsShouldLogToFalse() throws InvalidRampartRuleException {
        RampartAction emptyLogMsgAction = new RampartActionValidatorUpTo1_6(newRampartList(
                newRampartNamedValue(ALLOW_KEY, EMPTY_STRING),
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(1))))
                .validateRampartAction();
        assertThat(emptyLogMsgAction.shouldLog(), equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void nonEmptyStringLogMessageSetsShouldLogToTrue() throws InvalidRampartRuleException {
        RampartAction logMsgAction = new RampartActionValidatorUpTo1_6(newRampartList(
                newRampartNamedValue(ALLOW_KEY, LOG_MESSAGE),
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(1))))
                .validateRampartAction();
        assertThat(logMsgAction.shouldLog(), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void invalidSeverityTypeReturnsUnknownSeverity() throws InvalidRampartRuleException {
        RampartAction action = new RampartActionValidatorUpTo1_6(newRampartList(
                newRampartNamedValue(PROTECT_KEY, LOG_MESSAGE),
                newRampartNamedValue(SEVERITY_KEY, newRampartConstant("Low"))))
                .validateRampartAction();
        assertThat(action.getSeverity(), equalTo(RampartSeverity.UNKNOWN));
    }

}
