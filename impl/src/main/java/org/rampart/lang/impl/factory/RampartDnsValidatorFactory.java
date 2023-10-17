package org.rampart.lang.impl.factory;

import java.util.Map;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartVersion;
import org.rampart.lang.impl.core.RampartVersionImpl;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.java.builder.RampartDnsBuilder;
import org.rampart.lang.impl.dns.parsers.v2.RampartDnsParser2_1;
import org.rampart.lang.impl.dns.parsers.v2.RampartDnsParser2_3;
import org.rampart.lang.impl.dns.parsers.v2.RampartDnsParser2_6;
import org.rampart.lang.impl.dns.parsers.v2.RampartDnsParser2_9;

public class RampartDnsValidatorFactory {

    public static Validatable<RampartDnsBuilder, InvalidRampartRuleException> createValidator(RampartVersion version,
                                                                                              Map<String, RampartList> visitorSymbolTable) {
        if (version.isWithinRange(RampartVersionImpl.v2_1, RampartVersionImpl.v2_2) == RampartBoolean.TRUE) {
            return new RampartDnsParser2_1(visitorSymbolTable);
        } else if (version.isWithinRange(RampartVersionImpl.v2_3, RampartVersionImpl.v2_5) == RampartBoolean.TRUE) {
            return new RampartDnsParser2_3(visitorSymbolTable);
        } else if (version.isWithinRange(RampartVersionImpl.v2_6, RampartVersionImpl.v2_8) == RampartBoolean.TRUE) {
            return new RampartDnsParser2_6(visitorSymbolTable);
        } else if (version.greaterOrEqualThan(RampartVersionImpl.v2_9) == RampartBoolean.TRUE) {
            return new RampartDnsParser2_9(visitorSymbolTable);
        }
        return null;
    }
}
