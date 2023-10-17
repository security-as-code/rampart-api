package org.rampart.lang.impl.sql;

import static org.rampart.lang.api.constants.RampartSqlConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.impl.utils.ObjectUtils;

public class RampartVendorType implements RampartObject {
    private final RampartConstant name;
    private final RampartList options;
    private final int hashCode;

    private RampartVendorType(RampartConstant name, RampartConstant... supportedOptions) {
        this.name = name;
        this.options = newRampartList(supportedOptions);
        this.hashCode = ObjectUtils.hash(name, options);
    }

    public static RampartVendorType ORACLE = new RampartVendorType(ORACLE_VENDOR_KEY);
    public static RampartVendorType MYSQL = new RampartVendorType(MYSQL_VENDOR_KEY,
            ANSI_QUOTES_KEY,
            NO_BACKSLACK_ESCAPES_KEY);
    public static RampartVendorType MARIA_DB = new RampartVendorType(MARIADB_VENDOR_KEY,
            ANSI_QUOTES_KEY,
            NO_BACKSLACK_ESCAPES_KEY);
    public static RampartVendorType SYBASE = new RampartVendorType(SYBASE_VENDOR_KEY,
            QUOTED_IDENTIFIERS_KEY);
    public static RampartVendorType MSSQL = new RampartVendorType(MSSQL_VENDOR_KEY,
            QUOTED_IDENTIFIERS_KEY);
    public static RampartVendorType DB2 = new RampartVendorType(DB2_VENDOR_KEY);
    public static RampartVendorType POSTGRES = new RampartVendorType(POSTGRES_VENDOR_KEY,
            STANDARD_CONFORMING_STRINGS_KEY,
            BACKSLASH_QUOTE_KEY);
    public static RampartVendorType ANY = new RampartVendorType(ANY_KEY);

    private static final RampartVendorType[] VALUES =
            new RampartVendorType[] {ORACLE, MYSQL, SYBASE, MSSQL, MARIA_DB, POSTGRES, DB2, ANY};

    public RampartConstant getName() {
        return name;
    }

    public static RampartVendorType valueOf(RampartConstant name) {
        for (RampartVendorType vendor : VALUES) {
            if (vendor.getName().equals(name)) {
                return vendor;
            }
        }
        return null;
    }

    public static RampartList getAllVendorTypes() {
        return newRampartList(VALUES);
    }

    public RampartList getSupportedOptions() {
        return options;
    }

    @Override
    public String toString() {
        return name.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartVendorType)) {
            return false;
        }
        RampartVendorType otherVendorType = (RampartVendorType) other;
        return ObjectUtils.equals(name, otherVendorType.name)
                && ObjectUtils.equals(options, otherVendorType.options);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
