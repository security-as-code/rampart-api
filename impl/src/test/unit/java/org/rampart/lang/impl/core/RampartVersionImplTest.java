package org.rampart.lang.impl.core;

import org.rampart.lang.api.RampartBoolean;
import org.junit.jupiter.api.Test;
import org.rampart.lang.impl.core.RampartVersionImpl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class RampartVersionImplTest {
    @Test
    public void isWithinRange_versionOutsideRange() {
        assertThat(RampartVersionImpl.v1_3.isWithinRange(RampartVersionImpl.v2_0, RampartVersionImpl.v2_3),
                equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void isWithinRange_versionLowerBound() {
        assertThat(RampartVersionImpl.v2_0.isWithinRange(RampartVersionImpl.v2_0, RampartVersionImpl.v2_3),
                equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void isWithinRange_versionHigherBound() {
        assertThat(RampartVersionImpl.v2_3.isWithinRange(RampartVersionImpl.v2_0, RampartVersionImpl.v2_3),
                equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void isWithinRange_versionInsideRangeDifferentMajor() {
        assertThat(RampartVersionImpl.v2_0.isWithinRange(RampartVersionImpl.v1_3, RampartVersionImpl.v2_3),
                equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void isWithinRange_versionInsideRangeDifferentMinor() {
        assertThat(RampartVersionImpl.v2_2.isWithinRange(RampartVersionImpl.v2_0, RampartVersionImpl.v2_3),
                equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void isWithinRange_versionHigherThanRangeDifferentMinor() {
        assertThat(RampartVersionImpl.v2_4.isWithinRange(RampartVersionImpl.v2_0, RampartVersionImpl.v2_3),
                equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void isWithinRange_versionHigherThanRangeDifferentMajor() {
        assertThat(RampartVersionImpl.v2_4.isWithinRange(RampartVersionImpl.v1_3, RampartVersionImpl.v1_5),
                equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void greaterOrEqualThan_versionEqual() {
        assertThat(RampartVersionImpl.v2_2.greaterOrEqualThan(RampartVersionImpl.v2_2), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void greaterOrEqualThan_versionLowerThanOtherDifferentMinor() {
        assertThat(RampartVersionImpl.v2_0.greaterOrEqualThan(RampartVersionImpl.v2_2), equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void greaterOrEqualThan_versionHigherThanOtherDifferentMinor() {
        assertThat(RampartVersionImpl.v2_2.greaterOrEqualThan(RampartVersionImpl.v2_0), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void greaterOrEqualThan_versionLowerThanOtherDifferentMajor() {
        assertThat(RampartVersionImpl.v1_3.greaterOrEqualThan(RampartVersionImpl.v2_2), equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void greaterOrEqualThan_versionHigherThanOtherDifferentMajor() {
        assertThat(RampartVersionImpl.v2_2.greaterOrEqualThan(RampartVersionImpl.v1_3), equalTo(RampartBoolean.TRUE));
    }
}
