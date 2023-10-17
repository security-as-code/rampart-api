package org.rampart.lang.impl.factory;

import static org.rampart.lang.api.constants.RampartGeneralConstants.REQUIRES_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.VERSION_KEY;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.java.InvalidRampartAppException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class RampartAppValidatorFactoryTest {
    private static final RampartList VALID_APP_VALUES = newRampartList(newRampartString("appname"));

    @Test
    public void requiresValuesUnsupportedHighAppVersionThrowsException() {
        String versionString = "RAMPART/8.1";
        HashMap<String, RampartList> appDeclarations = new HashMap<String, RampartList>() {
            {
                put(REQUIRES_KEY.toString(), newRampartList(newRampartNamedValue(VERSION_KEY, newRampartString(versionString))));
            }
        };

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> RampartAppValidatorFactory.createAppValidator(VALID_APP_VALUES, appDeclarations).validate());

        assertThat(thrown.getMessage(), startsWith("invalid version 8.1"));
    }

    @Test
    public void requiresValuesUnsupportedLowAppVersionThrowsException() {
        String versionString = "RAMPART/0.1";
        HashMap<String, RampartList> appDeclarations = new HashMap<String, RampartList>() {
            {
                put(REQUIRES_KEY.toString(), newRampartList(newRampartNamedValue(VERSION_KEY, newRampartString(versionString))));
            }
        };

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> RampartAppValidatorFactory.createAppValidator(VALID_APP_VALUES, appDeclarations).validate());

        assertThat(thrown.getMessage(), Matchers.startsWith("invalid version 0.1"));
    }

    @Test
    public void requiresValuesAppVersionNotAFloatThrowsException() {
        String versionString = "RAMPART/1.1.1";
        HashMap<String, RampartList> appDeclarations = new HashMap<String, RampartList>() {
            {
                put(REQUIRES_KEY.toString(), newRampartList(newRampartNamedValue(VERSION_KEY, newRampartString(versionString))));
            }
        };

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> RampartAppValidatorFactory.createAppValidator(VALID_APP_VALUES, appDeclarations).validate());

        assertThat(thrown.getCause(), instanceOf(NumberFormatException.class));
    }

    @Test
    public void requiresValuesAppVersionNoDotThrowsException() {
        String versionString = "RAMPART/1";
        HashMap<String, RampartList> appDeclarations = new HashMap<String, RampartList>() {
            {
                put(REQUIRES_KEY.toString(), newRampartList(newRampartNamedValue(VERSION_KEY, newRampartString(versionString))));
            }
        };

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> RampartAppValidatorFactory.createAppValidator(VALID_APP_VALUES, appDeclarations).validate());

        assertThat(thrown.getMessage(),
                equalTo("version is invalid - major and minor version must be separated by a dot"));
    }

    @Test
    public void requiresValuesAppVersionMissingThrowsException() {
        HashMap<String, RampartList> appDeclarations = new HashMap<String, RampartList>() {
            {
                put(REQUIRES_KEY.toString(), newRampartList(newRampartNamedValue(VERSION_KEY, RampartList.EMPTY)));
            }
        };

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> RampartAppValidatorFactory.createAppValidator(VALID_APP_VALUES, appDeclarations).validate());

        assertThat(thrown.getMessage(),
                equalTo("named value \"version\" is expected to be a string or a constant value."));
    }

    @Test
    public void requiresValuesInvalidAppVersionThrowsException() {
        String versionString = "invalid version string";
        HashMap<String, RampartList> appDeclarations = new HashMap<String, RampartList>() {
            {
                put(REQUIRES_KEY.toString(), newRampartList(newRampartNamedValue(VERSION_KEY, newRampartString(versionString))));
            }
        };

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> RampartAppValidatorFactory.createAppValidator(VALID_APP_VALUES, appDeclarations).validate());

        assertThat(thrown.getMessage(), Matchers.startsWith("version is invalid"));
    }
}
