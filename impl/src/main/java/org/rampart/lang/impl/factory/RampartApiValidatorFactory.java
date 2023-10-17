package org.rampart.lang.impl.factory;

import java.util.Map;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartVersion;
import org.rampart.lang.impl.apiprotect.validators.RampartApiProtectValidator2_9;
import org.rampart.lang.impl.core.RampartVersionImpl;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.java.builder.RampartApiBuilder;

final class RampartApiValidatorFactory {
    private RampartApiValidatorFactory() {
        throw new UnsupportedOperationException();
    }


    static Validatable<RampartApiBuilder, InvalidRampartRuleException> createValidator(RampartVersion version, Map<String, RampartList> visitorSymbolTable) {
        if (version.greaterOrEqualThan(RampartVersionImpl.v2_9) == RampartBoolean.TRUE) {
            return new RampartApiProtectValidator2_9(visitorSymbolTable);
        }
        return null;
    }
}
