package org.rampart.lang.impl.sql.validators.v2;

import static org.rampart.lang.api.constants.RampartSqlConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.sql.RampartVendor;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

public class RampartSqlVendorValidatorTest {

    private Map<String, RampartList> symbolTable;

    @BeforeEach
    public void setUp() {
        symbolTable = new HashMap<>();
    }

    @Test
    public void emptyVendor() {
        symbolTable.put(VENDOR_KEY.toString(), RampartList.EMPTY);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartSqlVendorValidator(symbolTable).validateVendor());

        assertThat(thrown.getMessage(), equalTo("vendor name not found"));
    }

    @Test
    public void noVendorSpecifiedDefaults() throws InvalidRampartRuleException {
        RampartVendor vendor = new RampartSqlVendorValidator(symbolTable).validateVendor();
        assertThat(vendor.getName(), equalTo(ANY_KEY));
        assertThat(vendor.getOptions(), equalTo(RampartList.EMPTY));
    }

    @Test
    public void anyVendor() throws InvalidRampartRuleException {
        symbolTable.put(VENDOR_KEY.toString(), newRampartList(ANY_KEY));

        RampartVendor vendor = new RampartSqlVendorValidator(symbolTable).validateVendor();
        assertThat(vendor.getName(), equalTo(ANY_KEY));
        assertThat(vendor.getOptions(), equalTo(RampartList.EMPTY));
    }

    @Test
    public void anyVendorNoSupportForOptions() {
        symbolTable.put(VENDOR_KEY.toString(), newRampartList(
                ANY_KEY,
                newRampartNamedValue(
                        OPTIONS_KEY,
                        newRampartList(ANSI_QUOTES_KEY))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartSqlVendorValidator(symbolTable).validateVendor());

        assertThat(thrown.getMessage(), equalTo("vendor \"any\" does not support any options"));
    }

    @Test
    public void unsupportedVendor() {
        symbolTable.put(VENDOR_KEY.toString(), newRampartList(
                newRampartConstant("mongoDB")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartSqlVendorValidator(symbolTable).validateVendor());

        assertThat(thrown.getMessage(), startsWith("vendor must be one of supported types"));
    }

    @Test
    public void unsupportedVendorWithOptions() {
        symbolTable.put(VENDOR_KEY.toString(), newRampartList(
                newRampartConstant("mongoDB"),
                newRampartNamedValue(
                        OPTIONS_KEY,
                        newRampartList(ANSI_QUOTES_KEY))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartSqlVendorValidator(symbolTable).validateVendor());

        assertThat(thrown.getMessage(), startsWith("vendor must be one of supported types"));
    }

    @Test
    public void supportedVendorWithOptions() throws InvalidRampartRuleException {
        symbolTable.put(VENDOR_KEY.toString(), newRampartList(
                MYSQL_VENDOR_KEY,
                newRampartNamedValue(
                        OPTIONS_KEY,
                        newRampartList(ANSI_QUOTES_KEY))));
        RampartVendor vendor = new RampartSqlVendorValidator(symbolTable).validateVendor();
        assertThat(vendor.getName(), equalTo(MYSQL_VENDOR_KEY));
        assertThat(vendor.getOptions(), equalTo(newRampartList(ANSI_QUOTES_KEY)));
    }

    @Test
    public void supportedVendorWithUnsupportedOptions() {
        symbolTable.put(VENDOR_KEY.toString(), newRampartList(
                ORACLE_VENDOR_KEY,
                newRampartNamedValue(
                        OPTIONS_KEY,
                        newRampartList(ANSI_QUOTES_KEY))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartSqlVendorValidator(symbolTable).validateVendor());

        assertThat(thrown.getMessage(), equalTo("vendor \"oracle\" does not support any options"));
    }

    @Test
    public void supportedVendorWithInvalidOptionTypes() {
        symbolTable.put(VENDOR_KEY.toString(), newRampartList(
                MYSQL_VENDOR_KEY,
                newRampartNamedValue(
                        OPTIONS_KEY,
                        newRampartList(newRampartString("ansi-quotes")))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartSqlVendorValidator(symbolTable).validateVendor());

        assertThat(thrown.getMessage(), equalTo("\"" + OPTIONS_KEY + "\" declaration list entries must be constants"));
    }

    @Test
    public void optionsAreOptional() throws InvalidRampartRuleException {
        symbolTable.put(VENDOR_KEY.toString(), newRampartList(ORACLE_VENDOR_KEY));
        RampartVendor vendor = new RampartSqlVendorValidator(symbolTable).validateVendor();
        assertThat(vendor.getName(), equalTo(ORACLE_VENDOR_KEY));
        assertThat(vendor.getOptions(), equalTo(RampartList.EMPTY));
    }

    @Test
    public void moreThanOneSupportedVendor() {
        symbolTable.put(VENDOR_KEY.toString(), newRampartList(ORACLE_VENDOR_KEY, MYSQL_VENDOR_KEY));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartSqlVendorValidator(symbolTable).validateVendor());

        assertThat(thrown.getMessage(), equalTo("invalid vendor \"mysql\" only one database vendor can be specified"));
    }

    @Test
    public void optionsWithNoVendor() {
        symbolTable.put(VENDOR_KEY.toString(), newRampartList(
                newRampartNamedValue(
                        OPTIONS_KEY,
                        newRampartList(NO_BACKSLACK_ESCAPES_KEY))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartSqlVendorValidator(symbolTable).validateVendor());

        assertThat(thrown.getMessage(), equalTo("vendor name not found"));
    }

    @Test
    public void invalidVendor() {
        symbolTable.put(VENDOR_KEY.toString(), newRampartList(newRampartInteger(1)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartSqlVendorValidator(symbolTable).validateVendor());

        assertThat(thrown.getMessage(), equalTo("vendor name not found"));
    }

    @Test
    public void invalidVendorWithValidVendor() {
        symbolTable.put(VENDOR_KEY.toString(), newRampartList(
                newRampartConstant("invalid-vendor"),
                ORACLE_VENDOR_KEY));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartSqlVendorValidator(symbolTable).validateVendor());

        assertThat(thrown.getMessage(), startsWith("vendor must be one of supported types"));
    }

    @Test
    public void vendorWithEmptyOptions() {
        symbolTable.put(VENDOR_KEY.toString(), newRampartList(
                ORACLE_VENDOR_KEY,
                newRampartNamedValue(
                        OPTIONS_KEY,
                        RampartList.EMPTY)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartSqlVendorValidator(symbolTable).validateVendor());

        assertThat(thrown.getMessage(),
                equalTo("\"" + OPTIONS_KEY + "\" declaration must be followed by a non empty list"));
    }

    @Test
    public void vendorWithOptionsOneInvalidType() {
        symbolTable.put(VENDOR_KEY.toString(), newRampartList(
                ORACLE_VENDOR_KEY,
                newRampartNamedValue(
                        OPTIONS_KEY,
                        newRampartList(
                                NO_BACKSLACK_ESCAPES_KEY,
                                newRampartString("ansi-quotes")))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartSqlVendorValidator(symbolTable).validateVendor());

        assertThat(thrown.getMessage(), equalTo("\"" + OPTIONS_KEY + "\" declaration list entries must be constants"));
    }

    @Test
    public void vendorWithInvalidOptionsFormat() {
        symbolTable.put(VENDOR_KEY.toString(), newRampartList(
                ORACLE_VENDOR_KEY,
                newRampartNamedValue(
                        OPTIONS_KEY,
                        newRampartInteger(1))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartSqlVendorValidator(symbolTable).validateVendor());

        assertThat(thrown.getMessage(),
                equalTo("\"" + OPTIONS_KEY + "\" declaration must be followed by a non empty list"));
    }

    @Test
    public void vendorWithOptionsAndUnsupportedOptionGiven() {
        symbolTable.put(VENDOR_KEY.toString(), newRampartList(
                SYBASE_VENDOR_KEY,
                newRampartNamedValue(
                        OPTIONS_KEY,
                        newRampartList(NO_BACKSLACK_ESCAPES_KEY))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartSqlVendorValidator(symbolTable).validateVendor());

        assertThat(thrown.getMessage(), startsWith("vendor \"sybase\" options must be one of"));
    }
}
