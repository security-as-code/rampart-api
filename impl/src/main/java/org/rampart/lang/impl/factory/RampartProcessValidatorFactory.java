package org.rampart.lang.impl.factory;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartVersion;
import org.rampart.lang.impl.core.RampartVersionImpl;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.java.parser.RampartSingleAppVisitor;
import org.rampart.lang.java.builder.RampartProcessBuilder;
import org.rampart.lang.impl.process.parsers.v2.RampartProcessParser2_0;
import org.rampart.lang.impl.process.parsers.v2.RampartProcessParser2_3;
import org.rampart.lang.impl.process.parsers.v2.RampartProcessParser2_6;
import org.rampart.lang.impl.process.parsers.v2.RampartProcessParser2_9;

import java.util.Map;

/**
 * Factory class to create Rampart process validators
 */
class RampartProcessValidatorFactory {
    private RampartProcessValidatorFactory() {}

    /**
     * Returns appropriate validator based on Rampart version
     *
     * @param version rampart api version requested
     * @param visitorSymbolTable values returned from the RampartSingleAppVisitor
     * @return Validator appropriate for that version or null for an unsupported version
     * @see RampartSingleAppVisitor
     */
    static Validatable<RampartProcessBuilder, InvalidRampartRuleException> createValidator(RampartVersion version,
                                                                                           Map<String, RampartList> visitorSymbolTable) {
        if (version.isWithinRange(RampartVersionImpl.v2_0, RampartVersionImpl.v2_2) == RampartBoolean.TRUE) {
            return new RampartProcessParser2_0(visitorSymbolTable);
        } else if (version.isWithinRange(RampartVersionImpl.v2_3, RampartVersionImpl.v2_5) == RampartBoolean.TRUE) {
            return new RampartProcessParser2_3(visitorSymbolTable);
        } else if (version.isWithinRange(RampartVersionImpl.v2_6, RampartVersionImpl.v2_8) == RampartBoolean.TRUE) {
            return new RampartProcessParser2_6(visitorSymbolTable);
        } else if (version.greaterOrEqualThan(RampartVersionImpl.v2_9) == RampartBoolean.TRUE) {
            return new RampartProcessParser2_9(visitorSymbolTable);
        }
        return null;
    }
}
