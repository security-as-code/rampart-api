package org.rampart.lang.api;

import static org.rampart.lang.api.utils.RampartUtils.newRampartConstant;
import static org.rampart.lang.api.utils.RampartUtils.newRampartNamedValue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class RampartNamedValueTest {

    private static final RampartConstant POTATOES = newRampartConstant("potatoes");
    private static final RampartConstant ONIONS = newRampartConstant("onions");
    private static final RampartObject MOCKED_OBJECT = mock(RampartObject.class);
    private static final RampartObject ANOTHER_MOCKED_OBJECT = mock(RampartObject.class);

    @Test
    public void equalsNoMatchWithDifferentType() {
        RampartNamedValue namedValue = newRampartNamedValue(POTATOES, MOCKED_OBJECT);

        assertThat(namedValue, not(equalTo(POTATOES)));
    }

    @Test
    public void equalsMatchesWithNullName() {
        RampartNamedValue namedValue1 = newRampartNamedValue(null, MOCKED_OBJECT);
        RampartNamedValue namedValue2 = newRampartNamedValue(null, MOCKED_OBJECT);

        assertThat(namedValue1, equalTo(namedValue2));
    }

    @Test
    public void equalsMatchesWithNullValue() {
        RampartNamedValue namedValue1 = newRampartNamedValue(POTATOES, null);
        RampartNamedValue namedValue2 = newRampartNamedValue(POTATOES, null);

        assertThat(namedValue1, equalTo(namedValue2));
    }

    @Test
    public void equalsNoMatchWithDifferentName() {
        RampartNamedValue namedValue1 =
                newRampartNamedValue(POTATOES, MOCKED_OBJECT);
        RampartNamedValue namedValue2 =
                newRampartNamedValue(ONIONS, MOCKED_OBJECT);

        assertThat(namedValue1, not(equalTo(namedValue2)));
    }

    @Test
    public void equalsNoMatchWithDifferentValues() {
        RampartNamedValue namedValue1 =
                newRampartNamedValue(POTATOES, MOCKED_OBJECT);
        RampartNamedValue namedValue2 =
                newRampartNamedValue(POTATOES, ANOTHER_MOCKED_OBJECT);

        assertThat(namedValue1, not(equalTo(namedValue2)));
    }

    @Test
    public void equalsOneNamedValueWithNullNameOtherNamedValueWithNonNullName() {
        RampartNamedValue namedValue1 =
                newRampartNamedValue(null, MOCKED_OBJECT);
        RampartNamedValue namedValue2 =
                newRampartNamedValue(POTATOES, MOCKED_OBJECT);

        assertThat(namedValue1, not(equalTo(namedValue2)));
    }

    @Test
    public void equalsOneNamedValueWithNullValueOtherNamedValueWithNonNullValue() {
        RampartNamedValue namedValue1 =
                newRampartNamedValue(POTATOES, MOCKED_OBJECT);
        RampartNamedValue namedValue2 =
                newRampartNamedValue(POTATOES, null);

        assertThat(namedValue1, not(equalTo(namedValue2)));
    }

}
