package org.rampart.lang.impl.sql.validators.v2;

import static org.rampart.lang.api.constants.RampartSqlConstants.*;

import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.rampart.lang.api.RampartNamedValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.sql.RampartSqlInjectionType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

public class RampartSqlInjectionTypeValidatorTest {

    private Map<String, RampartList> symbolTable;
    private RampartSqlInjectionTypeValidator validator;

    @BeforeEach
    public void setUp() {
        symbolTable = new HashMap<>();
        validator = new RampartSqlInjectionTypeValidator(symbolTable);
    }

    @Test
    public void missingInjectionDeclaration() {
        assertThrows(InvalidRampartRuleException.class, () -> validator.validateInjectionType());
    }

    @Test
    public void emptyInjectionReturnsDefault() throws InvalidRampartRuleException {
        symbolTable.put(INJECTION_KEY.toString(), RampartList.EMPTY);
        RampartSqlInjectionType injectionType = validator.validateInjectionType();

        assertAll(() -> {
            assertThat(injectionType.onSuccessfulAttempt(), equalTo(RampartBoolean.TRUE));
            assertThat(injectionType.onFailedAttempt(), equalTo(RampartBoolean.FALSE));
        });
    }

    @Test
    public void validSuccessfulAttempts() throws InvalidRampartRuleException {
        symbolTable.put(INJECTION_KEY.toString(), newRampartList(FAILED_ATTEMPT_KEY));
        RampartSqlInjectionType injectionType = validator.validateInjectionType();

        assertAll(() -> {
            assertThat(injectionType.onSuccessfulAttempt(), equalTo(RampartBoolean.FALSE));
            assertThat(injectionType.onFailedAttempt(), equalTo(RampartBoolean.TRUE));
        });
    }

    @Test
    public void validFailedAttempts() throws InvalidRampartRuleException {
        symbolTable.put(INJECTION_KEY.toString(), newRampartList(SUCCESSFUL_ATTEMPT_KEY));
        RampartSqlInjectionType injectionType = validator.validateInjectionType();

        assertAll(() -> {
            assertThat(injectionType.onSuccessfulAttempt(), equalTo(RampartBoolean.TRUE));
            assertThat(injectionType.onFailedAttempt(), equalTo(RampartBoolean.FALSE));
        });
    }

    @Test
    public void validSuccessfulFailedAttempts() throws InvalidRampartRuleException {
        symbolTable.put(INJECTION_KEY.toString(), newRampartList(SUCCESSFUL_ATTEMPT_KEY, FAILED_ATTEMPT_KEY));
        RampartSqlInjectionType injectionType = validator.validateInjectionType();

        assertAll(() -> {
            assertThat(injectionType.onSuccessfulAttempt(), equalTo(RampartBoolean.TRUE));
            assertThat(injectionType.onFailedAttempt(), equalTo(RampartBoolean.TRUE));
        });
    }

    @Test
    public void invalidInjectionValidationType() {
        symbolTable.put(INJECTION_KEY.toString(), newRampartList(newRampartConstant("not-supported")));

        assertThrows(InvalidRampartRuleException.class, () -> validator.validateInjectionType());
    }

    @Test
    public void invalidInjectionWithSuccesfulAttempts() {

        symbolTable.put(INJECTION_KEY.toString(),
                newRampartList(newRampartConstant("not-supported"), SUCCESSFUL_ATTEMPT_KEY));

        assertThrows(InvalidRampartRuleException.class, () -> validator.validateInjectionType());
    }

    @Test
    public void invalidIntegerParameter() {
        symbolTable.put(INJECTION_KEY.toString(), newRampartList(newRampartInteger(1)));

        assertThrows(InvalidRampartRuleException.class, () -> validator.validateInjectionType());
    }

    @Test
    public void differentInjectionTypeParameter() {
        symbolTable.put(INJECTION_KEY.toString(),
                newRampartList(newRampartString("sucessful-attempt"), SUCCESSFUL_ATTEMPT_KEY));

        assertThrows(InvalidRampartRuleException.class, () -> validator.validateInjectionType());
    }

    @Test
    public void unknowInjectionValidationType() {
        symbolTable.put(INJECTION_KEY.toString(), newRampartList(
                newRampartInteger(1),
                SUCCESSFUL_ATTEMPT_KEY));

        assertThrows(InvalidRampartRuleException.class, () -> validator.validateInjectionType());
    }

    @Test
    public void permitQueryProvidedOptionDefaultInjectionType() throws InvalidRampartRuleException {
        symbolTable.put(INJECTION_KEY.toString(), newRampartList(
                newRampartNamedValue(PERMIT_KEY, QUERY_PROVIDED_KEY)));
        RampartSqlInjectionType injectionType = validator.validateInjectionType();

        assertAll(() -> {
            assertThat(injectionType.onSuccessfulAttempt(), equalTo(RampartBoolean.TRUE));
            assertThat(injectionType.onFailedAttempt(), equalTo(RampartBoolean.FALSE));
            RampartNamedValue config = injectionType.getConfigurationIterator().next();
            assertThat(config.getName(), equalTo(PERMIT_KEY));
            assertThat(config.getRampartObject(), equalTo(QUERY_PROVIDED_KEY));
        });
    }

    @Test
    public void permitQueryProvidedOptionFailedAttempts() throws InvalidRampartRuleException {
        symbolTable.put(INJECTION_KEY.toString(), newRampartList(
                FAILED_ATTEMPT_KEY,
                newRampartNamedValue(PERMIT_KEY, QUERY_PROVIDED_KEY)));
        RampartSqlInjectionType injectionType = validator.validateInjectionType();

        assertAll(() -> {
            assertThat(injectionType.onSuccessfulAttempt(), equalTo(RampartBoolean.FALSE));
            assertThat(injectionType.onFailedAttempt(), equalTo(RampartBoolean.TRUE));
            RampartNamedValue config = injectionType.getConfigurationIterator().next();
            assertThat(config.getName(), equalTo(PERMIT_KEY));
            assertThat(config.getRampartObject(), equalTo(QUERY_PROVIDED_KEY));
        });
    }

    @Test
    public void permitQueryProvidedOptionSuccessfulAndFailedAttempts() throws InvalidRampartRuleException {
        symbolTable.put(INJECTION_KEY.toString(), newRampartList(
                SUCCESSFUL_ATTEMPT_KEY,
                FAILED_ATTEMPT_KEY,
                newRampartNamedValue(PERMIT_KEY, QUERY_PROVIDED_KEY)));
        RampartSqlInjectionType injectionType = validator.validateInjectionType();

        assertAll(() -> {
            assertThat(injectionType.onSuccessfulAttempt(), equalTo(RampartBoolean.TRUE));
            assertThat(injectionType.onFailedAttempt(), equalTo(RampartBoolean.TRUE));
            RampartNamedValue config = injectionType.getConfigurationIterator().next();
            assertThat(config.getName(), equalTo(PERMIT_KEY));
            assertThat(config.getRampartObject(), equalTo(QUERY_PROVIDED_KEY));
        });
    }

    @Test
    public void permitQueryProvidedOptionUnknownInjectionType() {
        symbolTable.put(INJECTION_KEY.toString(), newRampartList(
                newRampartInteger(2),
                newRampartNamedValue(PERMIT_KEY, QUERY_PROVIDED_KEY)));

        assertThrows(InvalidRampartRuleException.class, () -> validator.validateInjectionType());
    }

    @Test
    public void noConfigurationsGiven() throws InvalidRampartRuleException {
        symbolTable.put(INJECTION_KEY.toString(), RampartList.EMPTY);
        RampartSqlInjectionType injectionType = validator.validateInjectionType();

        assertThat(injectionType.getConfigurationIterator().hasNext(), equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void invalidConfigurationWithPermitConfiguration() {
        symbolTable.put(INJECTION_KEY.toString(), newRampartList(
                newRampartNamedValue(newRampartConstant("unsupported"), newRampartConstant("unknown")),
                newRampartNamedValue(PERMIT_KEY, QUERY_PROVIDED_KEY)));

        assertThrows(InvalidRampartRuleException.class, () -> validator.validateInjectionType());
    }

    @Test
    public void unsupportedPermitConfiguration() {
        symbolTable.put(INJECTION_KEY.toString(), newRampartList(
                newRampartNamedValue(PERMIT_KEY, newRampartConstant("unsupported"))));

        assertThrows(InvalidRampartRuleException.class, () -> validator.validateInjectionType());
    }

    @Test
    public void invalidPermitConfiguration() {
        symbolTable.put(INJECTION_KEY.toString(), newRampartList(
                newRampartNamedValue(PERMIT_KEY, newRampartInteger(0))));

        assertThrows(InvalidRampartRuleException.class, () -> validator.validateInjectionType());
    }

}
