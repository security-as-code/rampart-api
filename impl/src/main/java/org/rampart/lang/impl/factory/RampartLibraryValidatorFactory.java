package org.rampart.lang.impl.factory;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartVersion;
import org.rampart.lang.impl.core.RampartVersionImpl;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.java.parser.RampartSingleAppVisitor;
import org.rampart.lang.java.builder.RampartLibraryBuilder;
import org.rampart.lang.impl.library.parsers.v2.RampartLibraryParser2_0;
import org.rampart.lang.impl.library.parsers.v2.RampartLibraryParser2_3;
import org.rampart.lang.impl.library.parsers.v2.RampartLibraryParser2_6;

import java.util.Map;

/**
 * Factory class to create Rampart library validators
 */
class RampartLibraryValidatorFactory {
    private RampartLibraryValidatorFactory() {}

    /**
     * Returns appropriate validator based on Rampart version
     *
     * @param version rampart api version requested
     * @param visitorSymbolTable values returned from the RampartSingleAppVisitor
     * @return Validator appropriate for that version or null for an unsupported version
     * @see RampartSingleAppVisitor
     */
    static Validatable<RampartLibraryBuilder, InvalidRampartRuleException> createValidator(RampartVersion version,
                                                                                           Map<String, RampartList> visitorSymbolTable) {
        if (version.isWithinRange(RampartVersionImpl.v2_0, RampartVersionImpl.v2_2) == RampartBoolean.TRUE) {
            return new RampartLibraryParser2_0(visitorSymbolTable);
        } else if (version.isWithinRange(RampartVersionImpl.v2_3, RampartVersionImpl.v2_5) == RampartBoolean.TRUE) {
            return new RampartLibraryParser2_3(visitorSymbolTable);
        } else if (version.greaterOrEqualThan(RampartVersionImpl.v2_6) == RampartBoolean.TRUE) {
            return new RampartLibraryParser2_6(visitorSymbolTable);
        }
        return null;
    }
}
