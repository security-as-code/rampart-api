package org.rampart.lang.api.constants;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.core.RampartRuleType;

public final class RampartSqlConstants extends RampartGeneralConstants {
    public static final RampartConstant SQL_KEY = RampartRuleType.SQL.getName();

    public static final RampartConstant VENDOR_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "vendor";
        }
    };

    public static final RampartConstant ORACLE_VENDOR_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "oracle";
        }
    };
    public static final RampartConstant SYBASE_VENDOR_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "sybase";
        }
    };
    public static final RampartConstant MYSQL_VENDOR_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "mysql";
        }
    };
    public static final RampartConstant MARIADB_VENDOR_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "mariadb";
        }
    };
    public static final RampartConstant MSSQL_VENDOR_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "mssql";
        }
    };
    public static final RampartConstant DB2_VENDOR_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "db2";
        }
    };
    public static final RampartConstant POSTGRES_VENDOR_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "postgres";
        }
    };
    public static final RampartConstant ANSI_QUOTES_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "ansi-quotes";
        }
    };
    public static final RampartConstant NO_BACKSLACK_ESCAPES_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "no-backslash-escapes";
        }
    };
    public static final RampartConstant QUOTED_IDENTIFIERS_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "quoted-identifiers";
        }
    };
    public static final RampartConstant STANDARD_CONFORMING_STRINGS_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "standard-conforming-strings";
        }
    };
    public static final RampartConstant BACKSLASH_QUOTE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "backslash-quote";
        }
    };

    public static final RampartConstant SUCCESSFUL_ATTEMPT_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "successful-attempt";
        }
    };
    public static final RampartConstant FAILED_ATTEMPT_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "failed-attempt";
        }
    };

    public static final RampartConstant QUERY_PROVIDED_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "query-provided";
        }
    };
    public static final RampartConstant PERMIT_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "permit";
        }
    };
}
