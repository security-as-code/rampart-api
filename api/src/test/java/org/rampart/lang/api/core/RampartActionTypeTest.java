package org.rampart.lang.api.core;

import static org.rampart.lang.api.core.RampartActionType.*;
import static org.rampart.lang.api.utils.RampartUtils.newRampartConstant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

/**
 * NB: These trivial unit tests are in place to ensure the order of the
 * elements in RampartActionType is not changed without cause.
 * @see RampartActionType
 */
public class RampartActionTypeTest {
    @Test
    public void allowLowerOrdinalValueThanDetect() {
        assertThat(ALLOW.compareTo(DETECT), lessThan(0));
    }

    @Test
    public void protectLowerOrdinalValueThanAllow() {
        assertThat(PROTECT.compareTo(ALLOW), lessThan(0));
    }

    @Test
    public void amrrActionTypeFromUnknownValue() {
        assertThat(RampartActionType.fromConstant(newRampartConstant("some")), equalTo(UNKNOWN));
    }
}
