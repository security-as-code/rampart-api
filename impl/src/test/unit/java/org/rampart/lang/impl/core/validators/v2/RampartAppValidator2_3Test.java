package org.rampart.lang.impl.core.validators.v2;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartVersion;
import org.rampart.lang.java.builder.RampartAppBuilder;
import org.rampart.lang.java.InvalidRampartAppException;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RampartAppValidator2_3Test {

    private static final RampartList VALID_APP_VALUES = newRampartList(newRampartString("appname"));
    private static final RampartVersion VALID_REQUIRES_VERSION = newRampartVersion(2, 3);

    @Test
    public void expectedValuesValidateSuccessfully() {
        new RampartAppValidator2_3(
                VALID_APP_VALUES,
                VALID_REQUIRES_VERSION,
                Collections.emptyMap())
        .validate();
    }

    @Test
    public void validateAppValuesTooManyArgsThrowsException() {
        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppValidator2_3(
                    newRampartList(newRampartString("appname"), newRampartString("extra argument")),
                    VALID_REQUIRES_VERSION,
                    Collections.emptyMap())
        .validate());

        assertThat(thrown.getMessage(), equalTo("invalid number of arguments passed to app constructor"));
    }

    @Test
    public void validateAppValuesIncorrectTypeThrowsException() {
        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppValidator2_3(
                    newRampartList(newRampartInteger(2)),
                    VALID_REQUIRES_VERSION,
                    Collections.emptyMap())
        .validate());

        assertThat(thrown.getMessage(), startsWith("app constructor argument is expected to be "));
    }

    @Test
    public void invalidTypeSpecifiedInAppThrowsException() {
        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppValidator2_3(newRampartList(
                    newRampartList(newRampartInteger(2))),
                    VALID_REQUIRES_VERSION,
                    Collections.emptyMap())
        .validate());

        assertThat(thrown.getMessage(), startsWith("app constructor argument is expected to be "));
    }

    @Test
    public void emptyAppNameThrowsException() {
        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppValidator2_3(
                    newRampartList(newRampartString("")),
                    VALID_REQUIRES_VERSION,
                    Collections.emptyMap())
        .validate());

        assertThat(thrown.getMessage(), equalTo("app name cannot be empty"));
    }

    @Test
    public void appDoesNotUnderstandOtherDeclarations() {
        HashMap<String, RampartList> otherDeclarations = new HashMap<>();
        otherDeclarations.put("undefined", RampartList.EMPTY);
        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppValidator2_3(
                    VALID_APP_VALUES,
                    VALID_REQUIRES_VERSION,
                    otherDeclarations).validate());

        assertThat(thrown.getMessage(), equalTo("only \"version\" declaration is allowed within the RAMPART app"));
    }

    @Test
    public void versionDeclarationWithInteger() {
        HashMap<String, RampartList> otherDeclarations = new HashMap<>();
        otherDeclarations.put(VERSION_KEY.toString(), newRampartList(newRampartInteger(2)));
        RampartAppBuilder builder = new RampartAppValidator2_3(
                VALID_APP_VALUES,
                VALID_REQUIRES_VERSION,
                otherDeclarations)
        .validate();

        assertThat(builder.getAppVersion(), equalTo(newRampartInteger(2)));
    }

    @Test
    public void versionDeclarationWithFloat() {
        HashMap<String, RampartList> otherDeclarations = new HashMap<>();
        otherDeclarations.put(VERSION_KEY.toString(), newRampartList(newRampartFloat((float) 2.3)));
        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppValidator2_3(
                    VALID_APP_VALUES,
                    VALID_REQUIRES_VERSION,
                    otherDeclarations)
        .validate());

        assertThat(thrown.getMessage(),
                equalTo("invalid \"version\" declaration - only a single version integer is allowed"));
    }

    @Test
    public void versionDeclarationWithRampartString() {
        HashMap<String, RampartList> otherDeclarations = new HashMap<>();
        otherDeclarations.put(VERSION_KEY.toString(), newRampartList(newRampartString("2.3")));
        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppValidator2_3(
                    VALID_APP_VALUES,
                    VALID_REQUIRES_VERSION,
                    otherDeclarations)
        .validate());

        assertThat(thrown.getMessage(),
                equalTo("invalid \"version\" declaration - only a single version integer is allowed"));
    }

    @Test
    public void versionDeclarationWithIntegerOtherParameter() {
        HashMap<String, RampartList> otherDeclarations = new HashMap<>();
        otherDeclarations.put(VERSION_KEY.toString(), newRampartList(
                newRampartInteger(2),
                newRampartNamedValue(
                        newRampartConstant("scheme"),
                        newRampartString("semver"))));
        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppValidator2_3(
                    VALID_APP_VALUES,
                    VALID_REQUIRES_VERSION,
                    otherDeclarations)
        .validate());

        assertThat(thrown.getMessage(), equalTo(
                "invalid \"version\" declaration - only a single version integer is allowed"));
    }

    @Test
    public void versionDeclarationWithNotValidFloat() {
        HashMap<String, RampartList> otherDeclarations = new HashMap<>();
        otherDeclarations.put(VERSION_KEY.toString(), newRampartList(newRampartInteger(-2)));
        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppValidator2_3(
                    VALID_APP_VALUES,
                    VALID_REQUIRES_VERSION,
                    otherDeclarations)
        .validate());

        assertThat(thrown.getMessage(), equalTo("version number in \"version\" declaration must be a positive integer"));
    }
}
