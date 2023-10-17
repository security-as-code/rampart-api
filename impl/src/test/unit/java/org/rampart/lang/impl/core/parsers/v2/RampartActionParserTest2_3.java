package org.rampart.lang.impl.core.parsers.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.MESSAGE_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.SEVERITY_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.STACKTRACE_KEY;
import static org.rampart.lang.java.RampartPrimitives.newRampartConstant;
import static org.rampart.lang.java.RampartPrimitives.newRampartInteger;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.rampart.lang.java.RampartPrimitives.newRampartNamedValue;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

/** Test(s) for action parser 2.3. */
public final class RampartActionParserTest2_3 {

    private static final RampartString LOG_MESSAGE = newRampartString("log message");
    private static final RampartString STACKTRACE_VALUE_FULL = newRampartString("full");

    /* Checks inherited from DNS test. */

    @Test
    public void actionWithStacktraceValidatedSuccessfully() throws InvalidRampartRuleException {
        RampartAction action = parseStdAction(
            RampartActionType.DETECT,
            newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
            newRampartNamedValue(SEVERITY_KEY, newRampartInteger(5)),
            newRampartNamedValue(STACKTRACE_KEY, STACKTRACE_VALUE_FULL)
        );

        assertThat(action.getStacktrace(), equalTo(STACKTRACE_VALUE_FULL));
    }

    @Test
    public void invalidActionNumberParameter() {
        final String message = failStdParsingMessage(
                RampartActionType.PROTECT,
                newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
                newRampartInteger(5)
        );
        assertThat(message, equalTo("parameter \"5\" to the action \"protect\" is not supported"));
    }

    @Test
    public void invalidActionNamedValueParameter() {
        final String message = failStdParsingMessage(
                RampartActionType.PROTECT,
                newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
                newRampartNamedValue(newRampartConstant("make"), newRampartString("coffee"))
        );
        assertThat(message, equalTo("parameter \"make: \"coffee\"\" to the action \"protect\" is not supported"));
    }


    /**
     * Parses an action from the given input(s), assumes standard set of supported action types.
     */
    public static RampartAction parseStdAction(RampartActionType actionType, RampartObject... params)
            throws InvalidRampartRuleException {
        final Map<String, RampartList> symbolTable = new HashMap<>();
        symbolTable.put(actionType.getName().toString(), newRampartList(params));
        return RampartActionParser2_3.parseRampartAction(symbolTable, newRampartString("Test rule"),
                RampartActionType.ALLOW, RampartActionType.DETECT, RampartActionType.PROTECT);
    }


    /** Attempts to parse the action, ensures it fails and returns a failure message. */
    public static String failStdParsingMessage(RampartActionType actionType, RampartObject... params) {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> parseStdAction(actionType, params));
        return thrown.getMessage();
    }
}
