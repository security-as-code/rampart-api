package org.rampart.lang.api.constants;

import org.rampart.lang.api.RampartConstant;

public class RampartSanitizationConstants {

    public static final RampartConstant UNDETERMINED_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "undetermined";
        }
    };

    public static final RampartConstant VALUES_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "values";
        }
    };
    public static final RampartConstant SAFE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "safe";
        }
    };
    public static final RampartConstant UNSAFE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "unsafe";
        }
    };

    public static final RampartConstant LOGGING_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "logging";
        }
    };
    public static final RampartConstant ON_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "on";
        }
    };
    public static final RampartConstant OFF_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "off";
        }
    };

    public static final RampartConstant IGNORE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "ignore";
        }
    };
    public static final RampartConstant URI_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "uri";
        }
    };
    public static final RampartConstant PAYLOAD_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "payload";
        }
    };
    public static final RampartConstant ATTRIBUTE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "attribute";
        }
    };
}
