package org.rampart.lang.impl.core;

import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartActionAttribute;
import org.rampart.lang.api.core.RampartActionTarget;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.api.core.RampartSeverity;
import org.junit.jupiter.api.Test;

@SuppressWarnings("deprecation")
public class RampartActionWithAttributeImplTest {

    private static final RampartString LOG_MESSAGE = newRampartString("log message");
    private static final RampartString STACKTRACE_VALUE = newRampartString("full");
    public static final RampartList TEST_CONFIG_MAP = newRampartList(
            newRampartNamedValue(newRampartConstant("foo"), newRampartString("bar")));

    @Test
    public void toStringFormatBasedOnRampartActionImplNoStacktrace() {
        RampartAction action = new RampartActionImpl(
                RampartActionType.ALLOW, LOG_MESSAGE, RampartSeverity.HIGH, RampartBoolean.TRUE);
        RampartList configMap = newRampartList(newRampartNamedValue(newRampartConstant("foo"), newRampartString("bar")));
        RampartActionWithAttributeImpl rampartActionWithAttribute = new RampartActionWithAttributeImpl(
                action, RampartActionTarget.HTTP_RESPONSE, RampartActionAttribute.SET_HEADER, configMap
        );
        assertThat(rampartActionWithAttribute.toString(), equalTo(
                "allow(http-response: {set-header: {foo: \"bar\"}}," +
                        " message: \"log message\", severity: High)"));
    }

    @Test
    public void toStringFormatBasedOnRampartActionImplWithStacktrace() {
        RampartAction action = new RampartActionImpl(
                RampartActionType.ALLOW, LOG_MESSAGE, RampartSeverity.HIGH, RampartBoolean.TRUE, STACKTRACE_VALUE);
        RampartActionWithAttributeImpl rampartActionWithAttribute = new RampartActionWithAttributeImpl(
                action, RampartActionTarget.HTTP_RESPONSE, RampartActionAttribute.SET_HEADER, TEST_CONFIG_MAP
        );
        assertThat(rampartActionWithAttribute.toString(), equalTo(
                "allow(http-response: {set-header: {foo: \"bar\"}}," +
                        " message: \"log message\", severity: High, stacktrace: \"full\")"));
    }

}
