package org.rampart.lang.impl.http.validators.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.DETECT_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.MESSAGE_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.PROTECT_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.SEVERITY_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.STACKTRACE_KEY;

import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HttpActionValidator2_3Test {

    private Map<String, RampartList> symbolTable;
    private static final RampartString LOG_MESSAGE = newRampartString("log message");
    private HttpActionValidator2_3 actionValidator;
    static final RampartString STACKTRACE_VALUE = newRampartString("full");

    @BeforeEach
    public void setUp() {
        symbolTable = new HashMap<>();
        actionValidator = new HttpActionValidator2_3(symbolTable);
    }

    @Test
    public void actionWithStacktraceValidatedSuccessfully() throws InvalidRampartRuleException {
        symbolTable.put(DETECT_KEY.toString(),
                newRampartList(
                        newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
                        newRampartNamedValue(SEVERITY_KEY, newRampartInteger(5)),
                        newRampartNamedValue(STACKTRACE_KEY, STACKTRACE_VALUE)));

        RampartAction action = actionValidator.validateRampartAction();
        assertThat(action.hasStacktrace(), equalTo(RampartBoolean.TRUE));
        assertThat(action.getStacktrace(), equalTo(STACKTRACE_VALUE));
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
