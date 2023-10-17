package org.rampart.lang.impl.core.validators.v2;

import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartVersion;
import org.junit.jupiter.api.Test;

import org.rampart.lang.api.constants.RampartGeneralConstants;
import org.rampart.lang.java.InvalidRampartAppException;

import java.util.Collections;
import java.util.HashMap;

public class RampartAppValidatorUpTo2_3Test {
    private static final RampartList VALID_APP_VALUES = newRampartList(newRampartString("appname"));
    private static final RampartVersion VALID_REQUIRES_VERSION = newRampartVersion(1, 1);

    @Test
    public void expectedValuesValidatesSuccessfully() {
        new RampartAppValidatorUpTo2_3(
                VALID_APP_VALUES,
                VALID_REQUIRES_VERSION,
                Collections.emptyMap())
        .validate();
    }

    @Test
    public void validateAppValuesTooManyArgsThrowsException() {
        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class, () -> new RampartAppValidatorUpTo2_3(
                newRampartList(newRampartString("appname"), newRampartString("extra argument")),
                VALID_REQUIRES_VERSION,
                Collections.emptyMap())
        .validate());

        assertThat(thrown.getMessage(), equalTo("invalid number of arguments passed to app constructor"));
    }

    @Test
    public void validateAppValuesIncorrectTypeThrowsException() {
        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class, () -> new RampartAppValidatorUpTo2_3(
                newRampartList(newRampartInteger(2)),
                VALID_REQUIRES_VERSION,
                Collections.emptyMap())
        .validate());

        assertThat(thrown.getMessage(), startsWith("app constructor argument is expected to be "));
    }

    @Test
    public void invalidTypeSpecifiedInAppThrowsException() {
        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class, () -> new RampartAppValidatorUpTo2_3(
                newRampartList(newRampartList(newRampartInteger(2))),
                VALID_REQUIRES_VERSION,
                Collections.emptyMap())
        .validate());

        assertThat(thrown.getMessage(), startsWith("app constructor argument is expected to be "));
    }

    @Test
    public void emptyAppNameThrowsException() {
        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class, () -> new RampartAppValidatorUpTo2_3(
                newRampartList(newRampartString("")),
                VALID_REQUIRES_VERSION,
                Collections.emptyMap())
        .validate());

        assertThat(thrown.getMessage(), equalTo("app name cannot be empty"));
    }

    @Test
    public void appDoesNotUnderstandOtherDeclarations() {
        HashMap<String, RampartList> appDeclarations = new HashMap<>();
        appDeclarations.put(RampartGeneralConstants.VERSION_KEY.toString(),
                newRampartList(newRampartFloat((float) 2.3)));
        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class, () -> new RampartAppValidatorUpTo2_3(
                VALID_APP_VALUES, VALID_REQUIRES_VERSION,
                appDeclarations)
        .validate());

        assertThat(thrown.getMessage(), equalTo("RAMPART language version does not support \"version\" declaration"));
    }
}
