package org.rampart.lang.impl.socket.parsers.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.MESSAGE_KEY;
import static org.rampart.lang.api.constants.RampartSocketConstants.*;

import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.rampart.lang.api.core.RampartActionType.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartActionAttribute;
import org.rampart.lang.api.core.RampartActionTarget;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.api.core.RampartActionWithAttribute;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.parsers.v2.RampartActionAttributeParser2_0;
import org.rampart.lang.java.RampartPrimitives;

/**
 * Test for parsing RAMPART action used in the Socket rule parser for RAMPART 2.1.
 *
 * The actual test is now somewhat obsolete. At some time in the past
 * there was a separate class with a long inheritance chain that was worth testing.
 * Now all the attribute parsing is "just" a call to an utility method with some
 * configuration and the "integration point" is gone. Unfortunately, this method
 * is not high-level enough to test the whole class.
 *
 * It is still somewhat useful though it tests the attribute parsing utilities
 * (that should eventually be tested directly).
 */
public class RampartSocketActionParserTest2_1 {

    private static final RampartString LOG_MESSAGE = newRampartString("log message");

    @Test
    public void validateActionTypeAllowValidActionType() throws InvalidRampartRuleException {
        assertThat(
            parseActionType(ALLOW_KEY, RampartList.EMPTY),
            equalTo(ALLOW)
        );
    }

    @Test
    public void validateActionTypeProtectValidActionType() throws InvalidRampartRuleException {
        assertThat(
            parseActionType(PROTECT_KEY, RampartList.EMPTY),
            equalTo(PROTECT)
        );
    }

    @Test
    public void validateActionTypeDetectValidActionType() throws InvalidRampartRuleException {
        assertThat(
            parseActionType(DETECT_KEY, newRampartList(newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE))),
            equalTo(DETECT)
        );
    }

    @Test
    public void allowActionHasTargetWithAttributes() {
        InvalidRampartRuleException thrown =
            assertThrows(InvalidRampartRuleException.class,
                () -> parseActionType(
                        ALLOW_KEY,
                        newRampartList(newRampartNamedValue(CONNECTION_KEY, SECURE_KEY))
                      )
            );

        assertThat(thrown.getMessage(), equalTo("action type \"allow\" does not support targets and attributes"));
    }

    @Test
    public void detectActionHasTargetWithAttributes() {
        InvalidRampartRuleException thrown =
            assertThrows(InvalidRampartRuleException.class,
                () -> parseActionType(
                          DETECT_KEY,
                          newRampartList(
                              newRampartNamedValue(CONNECTION_KEY, UPGRADE_TLS_KEY),
                              newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE)
                          )
                      )
            );

        assertThat(thrown.getMessage(), equalTo("action type \"detect\" does not support targets and attributes"));
    }

    @Test
    public void protectActionHasConnectionTargetWithSecureAttribute() throws InvalidRampartRuleException {
        final RampartAction action = parseAction(PROTECT_KEY,
                newRampartList(newRampartNamedValue(CONNECTION_KEY, SECURE_KEY)));

        assertAll(() -> {
            assertThat(action, is(instanceOf(RampartActionWithAttribute.class)));
            assertThat(((RampartActionWithAttribute) action).getTarget(), equalTo(RampartActionTarget.CONNECTION));
            assertThat(((RampartActionWithAttribute) action).getAttribute(), equalTo(RampartActionAttribute.SECURE));
        });
    }

    @Test
    public void protectActionHasConnectionTargetWithUpgradeTlsAttribute() throws InvalidRampartRuleException {
        final RampartAction action = parseAction(PROTECT_KEY,
                newRampartList(newRampartNamedValue(CONNECTION_KEY, UPGRADE_TLS_KEY)));

        assertAll(() -> {
            assertThat(action, is(instanceOf(RampartActionWithAttribute.class)));
            assertThat(((RampartActionWithAttribute) action).getTarget(), equalTo(RampartActionTarget.CONNECTION));
            assertThat(((RampartActionWithAttribute) action).getAttribute(), equalTo(RampartActionAttribute.UPGRADE_TLS));
        });
    }


    @Test
    public void protectActionHasConnectionTargetWithSecureAttributeInList() {
        InvalidRampartRuleException thrown =
            assertThrows(InvalidRampartRuleException.class,
                () -> parseAction(PROTECT_KEY, newRampartList(
                        newRampartNamedValue(CONNECTION_KEY, newRampartList(SECURE_KEY))
                      ))
            );

        assertThat(thrown.getMessage(), equalTo("action target attribute \"secure\" must be a constant"));
    }

    @Test
    public void protectActionHasConnectionTargetWithUpgradeTlsAttributeInList() {
        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class,
                    () -> parseAction(PROTECT_KEY, newRampartList(
                            newRampartNamedValue(CONNECTION_KEY, newRampartList(UPGRADE_TLS_KEY))
                          ))
                );

        assertThat(thrown.getMessage(), equalTo("action target attribute \"upgrade-tls\" must be a constant"));
    }

    @Test
    public void protectActionHasConnectionTargetWithBothSecureUpgradeTlsAttributes() {
        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class,
                    () -> parseAction(PROTECT_KEY, newRampartList(
                            newRampartNamedValue(CONNECTION_KEY, newRampartList(SECURE_KEY, UPGRADE_TLS_KEY))
                          ))
                );

        assertThat(thrown.getMessage(),
                equalTo("list \"[secure, upgrade-tls]\" of action target \"connection\" must contain a single value"));
    }

    @Test
    public void protectActionHasConnectionTargetWithInvalidAttribute() {
        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class,
                    () -> parseAction(PROTECT_KEY, newRampartList(
                            newRampartNamedValue(CONNECTION_KEY, newRampartConstant("shutdown"))
                          ))
                );

        assertThat(thrown.getMessage(), equalTo("unsupported attribute \"shutdown\" for action target \"connection\""));
    }

    @Test
    public void protectActionIsNotValidWhenHasInvalidTarget() {
        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class,
                    () -> parseAction(PROTECT_KEY, newRampartList(
                            newRampartNamedValue(newRampartConstant("undefined"), newRampartConstant("unsupported"))
                          ))
                );

        assertThat(thrown.getMessage(), equalTo(
                "parameter \"undefined: unsupported\" to the action \"protect\" is not supported"));
    }

    @Test
    public void protectActionIsNotValidWhenHasUnsupportedTarget() {
        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class,
                    () -> parseAction(PROTECT_KEY, newRampartList(
                            newRampartNamedValue(HTTP_SESSION_KEY, REGENERATE_ID_KEY)
                          ))
                );

        assertThat(thrown.getMessage(), equalTo(
                "parameter \"http-session: regenerate-id\" to the action \"protect\" is not supported"));
    }

    @Test
    public void invalidActionNumberParameter() {
        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class,
                    () -> parseAction(PROTECT_KEY, newRampartList(
                            newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
                            newRampartInteger(5)
                          ))
                );

        assertThat(thrown.getMessage(), equalTo("parameter \"5\" to the action \"protect\" is not supported"));
    }


    @Test
    public void invalidActionNamedValueParameter() {
        InvalidRampartRuleException thrown =
                assertThrows(InvalidRampartRuleException.class,
                    () -> parseAction(PROTECT_KEY, newRampartList(
                            newRampartNamedValue(MESSAGE_KEY, LOG_MESSAGE),
                            newRampartNamedValue(newRampartConstant("make"), newRampartString("coffee"))
                          ))
                );

        assertThat(thrown.getMessage(), equalTo("parameter \"make: \"coffee\"\" to the action \"protect\" is not supported"));
    }


    /** Parses action type.  */
    private RampartActionType parseActionType(RampartConstant key, RampartList symbols) throws InvalidRampartRuleException {
        return parseAction(key, symbols).getActionType();
    }


    /** Parses action.  */
    private RampartAction parseAction(RampartConstant key, RampartList symbols) throws InvalidRampartRuleException {
        final Map<String, RampartList> symbolTable = new HashMap<>();
        symbolTable.put(key.toString(), symbols);
        return RampartActionAttributeParser2_0.parseActionWithOptionalAttribute(
                symbolTable, RampartPrimitives.newRampartString("Hello, world"),
                RampartSocketParser2_1.SUPPORTED_ACTIONS,
                RampartSocketParser2_1.ATTRIBUTE_CONFIG_PARSER,
                RampartSocketParser2_1.SUPPORTED_ACTION_TARGETS
            );
    }
}
