package org.rampart.lang.impl.http.validators.v1;

import org.rampart.lang.api.RampartString;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.junit.jupiter.api.Test;

import static org.rampart.lang.api.http.RampartHttpValidationType.METHOD;
import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.impl.http.validators.v1.RampartHttpActionValidatorUpTo1_5.HTTP_CSRF_AND_METHOD_VALIDATION_SUPPORTED_ACTION_TYPES;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("deprecation")
public class RampartHttpActionValidator1_6Test {
    private static final RampartString LOG_MESSAGE = newRampartString("logMessage");

    @Test
    public void unsupportedAllowActionForMethodValidation() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpActionValidator1_6(newRampartList(
                newRampartNamedValue(ALLOW_KEY, LOG_MESSAGE),
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(1))))
                .validateHttpAction(METHOD));

        assertThat(thrown.getMessage(),
                equalTo("invalid Rampart http \"method\" action specified. Valid \"method\" actions are: "
                        + HTTP_CSRF_AND_METHOD_VALIDATION_SUPPORTED_ACTION_TYPES));
    }

    @Test
    public void supportedDetectActionForMethodValidation() throws InvalidRampartRuleException {
        new RampartHttpActionValidator1_6(newRampartList(
                newRampartNamedValue(DETECT_KEY, LOG_MESSAGE),
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(1))))
        .validateHttpAction(METHOD);
    }

    @Test
    public void supportedProtectActionForMethodValidation() throws InvalidRampartRuleException {
        new RampartHttpActionValidator1_6(newRampartList(
                newRampartNamedValue(PROTECT_KEY, LOG_MESSAGE),
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(1))))
                .validateHttpAction(METHOD);
    }
}
