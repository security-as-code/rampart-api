package org.rampart.lang.impl.sql.validators.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.DETECT_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.MESSAGE_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.PROTECT_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.SEVERITY_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.STACKTRACE_KEY;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.v2.RampartActionValidator2_3Plus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RampartSqlActionValidator2_3Test {

    protected Map<String, RampartList> symbolTable;
    private static final RampartString LOG_MESSAGE = newRampartString("log message");
    protected RampartActionValidator2_3Plus actionValidator;
    private static final RampartString STACKTRACE_VALUE = newRampartString("full");

    @BeforeEach
    public void setUp() {
        symbolTable = new HashMap<>();
        actionValidator = new RampartSqlActionValidator2_3(symbolTable);
    }

    @Test
    public void actionWithStacktraceValidatedSuccessfully() throws InvalidRampartRuleException {
        symbolTable.put(DETECT_KEY.toString(),
                newRampartList(
                        newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
                        newRampartNamedValue(SEVERITY_KEY, newRampartInteger(5)),
                        newRampartNamedValue(STACKTRACE_KEY, STACKTRACE_VALUE)));

        RampartAction action = actionValidator.validateRampartAction();

        assertThat(action.getStacktrace(), equalTo(STACKTRACE_VALUE));
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
