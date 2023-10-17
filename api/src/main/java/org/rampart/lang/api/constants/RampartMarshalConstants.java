package org.rampart.lang.api.constants;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.core.RampartRuleType;

public class RampartMarshalConstants extends RampartGeneralConstants {
    public static final RampartConstant MARSHAL_KEY = RampartRuleType.MARSHAL.getName();

    public static final RampartConstant XML_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "xml";
        }
    };

    public static final RampartConstant DESERIALIZE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "deserialize";
        }
    };

    public static final RampartConstant RCE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "rce";
        }
    };

    public static final RampartConstant DOS_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "dos";
        }
    };

    public static final RampartConstant XXE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "xxe";
        }
    };

    public static final RampartConstant URI_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "uri";
        }
    };

    public static final RampartConstant REFERENCE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "reference";
        }
    };

    public static final RampartConstant REFERENCE_LIMIT_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "limit";
        }
    };

    public static final RampartConstant REFERENCE_EXPANSION_LIMIT_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "expansion-limit";
        }
    };

}
