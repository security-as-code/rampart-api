package org.rampart.lang.java.builder;

import org.rampart.lang.api.sql.RampartVendor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.rampart.lang.api.constants.RampartSqlConstants.*;
import static org.rampart.lang.api.constants.RampartSqlConstants.NO_BACKSLACK_ESCAPES_KEY;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class RampartVendorBuilderTest {
    private RampartVendorBuilder builder;

    @BeforeEach
    public void beforeEach() {
        builder = new RampartVendorBuilder();
    }

    @Test
    public void createRampartVendorNoOptions() {
        RampartVendor vendor = builder.addVendor(ORACLE_VENDOR_KEY).createRampartObject();
        assertThat(vendor.toString(), equalTo("vendor(oracle)"));
    }

    @Test
    public void createRampartVendorWithOptions() {
        RampartVendor vendor = builder.addVendor(MYSQL_VENDOR_KEY)
                                   .addConfiguredOptions(
                                           newRampartList(ANSI_QUOTES_KEY, NO_BACKSLACK_ESCAPES_KEY))
                                   .createRampartObject();
        assertThat(vendor.toString(), equalTo("vendor(mysql, options: [ansi-quotes, no-backslash-escapes])"));
    }
}
