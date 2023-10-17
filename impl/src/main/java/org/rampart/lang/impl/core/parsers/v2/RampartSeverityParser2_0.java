package org.rampart.lang.impl.core.parsers.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.SEVERITY_KEY;
import static org.rampart.lang.java.RampartPrimitives.newRampartInteger;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartInteger;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartSeverity;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.java.RampartPrimitives;

/** Parser for RAMPART severity. */
public class RampartSeverityParser2_0 {
    private RampartSeverityParser2_0() {
        throw new UnsupportedOperationException();
    }

    /**
     * Parses an RampartSeverity object representing the severity specified.
     *
     * @param severityObject object representing the severity in the symbol table.
     * @return RampartSeverity instance representing the value specified. Returns default (UNKNOWN) severity if
     *   <code>null</code> is passed.
     * @throws InvalidRampartRuleException when severity is specified as an RampartInteger outside
     *         permitted bounds.
     */
    public static RampartSeverity parseSeverityValue(RampartObject severityObject) throws InvalidRampartRuleException {
        if (severityObject == null) {
            return RampartSeverity.UNKNOWN;
        }
        if (severityObject instanceof RampartConstant) {
            return RampartSeverity.fromConstant((RampartConstant) severityObject);
        }
        if (severityObject instanceof RampartInteger) {
            RampartInteger severityInteger = (RampartInteger) severityObject;
            if (severityInteger.isLessThan(newRampartInteger(0)) == RampartBoolean.TRUE
                    || severityInteger.isGreaterThan(newRampartInteger(10)) == RampartBoolean.TRUE) {
                throw new InvalidRampartRuleException("\"" + SEVERITY_KEY + "\" must be in the range of 0-10 (inclusive)");
            }
            return RampartPrimitives.toRampartSeverity(severityInteger);
        }
        throw new InvalidRampartRuleException("\"" + SEVERITY_KEY + "\" is malformed, must be an integer or a constant");
    }
}
