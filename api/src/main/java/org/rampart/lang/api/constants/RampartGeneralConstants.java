package org.rampart.lang.api.constants;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.core.RampartActionAttribute;
import org.rampart.lang.api.core.RampartActionTarget;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.api.core.RampartInput;

/**
 * Class to store constant values common to all Rampart rules.
 */
public abstract class RampartGeneralConstants {

    //General constants
    public static final RampartConstant ANY_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "any";
        }
    };
    @Deprecated
    public static final RampartConstant ACTION_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "action";
        }
    };
    public static final RampartConstant CODE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "code";
        }
    };
    public static final RampartConstant LANGUAGE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "language";
        }
    };
    public static final RampartConstant SOURCE_CODE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "src";
        }
    };
    public static final RampartConstant IMPORT_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "import";
        }
    };
    public static final RampartConstant OS_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "os";
        }
    };
    public static final RampartConstant SEVERITY_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "severity";
        }
    };
    public static final RampartConstant MESSAGE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "message";
        }
    };
    // @since 2.3
    public static final RampartConstant STACKTRACE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "stacktrace";
        }
    };
    public static final RampartConstant VERSION_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "version";
        }
    };
    public static final RampartConstant OPTIONS_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "options";
        }
    };
    //@since 2.6
    public static final RampartConstant METADATA_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "metadata";
        }
    };
    public static final RampartConstant REQUIRES_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "requires";
        }
    };
    public static final RampartConstant LOG_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "log";
        }
    };
    public static final RampartConstant CWE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "cwe";
        }
    };
    public static final RampartConstant CVE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "cve";
        }
    };
    public static final RampartConstant DESCRIPTION_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "description";
        }
    };
    public static final RampartConstant CVSS_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "cvss";
        }
    };
    public static final RampartConstant SCORE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "score";
        }
    };
    public static final RampartConstant VECTOR_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "vector";
        }
    };
    public static final RampartConstant AFFECTED_OS_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "affected-os";
        }
    };
    public static final RampartConstant AFFECTED_PRODUCT_NAME_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "affected-product-name";
        }
    };
    public static final RampartConstant AFFECTED_PRODUCT_VERSION_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "affected-product-version";
        }
    };
    public static final RampartConstant RANGE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "range";
        }
    };
    public static final RampartConstant FROM_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "from";
        }
    };
    public static final RampartConstant TO_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "to";
        }
    };
    public static final RampartConstant CREATION_TIME_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "creation-time";
        }
    };

    // Action keys
    public static final RampartConstant ALLOW_KEY = RampartActionType.ALLOW.getName();
    public static final RampartConstant CORRECT_KEY = RampartActionType.CORRECT.getName();
    public static final RampartConstant DETECT_KEY = RampartActionType.DETECT.getName();
    public static final RampartConstant PROTECT_KEY = RampartActionType.PROTECT.getName();
    public static final RampartConstant HTTP_SESSION_KEY = RampartActionTarget.HTTP_SESSION.getName();
    public static final RampartConstant HTTP_RESPONSE_KEY = RampartActionTarget.HTTP_RESPONSE.getName();
    public static final RampartConstant REGENERATE_ID_KEY = RampartActionAttribute.REGENERATE_ID.getName();
    public static final RampartConstant SET_HEADER_KEY = RampartActionAttribute.SET_HEADER.getName();
    public static final RampartConstant NEW_RESPONSE_KEY = RampartActionAttribute.NEW_RESPONSE.getName();
    public static final RampartConstant CONNECTION_KEY = RampartActionTarget.CONNECTION.getName();
    public static final RampartConstant SECURE_KEY = RampartActionAttribute.SECURE.getName();
    public static final RampartConstant UPGRADE_TLS_KEY = RampartActionAttribute.UPGRADE_TLS.getName();

    // Supported Operating Systems
    public static final RampartConstant AIX_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "aix";
        }
    };
    public static final RampartConstant LINUX_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "linux";
        }
    };
    public static final RampartConstant SOLARIS_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "solaris";
        }
    };
    public static final RampartConstant WINDOWS_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "windows";
        }
    };

    // Supported Languages
    public static final RampartConstant JAVA_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "java";
        }
    };
    public static final RampartConstant CSHARP_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "csharp";
        }
    };
    public static final RampartConstant DOTNET_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "dotnet";
        }
    };
    public static final RampartConstant JAVASCRIPT_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "javascript";
        }
    };

    // input declaration keys
    public static final RampartConstant INPUT_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "input";
        }
    };
    public static final RampartConstant DATABASE_KEY = RampartInput.DATABASE.getName();
    public static final RampartConstant DESERIALIZATION_KEY = RampartInput.DESERIALIZATION.getName();
    public static final RampartConstant HTTP_KEY = RampartRuleType.HTTP.getName();

    // other shared keys
    public static final RampartConstant INJECTION_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "injection";
        }
    };
}
