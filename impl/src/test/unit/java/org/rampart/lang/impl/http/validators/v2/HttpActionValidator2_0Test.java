package org.rampart.lang.impl.http.validators.v2;

import static org.rampart.lang.api.core.RampartActionType.ALLOW;
import static org.rampart.lang.api.core.RampartActionType.DETECT;
import static org.rampart.lang.api.core.RampartActionType.PROTECT;
import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.HashMap;
import java.util.Map;

import org.rampart.lang.api.RampartNamedValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartActionAttribute;
import org.rampart.lang.api.core.RampartActionTarget;
import org.rampart.lang.api.core.RampartActionWithAttribute;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import matchers.RampartListMatcher;

public class HttpActionValidator2_0Test {

    private Map<String, RampartList> symbolTable;
    private static final RampartString LOG_MESSAGE = newRampartString("log message");
    private HttpActionValidator2_0 actionValidator;

    @BeforeEach
    public void setUp() {
        symbolTable = new HashMap<>();
        actionValidator = new HttpActionValidator2_0(symbolTable);
    }

    @Test
    public void validateActionTypeAllowValidActionType() throws InvalidRampartRuleException {
        symbolTable.put(ALLOW_KEY.toString(), RampartList.EMPTY);
        assertThat(actionValidator.validateRampartAction().getActionType(), equalTo(ALLOW));
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
    public void correctActionNotSupported() {
        symbolTable.put(CORRECT_KEY.toString(), newRampartList(
                newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(),
                equalTo("RAMPART \"http\" action is missing. Must be one of: [allow, detect, protect]"));
    }

    @Test
    public void protectActionHasResponseTargetWithRegenerateIdAttribute() {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(HTTP_RESPONSE_KEY, REGENERATE_ID_KEY)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(),
                equalTo("unsupported attribute \"regenerate-id\" for action target \"http-response\""));
    }

    @Test
    public void protectActionHasTargetWithSetHeaderAttributeSingleHeader() throws InvalidRampartRuleException {
        RampartNamedValue headerToSet = newRampartNamedValue(newRampartConstant("foo"), newRampartString("bar"));
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(HTTP_RESPONSE_KEY, newRampartList(
                        newRampartNamedValue(SET_HEADER_KEY, newRampartList(headerToSet))))));
        RampartActionWithAttribute action = (RampartActionWithAttribute) actionValidator.validateRampartAction();
        assertAll(() -> {
            assertThat(action.getAttribute(), equalTo(RampartActionAttribute.SET_HEADER));
            assertThat(action.getTarget(), equalTo(RampartActionTarget.HTTP_RESPONSE));
            assertThat(action.getConfigMap(), RampartListMatcher.containsInAnyOrder(headerToSet));
        });
    }

    @Test
    public void protectActionHasTargetWithSetHeaderAttributeMultipleHeaders() throws InvalidRampartRuleException {
        RampartNamedValue header1 = newRampartNamedValue(newRampartConstant("headername1"), newRampartString("headervalue1"));
        RampartNamedValue header2 = newRampartNamedValue(newRampartConstant("headername2"), newRampartString("headervalue2"));
        RampartNamedValue header3 = newRampartNamedValue(newRampartConstant("headername3"), newRampartString("headervalue3"));

        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(HTTP_RESPONSE_KEY, newRampartList(
                        newRampartNamedValue(SET_HEADER_KEY, newRampartList(header1, header2, header3))))));
        RampartActionWithAttribute action = (RampartActionWithAttribute) actionValidator.validateRampartAction();
        assertAll(() -> {
            assertThat(action.getAttribute(), equalTo(RampartActionAttribute.SET_HEADER));
            assertThat(action.getTarget(), equalTo(RampartActionTarget.HTTP_RESPONSE));
            assertThat(action.getConfigMap(), RampartListMatcher.containsInAnyOrder(header1, header2, header3));
        });
    }

    @Test
    public void protectActionHasTargetWithSetHeaderAttributeInvalidHeaderType() {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(HTTP_RESPONSE_KEY, newRampartList(
                        newRampartNamedValue(SET_HEADER_KEY, newRampartList(
                                newRampartNamedValue(newRampartConstant("foo"), newRampartConstant("bar"))))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(),
                equalTo("\"set-header\" attribute must contain name value pairs with string literals as values"));
    }

    @Test
    public void allowActionHasTargetWithAttributes() {
        symbolTable.put(ALLOW_KEY.toString(), newRampartList(
                newRampartNamedValue(HTTP_SESSION_KEY, REGENERATE_ID_KEY)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(), equalTo("action type \"allow\" does not support targets and attributes"));
    }

    @Test
    public void detectActionHasTargetWithAttributes() {
        RampartNamedValue headerToSet = newRampartNamedValue(newRampartConstant("foo"), newRampartString("bar"));
        symbolTable.put(DETECT_KEY.toString(), newRampartList(
                newRampartNamedValue(HTTP_RESPONSE_KEY, newRampartList(
                        newRampartNamedValue(SET_HEADER_KEY, newRampartList(headerToSet)))),
                newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(), equalTo("action type \"detect\" does not support targets and attributes"));
    }

    @Test
    public void protectActionHasTargetWithSetHeaderAttributeValuesMustBeList() {
        RampartNamedValue headerToSet = newRampartNamedValue(newRampartConstant("foo"), newRampartString("bar"));
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(HTTP_RESPONSE_KEY, newRampartList(
                        newRampartNamedValue(SET_HEADER_KEY, headerToSet)))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(), equalTo("attribute \"set-header\" must have a list of values"));
    }

    @Test
    public void protectActionHasTargetWithSetHeaderAttributeAndRegenerateId() {
        RampartNamedValue headerToSet = newRampartNamedValue(newRampartConstant("foo"), newRampartString("bar"));
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(HTTP_RESPONSE_KEY, newRampartList(
                        newRampartNamedValue(SET_HEADER_KEY, newRampartList(headerToSet)),
                        REGENERATE_ID_KEY))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(), equalTo(
                "list \"[set-header: [foo: \"bar\"], regenerate-id]\" of action target \"http-response\" must contain a single value"));
    }

    @Test
    public void protectActionUnsupportedTargetShouldFailValidation() {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(newRampartConstant("unknown"), newRampartConstant("undefined"))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(), equalTo(
                "parameter \"unknown: undefined\" to the action \"protect\" is not supported"));
    }

    @Test
    public void protectActionTargetWithUnknownAttribute() {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(HTTP_RESPONSE_KEY, newRampartList(
                        newRampartNamedValue(newRampartConstant("unknown"), newRampartConstant("undefined"))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(),
                equalTo("unsupported attribute \"unknown\" for action target \"http-response\""));
    }

    @Test
    public void protectActionDuplicateTarget() {
        RampartNamedValue headerToSet = newRampartNamedValue(newRampartConstant("foo"), newRampartString("bar"));
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(HTTP_RESPONSE_KEY, newRampartList(
                        newRampartNamedValue(SET_HEADER_KEY, newRampartList(headerToSet)))),
                newRampartNamedValue(HTTP_RESPONSE_KEY,
                        newRampartNamedValue(newRampartConstant("add-header"), newRampartList(headerToSet)))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(), equalTo(
                "invalid specified target \"http-response\". Only a single target is allowed for \"protect\" declaration"));
    }

    @Test
    public void protectActionTargetWithInvalidRegenerateIdAttribute() {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(HTTP_SESSION_KEY, newRampartList(
                        newRampartNamedValue(REGENERATE_ID_KEY, newRampartString("sessionIDalkmdajdh"))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(), equalTo("action target attribute \"regenerate-id\" must be a constant"));
    }

    @Test
    public void protectActionHasTargetWithSetHeaderAttributeInvalidValue() {
        RampartString headerName = newRampartString("foo");
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(HTTP_RESPONSE_KEY, newRampartList(
                        newRampartNamedValue(SET_HEADER_KEY, newRampartList(headerName))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(),
                equalTo("\"set-header\" attribute must contain name value pairs with string literals as values"));
    }

    @Test
    public void protectActionHasTargetWithInvalidAttributeType() {
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(
                newRampartNamedValue(HTTP_RESPONSE_KEY, newRampartString(REGENERATE_ID_KEY.toString()))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(),
                equalTo("action attribute \"regenerate-id\" must be a key value pair or a constant"));
    }

    @Test
    public void actionWithStacktraceShouldFailValidation() {
        symbolTable.put(DETECT_KEY.toString(),
                newRampartList(
                        newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
                        newRampartNamedValue(SEVERITY_KEY, newRampartInteger(5)),
                        newRampartNamedValue(STACKTRACE_KEY, HttpActionValidator2_3Test.STACKTRACE_VALUE)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> actionValidator.validateRampartAction());

        assertThat(thrown.getMessage(), equalTo(
                "parameter \"stacktrace: \"full\"\" to the action \"detect\" is not supported"));
    }
}
