package org.rampart.lang.impl.http.validators.v1;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.impl.http.validators.v1.RampartHttpActionValidatorUpTo1_5.HTTP_CSRF_AND_METHOD_VALIDATION_SUPPORTED_ACTION_TYPES;
import static org.rampart.lang.impl.http.validators.v1.RampartHttpActionValidatorUpTo1_5.HTTP_INPUT_VALIDATION_SUPPORTED_ACTION_TYPES;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import static org.rampart.lang.api.http.RampartHttpValidationType.CSRF;
import static org.rampart.lang.api.http.RampartHttpValidationType.HTTP_PARAMETER;

import org.rampart.lang.api.RampartString;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

public class RampartHttpActionValidatorUpTo1_5Test {
    private static final RampartString LOG_MESSAGE = newRampartString("logMessage");

    @Test
    public void supportedCSRFValidationActionIsValidated() throws InvalidRampartRuleException {
        new RampartHttpActionValidatorUpTo1_5(newRampartList(
                newRampartNamedValue(DETECT_KEY, LOG_MESSAGE),
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(1))))
                .validateHttpAction(CSRF);
    }

    @Test
    public void supportedHTTPInputValidationActionIsValidated() throws InvalidRampartRuleException {
        new RampartHttpActionValidatorUpTo1_5(newRampartList(newRampartNamedValue(ALLOW_KEY, LOG_MESSAGE),
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(1))))
                .validateHttpAction(HTTP_PARAMETER);
    }

    @Test
    public void unsupportedHTTPInputValidationActionThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpActionValidatorUpTo1_5(newRampartList(
                    newRampartNamedValue(CORRECT_KEY, LOG_MESSAGE),
                    newRampartNamedValue(SEVERITY_KEY, newRampartInteger(1))))
                .validateHttpAction(HTTP_PARAMETER));

        assertThat(thrown.getMessage(),
                equalTo("invalid Rampart http \"parameter\" action specified. Valid \"parameter\" actions are: "
                        + HTTP_INPUT_VALIDATION_SUPPORTED_ACTION_TYPES));
    }

    @Test
    public void unsupportedCSRFValidationActionThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpActionValidatorUpTo1_5(newRampartList(
                    newRampartNamedValue(ALLOW_KEY, LOG_MESSAGE),
                    newRampartNamedValue(SEVERITY_KEY, newRampartInteger(1))))
                    .validateHttpAction(CSRF));

        assertThat(thrown.getMessage(), equalTo("invalid Rampart http \"csrf\" action specified. Valid \"csrf\" actions are: "
                + HTTP_CSRF_AND_METHOD_VALIDATION_SUPPORTED_ACTION_TYPES));
    }
}
