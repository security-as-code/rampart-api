package org.rampart.lang.impl.http.validators.v2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;

import java.util.HashMap;

import org.rampart.lang.java.builder.RampartHttpBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartActionAttribute;
import org.rampart.lang.api.core.RampartActionTarget;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.api.core.RampartActionWithAttribute;
import org.rampart.lang.api.core.RampartVersion;
import org.rampart.lang.api.http.RampartCsrf;
import org.rampart.lang.api.http.RampartHttpIOType;
import org.rampart.lang.api.http.RampartHttpInputValidation;
import org.rampart.lang.api.http.RampartXss;
import org.rampart.lang.api.core.RampartInput;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

@SuppressWarnings("deprecation")
public class RampartHttpStructureValidator2_0Test {

    private RampartHttpBuilder builder;
    private RampartHttpStructureValidator2_0 validator;

    @BeforeEach
    public void setUp() {
        builder = new RampartHttpBuilder(mock(RampartVersion.class));
        builder.addDataInputs(RampartList.EMPTY);
        validator = new RampartHttpStructureValidator2_0(new HashMap<>(), builder);
    }

    @Test
    public void multipleSecurityFeaturesDeclared() {
        builder.addAuthenticate(RampartBoolean.TRUE);
        builder.addCsrf(mock(RampartCsrf.class));
        builder.addHttpInputValidation(mock(RampartHttpInputValidation.class));
        builder.addOpenRedirect(RampartBoolean.TRUE);
        builder.addXss(mock(RampartXss.class));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(), endsWith("cannot be declared together"));
    }

    @Test
    public void twoSecurityFeaturesDeclared() {
        builder.addCsrf(mock(RampartCsrf.class));
        builder.addXss(mock(RampartXss.class));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(), equalTo("\"xss\" and \"csrf\" cannot be declared together"));
    }

    @Test
    public void protectiveOpenRedirectWithRequestDeclaration() {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        builder.addHttpIOType(RampartHttpIOType.REQUEST);
        builder.addOpenRedirect(RampartBoolean.TRUE);
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(),
                equalTo("invalid declaration of \"request\" with declaration of \"open-redirect\""));
    }

    @Test
    public void protectiveOpenRedirectWithResponseDeclaration() throws InvalidRampartRuleException {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        builder.addHttpIOType(RampartHttpIOType.RESPONSE);
        builder.addOpenRedirect(RampartBoolean.TRUE);
        builder.addAction(action);
        validator.crossValidate(builder);
    }

    @Test
    public void allowedOpenRedirectWithResponseDeclaration() {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.ALLOW);
        builder.addHttpIOType(RampartHttpIOType.RESPONSE);
        builder.addOpenRedirect(RampartBoolean.TRUE);
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(),
                equalTo("invalid declaration of \"allow\" with declaration of \"open-redirect\""));
    }

    @Test
    public void protectiveOpenRedirectWithResponseDeclarationTaintedInputs() throws InvalidRampartRuleException {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        builder.addHttpIOType(RampartHttpIOType.RESPONSE);
        builder.addDataInputs(newRampartList(RampartInput.HTTP));
        builder.addOpenRedirect(RampartBoolean.TRUE);
        builder.addAction(action);
        validator.crossValidate(builder);
    }

    @Test
    public void detectingOpenRedirectWithResponseDeclaration() throws InvalidRampartRuleException {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.DETECT);
        builder.addHttpIOType(RampartHttpIOType.RESPONSE);
        builder.addOpenRedirect(RampartBoolean.TRUE);
        builder.addAction(action);
        validator.crossValidate(builder);
    }

    @Test
    public void protectiveOpenRedirectWithResponseDeclarationWithHttpResponseActionTarget() {
        RampartActionWithAttribute action = mock(RampartActionWithAttribute.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        when(action.getTarget()).thenReturn(RampartActionTarget.HTTP_RESPONSE);
        builder.addHttpIOType(RampartHttpIOType.RESPONSE);
        builder.addOpenRedirect(RampartBoolean.TRUE);
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(),
                equalTo("declaration \"open-redirect\" does not support action target \"http-response\""));
    }

    @Test
    public void protectiveOpenRedirectWithResponseDeclarationHttpSessionActionTarget() {
        RampartActionWithAttribute action = mock(RampartActionWithAttribute.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        when(action.getTarget()).thenReturn(RampartActionTarget.HTTP_SESSION);
        builder.addHttpIOType(RampartHttpIOType.RESPONSE);
        builder.addOpenRedirect(RampartBoolean.TRUE);
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(),
                equalTo("declaration \"open-redirect\" does not support action target \"http-session\""));
    }

    @Test
    public void protectiveSessionFixationWithRequestDeclaration() {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        builder.addHttpIOType(RampartHttpIOType.REQUEST);
        builder.addAuthenticate(RampartBoolean.TRUE);
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(), equalTo(
                "action \"protect\" is missing the action attribute \"regenerate-id\" for target \"http-session\""));
    }

    @Test
    public void protectiveSessionFixationWithResponseDeclaration() {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        builder.addHttpIOType(RampartHttpIOType.RESPONSE);
        builder.addAuthenticate(RampartBoolean.TRUE);
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(),
                equalTo("invalid declaration of \"response\" with declaration of \"authenticate\""));
    }

    @Test
    public void allowedSessionFixationWithRequestDeclaration() {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.ALLOW);
        builder.addHttpIOType(RampartHttpIOType.REQUEST);
        builder.addAuthenticate(RampartBoolean.TRUE);
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(),
                equalTo("invalid declaration of \"allow\" with declaration of \"authenticate\""));
    }

    @Test
    public void protectiveSessionFixationWithRequestDeclarationTaintedInputs() {
        RampartActionWithAttribute action = mock(RampartActionWithAttribute.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        builder.addHttpIOType(RampartHttpIOType.REQUEST);
        builder.addDataInputs(newRampartList(RampartInput.HTTP));
        builder.addAuthenticate(RampartBoolean.TRUE);
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(),
                equalTo("invalid declaration of \"input\" with declaration of \"authenticate\""));
    }

    @Test
    public void detectingSessionFixationWithRequestDeclaration() throws InvalidRampartRuleException {
        RampartActionWithAttribute action = mock(RampartActionWithAttribute.class);
        when(action.getActionType()).thenReturn(RampartActionType.DETECT);
        builder.addHttpIOType(RampartHttpIOType.REQUEST);
        builder.addAuthenticate(RampartBoolean.TRUE);
        builder.addAction(action);
        validator.crossValidate(builder);
    }

    @Test
    public void protectiveSessionFixationWithRequestDeclarationWithHttpResponseActionTarget() {
        RampartActionWithAttribute action = mock(RampartActionWithAttribute.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        when(action.getTarget()).thenReturn(RampartActionTarget.HTTP_RESPONSE);
        builder.addHttpIOType(RampartHttpIOType.REQUEST);
        builder.addAuthenticate(RampartBoolean.TRUE);
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(), equalTo(
                "action \"protect\" is missing the action attribute \"regenerate-id\" for target \"http-session\""));
    }

    @Test
    public void protectiveSessionFixationWithRequestDeclarationHttpSessionActionTarget() throws InvalidRampartRuleException {
        RampartActionWithAttribute action = mock(RampartActionWithAttribute.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        when(action.getTarget()).thenReturn(RampartActionTarget.HTTP_SESSION);
        when(action.getAttribute()).thenReturn(RampartActionAttribute.REGENERATE_ID);
        builder.addHttpIOType(RampartHttpIOType.REQUEST);
        builder.addAuthenticate(RampartBoolean.TRUE);
        builder.addAction(action);
        validator.crossValidate(builder);
    }

    @Test
    public void plainProtectActionWithRequestDeclaration() {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        builder.addHttpIOType(RampartHttpIOType.REQUEST);
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(), equalTo(
                "declaration of \"request\" is invalid with the current combination of declarations for RAMPART http rule"));
    }

    @Test
    public void plainProtectActionWithResponseDeclaration() {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        builder.addHttpIOType(RampartHttpIOType.RESPONSE);
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(), equalTo(
                "action \"protect\" is missing the action attribute \"set-header\" for target \"http-response\""));
    }

    @Test
    public void plainAllowActionWithResponseDeclaration() {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.ALLOW);
        builder.addHttpIOType(RampartHttpIOType.RESPONSE);
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(), equalTo(
                "declaration of \"allow\" is invalid with the current combination of declarations for RAMPART http rule"));
    }

    @Test
    public void plainDetectActionWithResponseDeclaration() throws InvalidRampartRuleException {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.DETECT);
        builder.addHttpIOType(RampartHttpIOType.RESPONSE);
        builder.addAction(action);
        validator.crossValidate(builder);
    }

    @Test
    public void plainDetectActionWithRequestDeclaration() {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.DETECT);
        builder.addHttpIOType(RampartHttpIOType.REQUEST);
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(), equalTo(
                "declaration of \"request\" is invalid with the current combination of declarations for RAMPART http rule"));
    }

    @Test
    public void protectActionWithResponseDeclarationTaintedInputs() {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        builder.addHttpIOType(RampartHttpIOType.RESPONSE);
        builder.addDataInputs(newRampartList(RampartInput.HTTP));
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(), equalTo(
                "declaration of \"input\" is invalid with the current combination of declarations for RAMPART http rule"));
    }

    @Test
    public void protectActionWithResponseDeclarationAndHttpResponseActionTarget() {
        RampartActionWithAttribute action = mock(RampartActionWithAttribute.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        when(action.getTarget()).thenReturn(RampartActionTarget.HTTP_RESPONSE);
        builder.addHttpIOType(RampartHttpIOType.RESPONSE);
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(), equalTo(
                "action \"protect\" is missing the action attribute \"set-header\" for target \"http-response\""));
    }

    @Test
    public void protectActionWithResponseDeclarationAndSetHeaderAttribute() throws InvalidRampartRuleException {
        RampartActionWithAttribute action = mock(RampartActionWithAttribute.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        when(action.getTarget()).thenReturn(RampartActionTarget.HTTP_RESPONSE);
        when(action.getAttribute()).thenReturn(RampartActionAttribute.SET_HEADER);
        builder.addHttpIOType(RampartHttpIOType.RESPONSE);
        builder.addAction(action);
        validator.crossValidate(builder);
    }

    @Test
    public void protectActionWithResponseDeclarationHttpSessionActionTarget() {
        RampartActionWithAttribute action = mock(RampartActionWithAttribute.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        when(action.getTarget()).thenReturn(RampartActionTarget.HTTP_SESSION);
        builder.addHttpIOType(RampartHttpIOType.RESPONSE);
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(), equalTo(
                "action \"protect\" is missing the action attribute \"set-header\" for target \"http-response\""));
    }

    @Test
    public void protectiveCsrfWithRequestDeclaration() throws InvalidRampartRuleException {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        builder.addHttpIOType(RampartHttpIOType.REQUEST);
        builder.addCsrf(mock(RampartCsrf.class));
        builder.addAction(action);
        validator.crossValidate(builder);
    }

    @Test
    public void protectiveCsrfWithResponseDeclaration() {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        builder.addHttpIOType(RampartHttpIOType.RESPONSE);
        builder.addCsrf(mock(RampartCsrf.class));
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(), equalTo("invalid declaration of \"response\" with declaration of \"csrf\""));
    }

    @Test
    public void allowedCsrfWithRequestDeclaration() {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.ALLOW);
        builder.addHttpIOType(RampartHttpIOType.REQUEST);
        builder.addCsrf(mock(RampartCsrf.class));
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(), equalTo("invalid declaration of \"allow\" with declaration of \"csrf\""));
    }

    @Test
    public void protectiveCsrfWithRequestDeclarationTaintedInputs() {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        builder.addHttpIOType(RampartHttpIOType.REQUEST);
        builder.addDataInputs(newRampartList(RampartInput.HTTP));
        builder.addCsrf(mock(RampartCsrf.class));
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(), equalTo("invalid declaration of \"input\" with declaration of \"csrf\""));
    }

    @Test
    public void detectingCsrfWithRequestDeclaration() throws InvalidRampartRuleException {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.DETECT);
        builder.addHttpIOType(RampartHttpIOType.REQUEST);
        builder.addCsrf(mock(RampartCsrf.class));
        builder.addAction(action);
        validator.crossValidate(builder);
    }

    @Test
    public void protectiveCsrfWithRequestDeclarationWithHttpResponseActionTarget() {
        RampartActionWithAttribute action = mock(RampartActionWithAttribute.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        when(action.getTarget()).thenReturn(RampartActionTarget.HTTP_RESPONSE);
        builder.addHttpIOType(RampartHttpIOType.REQUEST);
        builder.addCsrf(mock(RampartCsrf.class));
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(),
                equalTo("declaration \"csrf\" does not support action target \"http-response\""));
    }

    @Test
    public void protectiveCsrfWithRequestDeclarationHttpSessionActionTarget() {
        RampartActionWithAttribute action = mock(RampartActionWithAttribute.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        when(action.getTarget()).thenReturn(RampartActionTarget.HTTP_SESSION);
        builder.addHttpIOType(RampartHttpIOType.REQUEST);
        builder.addCsrf(mock(RampartCsrf.class));
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(),
                equalTo("declaration \"csrf\" does not support action target \"http-session\""));
    }


    @Test
    public void protectiveXssWithResponseDeclaration() throws InvalidRampartRuleException {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        builder.addHttpIOType(RampartHttpIOType.RESPONSE);
        builder.addXss(mock(RampartXss.class));
        builder.addAction(action);
        validator.crossValidate(builder);
    }

    @Test
    public void protectiveXssWithRequestDeclaration() {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        builder.addHttpIOType(RampartHttpIOType.REQUEST);
        builder.addXss(mock(RampartXss.class));
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(), equalTo("invalid declaration of \"request\" with declaration of \"xss\""));
    }

    @Test
    public void allowedXssWithRequestDeclaration() {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.ALLOW);
        builder.addHttpIOType(RampartHttpIOType.RESPONSE);
        builder.addXss(mock(RampartXss.class));
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(), equalTo("invalid declaration of \"allow\" with declaration of \"xss\""));
    }

    @Test
    public void protectiveXssWithResponseDeclarationTaintedInputs() throws InvalidRampartRuleException {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        builder.addHttpIOType(RampartHttpIOType.RESPONSE);
        builder.addDataInputs(newRampartList(RampartInput.HTTP));
        builder.addXss(mock(RampartXss.class));
        builder.addAction(action);
        validator.crossValidate(builder);
    }

    @Test
    public void detectingCsrfWithResponseDeclaration() throws InvalidRampartRuleException {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.DETECT);
        builder.addHttpIOType(RampartHttpIOType.RESPONSE);
        builder.addXss(mock(RampartXss.class));
        builder.addAction(action);
        validator.crossValidate(builder);
    }

    @Test
    public void protectiveXssWithResponseDeclarationWithHttpResponseActionTarget() {
        RampartActionWithAttribute action = mock(RampartActionWithAttribute.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        when(action.getTarget()).thenReturn(RampartActionTarget.HTTP_RESPONSE);
        builder.addHttpIOType(RampartHttpIOType.RESPONSE);
        builder.addXss(mock(RampartXss.class));
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(),
                equalTo("declaration \"xss\" does not support action target \"http-response\""));
    }

    @Test
    public void protectiveXssWithResponseDeclarationHttpSessionActionTarget() {
        RampartActionWithAttribute action = mock(RampartActionWithAttribute.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        when(action.getTarget()).thenReturn(RampartActionTarget.HTTP_SESSION);
        builder.addHttpIOType(RampartHttpIOType.RESPONSE);
        builder.addXss(mock(RampartXss.class));
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(), equalTo("declaration \"xss\" does not support action target \"http-session\""));
    }

    @Test
    public void protectiveInputValidationWithRequestDeclaration() throws InvalidRampartRuleException {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        builder.addHttpIOType(RampartHttpIOType.REQUEST);
        builder.addHttpInputValidation(mock(RampartHttpInputValidation.class));
        builder.addAction(action);
        validator.crossValidate(builder);
    }

    @Test
    public void protectiveInputValidationWithResponseDeclaration() {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        builder.addHttpIOType(RampartHttpIOType.RESPONSE);
        builder.addHttpInputValidation(mock(RampartHttpInputValidation.class));
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(),
                equalTo("invalid declaration of \"response\" with declaration of \"validate\""));
    }

    @Test
    public void allowedInputValidationWithRequestDeclaration() throws InvalidRampartRuleException {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.ALLOW);
        builder.addHttpIOType(RampartHttpIOType.REQUEST);
        builder.addHttpInputValidation(mock(RampartHttpInputValidation.class));
        builder.addAction(action);
        validator.crossValidate(builder);
    }

    @Test
    public void protectiveInputValidationWithResponseDeclarationTaintedInputs() {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        builder.addHttpIOType(RampartHttpIOType.REQUEST);
        builder.addHttpInputValidation(mock(RampartHttpInputValidation.class));
        builder.addDataInputs(newRampartList(RampartInput.HTTP));
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(), equalTo("invalid declaration of \"input\" with declaration of \"validate\""));
    }

    @Test
    public void detectingInputValidationWithResponseDeclaration() throws InvalidRampartRuleException {
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.DETECT);
        builder.addHttpIOType(RampartHttpIOType.REQUEST);
        builder.addHttpInputValidation(mock(RampartHttpInputValidation.class));
        builder.addAction(action);
        validator.crossValidate(builder);
    }

    @Test
    public void protectiveInputValidationWithRequestDeclarationWithHttpResponseActionTarget() {
        RampartActionWithAttribute action = mock(RampartActionWithAttribute.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        when(action.getTarget()).thenReturn(RampartActionTarget.HTTP_RESPONSE);
        builder.addHttpIOType(RampartHttpIOType.REQUEST);
        builder.addHttpInputValidation(mock(RampartHttpInputValidation.class));
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(),
                equalTo("declaration \"validate\" does not support action target \"http-response\""));
    }

    @Test
    public void protectiveInputValidationWithRequestDeclarationHttpSessionActionTarget() {
        RampartActionWithAttribute action = mock(RampartActionWithAttribute.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);
        when(action.getTarget()).thenReturn(RampartActionTarget.HTTP_SESSION);
        builder.addHttpIOType(RampartHttpIOType.REQUEST);
        builder.addHttpInputValidation(mock(RampartHttpInputValidation.class));
        builder.addAction(action);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.crossValidate(builder));

        assertThat(thrown.getMessage(),
                equalTo("declaration \"validate\" does not support action target \"http-session\""));
    }
}
