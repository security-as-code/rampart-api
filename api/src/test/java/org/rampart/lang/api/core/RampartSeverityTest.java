package org.rampart.lang.api.core;

import static org.rampart.lang.api.core.RampartSeverity.HIGH;
import static org.rampart.lang.api.core.RampartSeverity.LOW;
import static org.rampart.lang.api.core.RampartSeverity.MEDIUM;
import static org.rampart.lang.api.core.RampartSeverity.UNKNOWN;
import static org.rampart.lang.api.core.RampartSeverity.VERY_HIGH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;

import org.junit.jupiter.api.Test;

/**
 * NB: These trivial unit tests are in place to ensure the order of the
 * elements in RampartSeverity is not changed without cause.
 * @see RampartSeverity
 */
public class RampartSeverityTest {
    @Test
    public void veryHighLowerOrdinalValueThanHigh() {
        assertThat(VERY_HIGH.compareTo(HIGH), lessThan(0));
    }

    @Test
    public void highLowerOrdinalValueThanMed() {
        assertThat(HIGH.compareTo(MEDIUM), lessThan(0));
    }

    @Test
    public void medLowerOrdinalValueThanLow() {
        assertThat(MEDIUM.compareTo(LOW), lessThan(0));
    }

    @Test
    public void lowLowerOrdinalValueThanUnknown() {
        assertThat(LOW.compareTo(UNKNOWN), lessThan(0));
    }

    @Test
    public void veryHighLowerOrdinalValueMed() {
        assertThat(VERY_HIGH.compareTo(MEDIUM), lessThan(0));
    }

    @Test
    public void medLowerOrdinalValueUnknown() {
        assertThat(MEDIUM.compareTo(UNKNOWN), lessThan(0));
    }
}
