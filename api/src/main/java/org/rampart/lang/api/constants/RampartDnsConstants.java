package org.rampart.lang.api.constants;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.core.RampartRuleType;

public class RampartDnsConstants extends RampartGeneralConstants {

    public static final RampartConstant DNS_KEY = RampartRuleType.DNS.getName();

    public static final RampartConstant LOOKUP_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "lookup";
        }
    };
}
