package org.rampart.lang.impl.factory;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartVersion;
import org.rampart.lang.impl.core.RampartVersionImpl;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.impl.sanitization.validators.v2.RampartSanitizationValidator2_3;
import org.rampart.lang.impl.sanitization.validators.v2.RampartSanitizationValidator2_6;
import org.rampart.lang.impl.sanitization.validators.v2.RampartSanitizationValidator2_8;
import org.rampart.lang.impl.sanitization.validators.v2.RampartSanitizationValidator2_10;
import org.rampart.lang.java.builder.RampartSanitizationBuilder;

import java.util.Map;

public class RampartSanitizationFactory {

    public static Validatable<RampartSanitizationBuilder, InvalidRampartRuleException> createValidator(RampartVersion version,
                                                                                                       Map<String, RampartList> visitorSymbolTable) {
        if (version.isWithinRange(RampartVersionImpl.v2_3, RampartVersionImpl.v2_5) == RampartBoolean.TRUE) {
            return new RampartSanitizationValidator2_3(visitorSymbolTable);
        } else if (version.isWithinRange(RampartVersionImpl.v2_6, RampartVersionImpl.v2_7) == RampartBoolean.TRUE) {
            return new RampartSanitizationValidator2_6(visitorSymbolTable);
        } else if (version.isWithinRange(RampartVersionImpl.v2_8, RampartVersionImpl.v2_9) == RampartBoolean.TRUE) {
            return new RampartSanitizationValidator2_8(visitorSymbolTable);
        } else if (version.greaterOrEqualThan(RampartVersionImpl.v2_10) == RampartBoolean.TRUE) {
            return new RampartSanitizationValidator2_10(visitorSymbolTable);
        }
        return null;
    }

}

