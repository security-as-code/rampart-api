package org.rampart.lang.impl.http.validators.v1;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.api.http.RampartHttpInjectionType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.junit.jupiter.api.Test;

@SuppressWarnings("deprecation")
public class RampartHttpInjectionTypeValidator1_6Test {

    @Test
    public void injectionDeclarationNotMandatory() throws InvalidRampartRuleException {
        RampartHttpInjectionTypeValidator1_6 validator = new RampartHttpInjectionTypeValidator1_6(null);
        validator.validateInjectionType();
        validator.validateForActions(mock(RampartAction.class));
    }

    @Test
    public void injectionHeadersStringParameterValid() throws InvalidRampartRuleException {
        RampartList validValues = newRampartList(HEADERS_KEY.asRampartString());

        RampartHttpInjectionType type = new RampartHttpInjectionTypeValidator1_6(validValues).validateInjectionType();

        assertThat(type.onHeaderInjection(), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void injectionHeadersConstantParameterInvalid() {
        RampartList validValues = newRampartList(HEADERS_KEY);

        InvalidRampartRuleException throwable = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpInjectionTypeValidator1_6(validValues).validateInjectionType());

        assertThat(throwable.getMessage(),
                equalTo("only \"headers\" string literal is supported for \"injection\" declaration"));
    }

    @Test
    public void moreThanOneParameterInInjectionDeclaration() {
        RampartList validValues = newRampartList(HEADERS_KEY.asRampartString(), newRampartString("undefined"));

        InvalidRampartRuleException throwable = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpInjectionTypeValidator1_6(validValues).validateInjectionType());

        assertThat(throwable.getMessage(), equalTo("only a single parameter is allowed for \"injection\" declaration"));
    }

    @Test
    public void foreignParameterInInjectionDeclaration() {
        RampartList validValues = newRampartList(newRampartString("undefined"));

        InvalidRampartRuleException throwable = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpInjectionTypeValidator1_6(validValues).validateInjectionType());

        assertThat(throwable.getMessage(),
                equalTo("only \"headers\" string literal is supported for \"injection\" declaration"));
    }

    @Test
    public void injectionDeclarationNoParameters() {
        RampartList validValues = RampartList.EMPTY;

        InvalidRampartRuleException throwable = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpInjectionTypeValidator1_6(validValues).validateInjectionType());

        assertThat(throwable.getMessage(), equalTo("only a single parameter is allowed for \"injection\" declaration"));
    }

    @Test
    public void injectionDeclarationWithAllowAction() {
        RampartList validValues = newRampartList(HEADERS_KEY.asRampartString());
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.ALLOW);

        InvalidRampartRuleException throwable = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpInjectionTypeValidator1_6(validValues).validateForActions(action));

        assertThat(throwable.getMessage(), equalTo("action \"allow\" is unsupported with \"injection\" declaration"));
    }

    @Test
    public void injectionDeclarationWithProtectAction() throws InvalidRampartRuleException {
        RampartList validValues = newRampartList(HEADERS_KEY.asRampartString());
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);

        new RampartHttpInjectionTypeValidator1_6(validValues).validateForActions(action);
    }

    @Test
    public void injectionDeclarationWithDetectAction() throws InvalidRampartRuleException {
        RampartList validValues = newRampartList(HEADERS_KEY.asRampartString());
        RampartAction action = mock(RampartAction.class);
        when(action.getActionType()).thenReturn(RampartActionType.PROTECT);

        new RampartHttpInjectionTypeValidator1_6(validValues).validateForActions(action);
    }
}
