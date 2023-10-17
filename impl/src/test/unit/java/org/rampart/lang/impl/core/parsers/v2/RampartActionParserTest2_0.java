package org.rampart.lang.impl.core.parsers.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.ALLOW_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.MESSAGE_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.PROTECT_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.SEVERITY_KEY;
import static org.rampart.lang.java.RampartPrimitives.newRampartConstant;
import static org.rampart.lang.java.RampartPrimitives.newRampartInteger;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.rampart.lang.java.RampartPrimitives.newRampartNamedValue;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

/** Test(s) for action parser RAMPART/2.0. */
public final class RampartActionParserTest2_0 {

    private static final RampartString LOG_MESSAGE = newRampartString("log message");

    private static final RampartActionType[] SUPPORTED_ACTIONS =
        {RampartActionType.ALLOW, RampartActionType.DETECT, RampartActionType.PROTECT};

    /* Checks inherited from DNS test. */

    @Test
    public void allowActionValidatedSuccessfully() throws InvalidRampartRuleException {
        parseStdAction(RampartActionType.ALLOW);
    }

    @Test
    public void detectActionValidatedSuccessfully() throws InvalidRampartRuleException {
        parseStdAction(RampartActionType.DETECT,
                newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(5))
        );
    }

    @Test
    public void protectActionValidatedSuccessfully() throws InvalidRampartRuleException {
        parseStdAction(RampartActionType.PROTECT,
                newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(5))
        );
    }

    @Test
    public void invalidActionThrowsException() {
        final Map<String, RampartList> symbolTable = new HashMap<>();
        symbolTable.put("invalidAction", newRampartList(newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(5))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> RampartActionParser2_0.parseRampartAction(
                        symbolTable, newRampartString("Test rule"), SUPPORTED_ACTIONS
                      )
        );

        assertThat(thrown.getMessage(),
                equalTo("RAMPART \"Test rule\" action is missing. Must be one of: " + Arrays.toString(SUPPORTED_ACTIONS)));
    }

    @Test
    public void multipleValidActionsThrowsException() {
        final Map<String, RampartList> symbolTable = new HashMap<>();
        symbolTable.put(PROTECT_KEY.toString(), newRampartList(newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(5))));
        symbolTable.put(ALLOW_KEY.toString(), newRampartList());

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> RampartActionParser2_0.parseRampartAction(
                        symbolTable, newRampartString("Test rule"), SUPPORTED_ACTIONS
                      )
        );

        assertThat(thrown.getMessage(), equalTo(
                "actions \"allow\" and \"protect\" are declared. Declaration of more than one action type is not allowed."));
    }

    @Test
    public void validActionAndInvalidActionSucessfull() throws InvalidRampartRuleException {
        final Map<String, RampartList> symbolTable = new HashMap<>();
        symbolTable.put("invalidAction", newRampartList());
        symbolTable.put(ALLOW_KEY.toString(), newRampartList());

        RampartActionParser2_0.parseRampartAction(symbolTable, newRampartString("Test rule"), SUPPORTED_ACTIONS);
    }

    @Test
    public void notSupportedActionThrowsException() {
        final String message = extractMessageFromParseStdActionFailure(RampartActionType.CORRECT,
                newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
                newRampartNamedValue(SEVERITY_KEY, newRampartInteger(5))
        );
        assertThat(message,
                equalTo("RAMPART \"Test rule\" action is missing. Must be one of: " + Arrays.toString(SUPPORTED_ACTIONS)));
    }

    @Test
    public void invalidActionNumberParameter() {
        final String message = extractMessageFromParseStdActionFailure(RampartActionType.PROTECT,
                newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
                newRampartInteger(5)
        );
        assertThat(message, equalTo("parameter \"5\" to the action \"protect\" is not supported"));
    }

    @Test
    public void invalidActionNamedValueParameter() {
        final String message = extractMessageFromParseStdActionFailure(RampartActionType.PROTECT,
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
        return RampartActionParser2_0.parseRampartAction(symbolTable, newRampartString("Test rule"),
                SUPPORTED_ACTIONS);
    }


    /** Attempts to parse the action, ensures it fails and returns a failure message. */
    public static String extractMessageFromParseStdActionFailure(RampartActionType actionType, RampartObject... params) {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> parseStdAction(actionType, params));
        return thrown.getMessage();
    }
}
