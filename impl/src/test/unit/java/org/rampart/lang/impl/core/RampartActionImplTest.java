package org.rampart.lang.impl.core;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartString;

import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.api.core.RampartSeverity;
import org.junit.jupiter.api.Test;

import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@SuppressWarnings("deprecation")
public class RampartActionImplTest {

    private static final RampartString LOG_MESSAGE = newRampartString("log message");

    @Test
    public void equals_before_2_3_noStacktrace() {
        RampartAction left = new RampartActionImpl(RampartActionType.ALLOW, LOG_MESSAGE, RampartSeverity.HIGH, RampartBoolean.FALSE);
        RampartAction right = new RampartActionImpl(RampartActionType.ALLOW, LOG_MESSAGE, RampartSeverity.HIGH, RampartBoolean.FALSE);
        assertThat(left, equalTo(right));
    }

    @Test
    public void equals_after_2_3_noStacktrace() {
        RampartAction left = new RampartActionImpl(RampartActionType.ALLOW, LOG_MESSAGE,
                RampartSeverity.HIGH, RampartBoolean.FALSE, null);
        RampartAction right = new RampartActionImpl(RampartActionType.ALLOW, LOG_MESSAGE,
                RampartSeverity.HIGH, RampartBoolean.FALSE, null);
        assertThat(left, equalTo(right));
    }

    @Test
    public void equals_before_2_3_shouldLogMatters() {
        RampartAction left = new RampartActionImpl(RampartActionType.ALLOW, LOG_MESSAGE, RampartSeverity.HIGH, RampartBoolean.FALSE);
        RampartAction right = new RampartActionImpl(RampartActionType.ALLOW, LOG_MESSAGE, RampartSeverity.HIGH, RampartBoolean.TRUE);
        assertThat(left, not(equalTo(right)));
    }

    @Test
    public void equals_after_2_3_shouldLogMatters() {
        RampartAction left = new RampartActionImpl(RampartActionType.ALLOW, LOG_MESSAGE, RampartSeverity.HIGH,
                RampartBoolean.FALSE, null);
        RampartAction right = new RampartActionImpl(RampartActionType.ALLOW, LOG_MESSAGE, RampartSeverity.HIGH,
                RampartBoolean.TRUE, null);
        assertThat(left, not(equalTo(right)));
    }


    @Test
    public void equals_typeMatters() {
        RampartAction left = new RampartActionImpl(RampartActionType.ALLOW,
                LOG_MESSAGE, RampartSeverity.HIGH, RampartBoolean.FALSE, null);
        RampartAction right = new RampartActionImpl(RampartActionType.DETECT,
                LOG_MESSAGE, RampartSeverity.HIGH, RampartBoolean.FALSE, null);
        assertThat(left, not(equalTo(right)));
    }

    @Test
    public void equals_messageMatters() {
        RampartAction left = new RampartActionImpl(RampartActionType.ALLOW, LOG_MESSAGE,
                RampartSeverity.HIGH, RampartBoolean.FALSE, null);
        RampartAction right = new RampartActionImpl(RampartActionType.ALLOW, newRampartString("7"),
                RampartSeverity.HIGH, RampartBoolean.FALSE, null);
        assertThat(left, not(equalTo(right)));
    }

    @Test
    public void equals_severityMatters() {
        RampartAction left = new RampartActionImpl(RampartActionType.ALLOW, LOG_MESSAGE, RampartSeverity.HIGH,
                RampartBoolean.FALSE, null);
        RampartAction right = new RampartActionImpl(RampartActionType.ALLOW, LOG_MESSAGE, RampartSeverity.LOW,
                RampartBoolean.FALSE, null);
        assertThat(left, not(equalTo(right)));
    }

}
