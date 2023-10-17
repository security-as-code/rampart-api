package org.rampart.lang.impl.patch.validators;

import static org.rampart.lang.java.RampartPrimitives.newRampartInteger;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

public class RampartChecksumValidatorTest {
    @Test
    public void checksumListValidatesSuccessfully() throws InvalidRampartRuleException {
        RampartObject checksums = newRampartList(newRampartString("123456789abcdef"));
        new RampartChecksumValidator(checksums).validateChecksumValues();
    }

    @Test
    public void noChecksumsReturnsNull() throws InvalidRampartRuleException {
        RampartList nullChecksumList = new RampartChecksumValidator(null)
                .validateChecksumValues();
        assertThat(nullChecksumList, equalTo(null));
    }

    @Test
    public void checksumValueInvalidTypeThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartChecksumValidator(newRampartString("Invalid")).validateChecksumValues());

        assertThat(thrown.getMessage(), containsString("must be an RampartList and not"));
    }

    @Test
    public void emptyChecksumListThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartChecksumValidator(newRampartList()).validateChecksumValues());

        assertThat(thrown.getMessage(), endsWith("must be a non empty RampartList"));
    }

    @Test
    public void checksumValueTooSmallThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartChecksumValidator(newRampartList(newRampartString("abcdef"))).validateChecksumValues());

        assertThat(thrown.getMessage(), containsString("length is not in the supported range of "));
    }

    @Test
    public void checksumValueTooLargeThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartChecksumValidator(newRampartList(newRampartString(
                "abcdef0123456789abcdef0123456789abcdef0123456789ab")))
                    .validateChecksumValues());

        assertThat(thrown.getMessage(), containsString("length is not in the supported range of "));
    }

    @Test
    public void duplicateChecksumValueThrowsException() {
        String checksum = "abcdef0123456789";
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartChecksumValidator(newRampartList(
                newRampartString(checksum),
                newRampartString(checksum)))
        .validateChecksumValues());

        assertThat(thrown.getMessage(), endsWith("duplicate checksum [" + checksum + "]"));
    }

    @Test
    public void invalidChecksumValueTypeThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartChecksumValidator(newRampartList(newRampartInteger(2))).validateChecksumValues());

        assertThat(thrown.getMessage(), endsWith("checksum [2] is not a String"));
    }
}
