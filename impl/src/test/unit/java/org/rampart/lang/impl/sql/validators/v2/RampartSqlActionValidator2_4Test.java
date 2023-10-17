package org.rampart.lang.impl.sql.validators.v2;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartNamedValue;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.*;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import matchers.RampartListMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.rampart.lang.api.core.RampartActionType.*;
import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

public class RampartSqlActionValidator2_4Test extends RampartSqlActionValidator2_3Test {
    private static final RampartString LOG_MESSAGE = newRampartString("log message");

    @BeforeEach
    public void setUp() {
        super.setUp();
        actionValidator = new RampartSqlActionValidator2_4(symbolTable);
    }

    @Test
    public void validateActionTypeInvalidActionType() {
        assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());
    }

    @Test
    public void validateActionTypeAllowValidActionType() {
        assertDoesNotThrow(() -> {
            symbolTable.put(ALLOW_KEY.toString(), RampartList.EMPTY);
            assertThat(actionValidator.validateRampartAction().getActionType(), equalTo(ALLOW));
        });
    }

    @Test
    public void validateActionTypeProtectValidActionType() {
        assertDoesNotThrow(() -> {
            symbolTable.put(PROTECT_KEY.toString(), RampartList.EMPTY);
            assertThat(actionValidator.validateRampartAction().getActionType(), equalTo(PROTECT));
        });
    }

    @Test
    public void validateActionTypeDetectValidActionType() {
        assertDoesNotThrow(() -> {
            symbolTable.put(DETECT_KEY.toString(), newRampartList(newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE)));
            assertThat(actionValidator.validateRampartAction().getActionType(), equalTo(DETECT));
        });
    }

    @Test
    public void missingActionKeysFromRuleDeclaration() {
        symbolTable.put("invalidAction", newRampartList(newRampartInteger(2)));

        assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());
    }

    @Test
    public void protectWithLogMessage() {
        assertDoesNotThrow(() -> {
            symbolTable.put(PROTECT_KEY.toString(),
                    newRampartList(newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE)));
            actionValidator.validateRampartAction();
        });
    }

    @Test
    public void protectWithSeverityNumberMatchesType() {
        assertDoesNotThrow(() -> {
            symbolTable.put(PROTECT_KEY.toString(), newRampartList(newRampartNamedValue(SEVERITY_KEY, newRampartInteger(2))));
            RampartAction action = actionValidator.validateRampartAction();
            assertThat(action.getSeverity(), equalTo(RampartSeverity.LOW));
        });
    }

    @Test
    public void stringSeverityMapsToInvalidSeverityType() {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(newRampartNamedValue(SEVERITY_KEY, newRampartString("low"))));

        assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());
    }

    @Test
    public void invalidStringForSeverity() {
        symbolTable.put(PROTECT_KEY.toString(),
                newRampartList(newRampartNamedValue(SEVERITY_KEY, newRampartString("invalid string"))));

        assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());
    }

    @Test
    public void severityOutsideValidRangePositive() {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(newRampartNamedValue(SEVERITY_KEY, newRampartInteger(11))));

        assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());
    }

    @Test
    public void severityOutsideValidRangeNegative() {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(newRampartNamedValue(SEVERITY_KEY, newRampartInteger(-1))));

        assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());
    }

    @Test
    public void emptyStringLogMessageSetsShouldLog() {
        assertDoesNotThrow(() -> {
            symbolTable.put(PROTECT_KEY.toString(), newRampartList(newRampartNamedValue(MESSAGE_KEY, newRampartString(""))));

            RampartAction action = actionValidator.validateRampartAction();
            assertThat(action.shouldLog(), equalTo(RampartBoolean.TRUE));
        });
    }

    @Test
    public void missingLogMessageShouldNotLog() {
        assertDoesNotThrow(() -> {
            symbolTable.put(PROTECT_KEY.toString(), RampartList.EMPTY);

            RampartAction action = actionValidator.validateRampartAction();
            assertThat(action.shouldLog(), equalTo(RampartBoolean.FALSE));
        });
    }

    @Test
    public void nonEmptyStringLogMessageSetsShouldLogToTrue() {
        assertDoesNotThrow(() -> {
            symbolTable.put(PROTECT_KEY.toString(), newRampartList(newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE)));

            RampartAction action = actionValidator.validateRampartAction();
            assertThat(action.shouldLog(), equalTo(RampartBoolean.TRUE));
        });
    }

    @Test
    public void protectActionHasTargetWithSendError400Attribute() {
        RampartNamedValue sendErrorAttribute = newRampartNamedValue(NEW_RESPONSE_KEY, newRampartInteger(400));
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(HTTP_RESPONSE_KEY, newRampartList(sendErrorAttribute))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(),
                equalTo("attribute \"" + NEW_RESPONSE_KEY + "\" must have a list of values"));
    }

    @Test
    public void protectActionHasTargetWithSendErrorAttributeCode400() {
        RampartNamedValue statusCodeKeyValue = newRampartNamedValue(CODE_KEY, newRampartInteger(400));
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(HTTP_RESPONSE_KEY, newRampartList(
                        newRampartNamedValue(NEW_RESPONSE_KEY, newRampartList(statusCodeKeyValue))))));
        assertDoesNotThrow(() -> {
            RampartActionWithAttribute action = (RampartActionWithAttribute) actionValidator.validateRampartAction();
            assertAll(() -> {
                assertThat(action.getAttribute(), equalTo(RampartActionAttribute.NEW_RESPONSE));
                assertThat(action.getTarget(), equalTo(RampartActionTarget.HTTP_RESPONSE));
                assertThat(action.getConfigMap(), RampartListMatcher.containsInAnyOrder(statusCodeKeyValue));
            });
        });
    }

    @Test
    public void protectActionHasTargetWithSendErrorAttributeUnsupportedCode() {
        RampartNamedValue statusCodeKeyValue = newRampartNamedValue(CODE_KEY, newRampartInteger(302));
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(HTTP_RESPONSE_KEY, newRampartList(
                        newRampartNamedValue(NEW_RESPONSE_KEY, newRampartList(statusCodeKeyValue))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(),
                equalTo("\"302\" is not a supported HTTP status code"));
    }

    @Test
    public void protectActionHasTargetWithSetHeaderAttribute() {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(HTTP_RESPONSE_KEY, newRampartList(
                        newRampartNamedValue(SET_HEADER_KEY, newRampartList(
                                newRampartNamedValue(newRampartConstant("foo"), newRampartString("bar"))))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () ->actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(), equalTo("unsupported attribute \"set-header\""));
    }

    @Test
    public void protectActionIsInvalidWhenHasHttpSessionTarget() {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(HTTP_SESSION_KEY, REGENERATE_ID_KEY)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(), equalTo(
                "parameter \"http-session: regenerate-id\" to the action \"protect\" is not supported"));
    }

    @Test
    public void allowActionHasTargetWithAttributes() {
        RampartNamedValue statusCodeKeyValue = newRampartNamedValue(CODE_KEY, newRampartInteger(400));
        symbolTable.put(ALLOW.toString(), newRampartList(
                newRampartNamedValue(HTTP_RESPONSE_KEY, newRampartList(
                        newRampartNamedValue(NEW_RESPONSE_KEY, newRampartList(statusCodeKeyValue))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(), equalTo("action type \"allow\" does not support targets and attributes"));
    }

    @Test
    public void protectActionHasTargetWithDuplicatedSendErrorAttributeCode400() {
        RampartNamedValue statusCodeKeyValue = newRampartNamedValue(CODE_KEY, newRampartInteger(400));
        symbolTable.put(PROTECT.toString(), newRampartList(
                newRampartNamedValue(HTTP_RESPONSE_KEY, newRampartList(
                        newRampartNamedValue(NEW_RESPONSE_KEY, newRampartList(statusCodeKeyValue)),
                        newRampartNamedValue(NEW_RESPONSE_KEY, newRampartList(statusCodeKeyValue))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(),
                endsWith("of action target \"http-response\" must contain a single value"));
    }

    @Test
    public void protectActionHasTargetWithSendErrorAttributeDuplicatedCode400() {
        RampartNamedValue statusCodeKeyValue = newRampartNamedValue(CODE_KEY, newRampartInteger(400));
        symbolTable.put(PROTECT.toString(), newRampartList(
                newRampartNamedValue(HTTP_RESPONSE_KEY, newRampartList(
                        newRampartNamedValue(NEW_RESPONSE_KEY, newRampartList(statusCodeKeyValue, statusCodeKeyValue))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(),
                equalTo("only a single setting must be defined for the \"" + NEW_RESPONSE_KEY + "\" attribute"));
    }

    @Test
    public void protectActionHasTargetWithSendErrorAttributeRegenerateSessionIdSetting() {
        symbolTable.put(PROTECT.toString(), newRampartList(
                newRampartNamedValue(HTTP_RESPONSE_KEY, newRampartList(
                        newRampartNamedValue(NEW_RESPONSE_KEY, newRampartList(
                                REGENERATE_ID_KEY))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(),
                equalTo("only the \"code\" setting is supported for the attribute \"" + NEW_RESPONSE_KEY + "\""));
    }

    @Test
    public void protectActionHasTargetWithSingleAttributeSendError() {
        RampartNamedValue statusCodeKeyValue = newRampartNamedValue(CODE_KEY, newRampartInteger(400));
        symbolTable.put(PROTECT.toString(), newRampartList(
                newRampartNamedValue(HTTP_RESPONSE_KEY,
                        newRampartNamedValue(NEW_RESPONSE_KEY, newRampartList(statusCodeKeyValue)))));

        assertDoesNotThrow(() -> {
            RampartActionWithAttribute action = (RampartActionWithAttribute) actionValidator.validateRampartAction();
            assertAll(() -> {
                assertThat(action.getAttribute(), equalTo(RampartActionAttribute.NEW_RESPONSE));
                assertThat(action.getTarget(), equalTo(RampartActionTarget.HTTP_RESPONSE));
                assertThat(action.getConfigMap(), RampartListMatcher.containsInAnyOrder(statusCodeKeyValue));
            });
        });
    }

    @Test
    public void protectActionDuplicateTarget() {
        RampartNamedValue statusCodeKeyValue = newRampartNamedValue(CODE_KEY, newRampartInteger(400));
        symbolTable.put(PROTECT.toString(), newRampartList(
                newRampartNamedValue(HTTP_RESPONSE_KEY, newRampartList(
                        newRampartNamedValue(NEW_RESPONSE_KEY, newRampartList(statusCodeKeyValue)))),
                newRampartNamedValue(HTTP_RESPONSE_KEY, newRampartList(
                        newRampartNamedValue(NEW_RESPONSE_KEY, newRampartList(statusCodeKeyValue))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(), equalTo(
                "invalid specified target \"http-response\". Only a single target is allowed for \"protect\" declaration"));
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
