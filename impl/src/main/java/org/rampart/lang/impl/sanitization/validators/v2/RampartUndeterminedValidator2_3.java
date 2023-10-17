package org.rampart.lang.impl.sanitization.validators.v2;

import static org.rampart.lang.api.constants.RampartSanitizationConstants.SAFE_KEY;
import static org.rampart.lang.api.constants.RampartSanitizationConstants.UNDETERMINED_KEY;
import static org.rampart.lang.api.constants.RampartSanitizationConstants.UNSAFE_KEY;
import static org.rampart.lang.api.constants.RampartSanitizationConstants.VALUES_KEY;

import org.rampart.lang.api.*;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.sanitization.RampartUndetermined;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.FirstClassRuleObjectValidator;
import org.rampart.lang.impl.sanitization.RampartUndeterminedImpl;
import org.rampart.lang.java.RampartPrimitives;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RampartUndeterminedValidator2_3 implements FirstClassRuleObjectValidator {

    private static final RampartInteger MAX_IGNORE_PARAMS =
            RampartPrimitives.newRampartInteger(1);
    protected final Map<String, RampartList> visitorSymbolTable;

    public RampartUndeterminedValidator2_3(Map<String, RampartList> visitorSymbolTable) {
        this.visitorSymbolTable = visitorSymbolTable;
    }

    // @Override
    public List<RampartConstant> allowedKeys() {
        return Collections.singletonList(UNDETERMINED_KEY);
    }

    public RampartUndetermined validate() throws InvalidRampartRuleException {
        RampartList undetermined = visitorSymbolTable.get(UNDETERMINED_KEY.toString());
        if (undetermined == null) {
            throw new InvalidRampartRuleException(
                    "missing mandatory \"" + UNDETERMINED_KEY + "\" declaration");
        }
        RampartInteger paramSize = undetermined.size();
        if (paramSize.isGreaterThan(MAX_IGNORE_PARAMS).equals(RampartBoolean.TRUE)) {
            throw new InvalidRampartRuleException(
                    "Invalid Sanitization rule configuration."
                    + " Too many parameters. Only expecting a parameter for "
                    + VALUES_KEY);
        }
        RampartBoolean isSafe = isSafe(undetermined);
        return new RampartUndeterminedImpl(isSafe);
    }

    private RampartBoolean isSafe(RampartList undeterminedProps) throws InvalidRampartRuleException {
        RampartObjectIterator it = undeterminedProps.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject prop = it.next();
            if (prop instanceof RampartNamedValue
                && ((RampartNamedValue) prop).getName().equals(VALUES_KEY)) {
                RampartObject propValue = ((RampartNamedValue) prop).getRampartObject();
                if (SAFE_KEY.equals(propValue)) {
                    return RampartBoolean.TRUE;
                }
                if (UNSAFE_KEY.equals(propValue)) {
                    return RampartBoolean.FALSE;
                }
                throw new InvalidRampartRuleException(
                        "\"" + VALUES_KEY + "\" declaration in sanitize " +
                        "rule only supports \"" + SAFE_KEY + "\" or \"" +
                        UNSAFE_KEY + "\" constants as parameters");
            }
        }
        // Default is TRUE
        return RampartBoolean.TRUE;
    }

}
