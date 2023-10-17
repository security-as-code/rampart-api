package org.rampart.lang.impl.factory;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartVersion;
import org.rampart.lang.impl.core.RampartVersionImpl;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.java.builder.RampartHttpBuilder;
import org.rampart.lang.impl.http.validators.v1.RampartHttpValidator1_4;
import org.rampart.lang.impl.http.validators.v1.RampartHttpValidator1_5;
import org.rampart.lang.impl.http.validators.v1.RampartHttpValidator1_6;
import org.rampart.lang.java.parser.RampartSingleAppVisitor;
import org.rampart.lang.impl.http.validators.v2.*;

import java.util.Map;

/**
 * Factory class to create Rampart http validators
 *
 * @since 1.4
 */
class RampartHttpValidatorFactory {
    private RampartHttpValidatorFactory() {}

    /**
     * Returns appropriate validator based on Rampart version
     *
     * @param version rampart api version requested
     * @param visitorSymbolTable values returned from the RampartSingleAppVisitor
     * @return Validator appropriate for that version or null for an unsupported version
     * @see RampartSingleAppVisitor
     */
    static Validatable<RampartHttpBuilder, InvalidRampartRuleException> createValidator(
            RampartVersion version, Map<String, RampartList> visitorSymbolTable) {
        if (version.equals(RampartVersionImpl.v1_4)) {
            return new RampartHttpValidator1_4(visitorSymbolTable);
        } else if (version == RampartVersionImpl.v1_5) {
            return new RampartHttpValidator1_5(visitorSymbolTable);
        } else if (version.greaterOrEqualThan(RampartVersionImpl.v1_6) == RampartBoolean.TRUE
                && version.greaterOrEqualThan(RampartVersionImpl.v2_0) == RampartBoolean.FALSE) {
            return new RampartHttpValidator1_6(visitorSymbolTable);
        } else if (version.equals(RampartVersionImpl.v2_0)) {
            return new RampartHttpValidator2_0(visitorSymbolTable);
        } else if (version.equals(RampartVersionImpl.v2_1)) {
            return new RampartHttpValidator2_1(visitorSymbolTable);
        } else if (version.equals(RampartVersionImpl.v2_2)) {
            return new RampartHttpValidator2_2(visitorSymbolTable);
        } else if (version.isWithinRange(RampartVersionImpl.v2_3, RampartVersionImpl.v2_4) == RampartBoolean.TRUE) {
            return new RampartHttpValidator2_3(visitorSymbolTable);
        } else if (version.equals(RampartVersionImpl.v2_5)) {
            return new RampartHttpValidator2_5(visitorSymbolTable);
        } else if (version.equals(RampartVersionImpl.v2_6)) {
            return new RampartHttpValidator2_6(visitorSymbolTable);
        } else if (version.greaterOrEqualThan(RampartVersionImpl.v2_7) == RampartBoolean.TRUE) {
            return new RampartHttpValidator2_7(visitorSymbolTable);
        }
        return null;
    }
}
