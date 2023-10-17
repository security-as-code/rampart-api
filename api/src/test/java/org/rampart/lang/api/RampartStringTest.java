package org.rampart.lang.api;

import static org.rampart.lang.api.utils.RampartUtils.newRampartConstant;
import static org.rampart.lang.api.utils.RampartUtils.newRampartString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class RampartStringTest {

    @Test
    public void equalsNoMatchWithDifferentType() {
        assertThat(newRampartString("potatoes"),
                not(equalTo(newRampartConstant("potatoes"))));
    }

    @Test
    public void equalsMatchWithNull() {
        assertThat(newRampartString(null), equalTo(newRampartString(null)));
    }

    @Test
    public void equalsNoMatchWithDifferentString() {
        assertThat(newRampartString("potatoes"),
                not(equalTo(newRampartString("onions"))));
    }

    @Test
    public void equalsOneStringWithNullOtherStringWithNonNull() {
        assertThat(newRampartString("potatoes"), not(equalTo(newRampartString(null))));
    }

    @Test
    public void equalsOneStringWithNonNullOtherStringWithNull() {
        assertThat(newRampartString(null),
                not(equalTo(newRampartString("potatoes"))));
    }

    @Test
    public void equalsMatchWithSameString() {
        assertThat(newRampartString("potatoes"),
                equalTo(newRampartString("potatoes")));
    }
}
