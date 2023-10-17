package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class RampartActionBuilderTest {
    private static final RampartString MESSAGE = newRampartString("action message here");
    private static final RampartString STACKTRACE = newRampartString("full");
    private static final RampartActionTarget TARGET = RampartActionTarget.HTTP_RESPONSE;
    private static final RampartActionAttribute ATTRIBUTE = RampartActionAttribute.SET_HEADER;
    private static final RampartList HEADERS_CONFIG = newRampartList(
            newRampartNamedValue(newRampartConstant("foo"), newRampartString("bar")),
            newRampartNamedValue(newRampartConstant("three"), newRampartString("little pigs")));

    private RampartActionBuilder builder;

    @BeforeEach
    public void beforeEach() {
        builder = new RampartActionBuilder();
    }

    @Test
    public void createSimpleRampartAction() {
        RampartAction action = builder.addActionType(RampartActionType.PROTECT)
                                    .addSeverity(RampartSeverity.MEDIUM)
                                    .createRampartObject();
        assertThat(action.toString(), equalTo("protect(severity: Medium)"));
    }

    @Test
    public void createSimpleRampartActionWithLogging() {
        RampartAction action = builder.addActionType(RampartActionType.PROTECT)
                .addSeverity(RampartSeverity.MEDIUM)
                .addLogMessage(MESSAGE)
                .createRampartObject();
        assertThat(action.toString(), equalTo("protect(message: \"" + MESSAGE + "\", severity: Medium)"));
    }

    @Test
    public void createSimpleRampartActionWithLoggingAndStacktrace() {
        RampartAction action = builder.addActionType(RampartActionType.PROTECT)
                .addSeverity(RampartSeverity.MEDIUM)
                .addLogMessage(MESSAGE)
                .addStacktrace(STACKTRACE)
                .createRampartObject();
        assertThat(action.toString(), equalTo(
                "protect(message: \"" + MESSAGE + "\", severity: Medium, stacktrace: \"full\")"));
    }

    @Test
    public void createRampartActionWithTarget() {
        RampartAction action = builder.addActionType(RampartActionType.PROTECT)
                .addSeverity(RampartSeverity.MEDIUM)
                .addActionTarget(TARGET)
                .addActionAttribute(ATTRIBUTE)
                .addTargetConfigMap(HEADERS_CONFIG)
                .createRampartObject();
        assertThat(action.toString(), equalTo(
                "protect(http-response: {set-header: {foo: \"bar\", three: \"little pigs\"}}, severity: Medium)"));
    }

    @Test
    public void createRampartActionWithTargetWithLogging() {
        RampartAction action = builder.addActionType(RampartActionType.PROTECT)
                .addSeverity(RampartSeverity.MEDIUM)
                .addLogMessage(MESSAGE)
                .addActionTarget(TARGET)
                .addActionAttribute(ATTRIBUTE)
                .addTargetConfigMap(HEADERS_CONFIG)
                .createRampartObject();
        assertThat(action.toString(), equalTo(
                "protect(http-response: {set-header: {foo: \"bar\", three: \"little pigs\"}}, message: \"" + MESSAGE + "\", severity: Medium)"));
    }

    @Test
    public void createRampartActionWithTargetWithLoggingAndStacktrace() {
        RampartAction action = builder.addActionType(RampartActionType.PROTECT)
                .addSeverity(RampartSeverity.MEDIUM)
                .addLogMessage(MESSAGE)
                .addStacktrace(STACKTRACE)
                .addActionTarget(TARGET)
                .addActionAttribute(ATTRIBUTE)
                .addTargetConfigMap(HEADERS_CONFIG)
                .createRampartObject();
        assertThat(action.toString(), equalTo(
                "protect(http-response: {set-header: {foo: \"bar\", three: \"little pigs\"}}, message: \"" + MESSAGE + "\", severity: Medium, stacktrace: \"full\")"));
    }
}
