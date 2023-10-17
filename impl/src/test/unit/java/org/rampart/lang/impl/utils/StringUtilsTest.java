package org.rampart.lang.impl.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import org.rampart.lang.impl.utils.StringUtils;

public class StringUtilsTest {

    @Test
    public void isBlank_nullRef() {
        assertThat(StringUtils.isBlank(null), equalTo(true));
    }

    @Test
    public void isBlank_emptyString() {
        assertThat(StringUtils.isBlank(""), equalTo(true));
    }

    @Test
    public void isBlank_onlyWhitespace() {
        assertThat(StringUtils.isBlank("   "), equalTo(true));
    }

    @Test
    public void isBlank_tabs() {
        assertThat(StringUtils.isBlank("\t"), equalTo(true));
    }

    @Test
    public void isBlank_newLine() {
        assertThat(StringUtils.isBlank("\n"), equalTo(true));
    }

    @Test
    public void isBlank_letters() {
        assertThat(StringUtils.isBlank("bob"), equalTo(false));
    }

    @Test
    public void isBlank_lettersAndWhitespace() {
        assertThat(StringUtils.isBlank("  bob  "), equalTo(false));
    }

    @Test
    public void isBlank_numbersAndWhitespace() {
        assertThat(StringUtils.isBlank("  323423  "), equalTo(false));
    }

}
