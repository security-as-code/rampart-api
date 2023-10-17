package org.rampart.lang.impl.apiprotect.parsers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;

import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.apiprotect.RampartApiFilter;
import org.rampart.lang.api.constants.RampartApiProtectConstants;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

/** Some tests for the RAMPART API filter parser. */
public class RampartApiFilterParserTest {

    @Test
    public void nullIsParsedAsNull() throws InvalidRampartRuleException {
        assertThat(RampartApiFilterParser.parse((RampartList) null), equalTo(null));
    }


    @Test
    public void anyIsParsedAsWildcard() throws InvalidRampartRuleException {
        final RampartApiFilter filter =
                RampartApiFilterParser.parse(newRampartList(RampartApiProtectConstants.ANY_KEY));
        assertThat(filter.onAllTargets(), equalTo(RampartBoolean.TRUE));
    }


    @Test
    public void singleStringIsParsed() throws InvalidRampartRuleException {
        final RampartList items = newRampartList(newRampartString("/api/v1"));
        final RampartApiFilter filter = RampartApiFilterParser.parse(items);
        assertThat(filter.onAllTargets(), equalTo(RampartBoolean.FALSE));
        assertThat(filter.getUrlPatterns(), equalTo(items));
    }


    @Test
    public void twoStringsAreParsed() throws InvalidRampartRuleException {
        final RampartList items = newRampartList(
                newRampartString("/api/v1"),
                newRampartString("/api/v2/*")
        );
        final RampartApiFilter filter = RampartApiFilterParser.parse(items);
        assertThat(filter.onAllTargets(), equalTo(RampartBoolean.FALSE));
        assertThat(filter.getUrlPatterns(), equalTo(items));
    }


    @Test
    public void emptyListFails() {
        assertFailsOn();
    }


    @Test
    public void twoAnysFails() {
        assertFailsOn(RampartApiProtectConstants.ANY_KEY, RampartApiProtectConstants.ANY_KEY);
    }


    @Test
    public void stringAndAnyFails() {
        assertFailsOn(newRampartString("/api/v1"), RampartApiProtectConstants.ANY_KEY);
    }

    @Test
    public void anyAndStringFails() {
        assertFailsOn(RampartApiProtectConstants.ANY_KEY, newRampartString("/api/v1"));
    }

    @Test
    public void twoWildcardsFails() {
        assertFailsOn(newRampartString("/api/v1"), newRampartString("/api/v1/*/*"));
    }

    @Test
    public void emptyStringFails() {
        assertFailsOn(newRampartString(""));
    }

    private void assertFailsOn(RampartObject... values) {
        assertThrows(InvalidRampartRuleException.class,
                () -> RampartApiFilterParser.parse(newRampartList(values)));
    }
}
