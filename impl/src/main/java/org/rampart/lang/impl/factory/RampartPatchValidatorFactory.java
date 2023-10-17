package org.rampart.lang.impl.factory;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartVersion;
import org.rampart.lang.impl.core.RampartVersionImpl;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.impl.patch.validators.v2.RampartPatchValidator2_6;
import org.rampart.lang.impl.patch.validators.v2.RampartPatchValidator2_8;
import org.rampart.lang.java.parser.RampartSingleAppVisitor;
import org.rampart.lang.java.builder.RampartPatchBuilder;
import org.rampart.lang.impl.patch.validators.v1.RampartPatchValidator1_1;
import org.rampart.lang.impl.patch.validators.v1.RampartPatchValidator1_2;
import org.rampart.lang.impl.patch.validators.v1.RampartPatchValidator1_3;
import org.rampart.lang.impl.patch.validators.v1.RampartPatchValidator1_5;
import org.rampart.lang.impl.patch.validators.v2.RampartPatchValidator2_1;
import org.rampart.lang.impl.patch.validators.v2.RampartPatchValidatorUpTo2_0;

import java.util.Map;

/**
 * Factory class to create Rampart patch validators
 *
 * @since 1.1
 */
class RampartPatchValidatorFactory {
    private RampartPatchValidatorFactory() {}

    /**
     * Returns appropriate validator based on Rampart version
     *
     * @param version rampart api version requested
     * @param visitorSymbolTable values returned from the RampartSingleAppVisitor
     * @return Validator appropriate for that version or null for an unsupported version
     * @see RampartSingleAppVisitor
     */
    static Validatable<RampartPatchBuilder, InvalidRampartRuleException> createValidator(RampartVersion version, Map<String, RampartList> visitorSymbolTable) {
        if (version.isWithinRange(RampartVersionImpl.v1_0, RampartVersionImpl.v1_1) == RampartBoolean.TRUE) {
            return new RampartPatchValidator1_1(visitorSymbolTable);
        } else if (version.equals(RampartVersionImpl.v1_2)) {
            return new RampartPatchValidator1_2(visitorSymbolTable);
        } else if (version.isWithinRange(RampartVersionImpl.v1_3, RampartVersionImpl.v1_4) == RampartBoolean.TRUE) { // No additions to patch @ 1.4
            return new RampartPatchValidator1_3(visitorSymbolTable);
        } else if (version.greaterOrEqualThan(RampartVersionImpl.v1_5) == RampartBoolean.TRUE
                && version.greaterOrEqualThan(RampartVersionImpl.v2_0) == RampartBoolean.FALSE) {
            return new RampartPatchValidator1_5(visitorSymbolTable);
        } else if (version.equals(RampartVersionImpl.v2_0)) {
            return new RampartPatchValidatorUpTo2_0(visitorSymbolTable);
        } else if (version.isWithinRange(RampartVersionImpl.v2_1, RampartVersionImpl.v2_5) == RampartBoolean.TRUE) {
            return new RampartPatchValidator2_1(visitorSymbolTable);
        } else if (version.isWithinRange(RampartVersionImpl.v2_6, RampartVersionImpl.v2_7) == RampartBoolean.TRUE) {
            return new RampartPatchValidator2_6(visitorSymbolTable);
        } else if (version.greaterOrEqualThan(RampartVersionImpl.v2_8) == RampartBoolean.TRUE) {
        return new RampartPatchValidator2_8(visitorSymbolTable);
    }
        return null;
    }
}
