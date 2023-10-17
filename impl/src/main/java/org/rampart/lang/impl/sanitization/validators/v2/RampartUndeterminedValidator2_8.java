package org.rampart.lang.impl.sanitization.validators.v2;

import static org.rampart.lang.api.constants.RampartSanitizationConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartInteger;

import java.util.Map;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartInteger;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartNamedValue;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.sanitization.RampartUndetermined;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.sanitization.RampartUndeterminedImpl2_8;
import org.rampart.lang.impl.utils.Optional;

public class RampartUndeterminedValidator2_8 extends RampartUndeterminedValidator2_3 {

    protected static final RampartInteger MAX_PARAMS_COUNT =  newRampartInteger(2);

    public RampartUndeterminedValidator2_8(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
    }

    @Override
    public RampartUndetermined validate() throws InvalidRampartRuleException {
        RampartList undetermined = visitorSymbolTable.get(UNDETERMINED_KEY.toString());
        if (undetermined == null) {
            throw new InvalidRampartRuleException(
                    "missing mandatory \"" + UNDETERMINED_KEY + "\" declaration");
        }
        RampartInteger paramSize = undetermined.size();
        if (paramSize.isGreaterThan(MAX_PARAMS_COUNT).equals(RampartBoolean.TRUE)) {
            throw new InvalidRampartRuleException(
                    "Invalid Sanitization rule configuration."
                    + " Too many parameters."
                    + " Only expecting a key value pairs for " + VALUES_KEY + " and " + LOGGING_KEY);
        }
        RampartBoolean isSafe = RampartBoolean.TRUE;
        Optional<RampartBoolean> shouldLog = Optional.empty();

        RampartObjectIterator it = undetermined.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject prop = it.next();
            if (!(prop instanceof RampartNamedValue)) {
                throw new InvalidRampartRuleException(
                        "Invalid Sanitization rule configuration."
                        + " Only expecting a key value pairs for " + VALUES_KEY + " and " + LOGGING_KEY);
            }
            RampartNamedValue propNamedValue = (RampartNamedValue)prop;
            if (VALUES_KEY.equals(propNamedValue.getName())) {
                RampartObject propValue = ((RampartNamedValue) prop).getRampartObject();
                if (SAFE_KEY.equals(propValue)) {
                    isSafe = RampartBoolean.TRUE;
                    continue;
                }
                if (UNSAFE_KEY.equals(propValue)) {
                    isSafe = RampartBoolean.FALSE;
                    continue;
                }
                throw new InvalidRampartRuleException(
                        "\"" + VALUES_KEY + "\" declaration in sanitize rule only supports \""
                        + SAFE_KEY + "\" or \"" + UNSAFE_KEY + "\" constants as parameters");
            }
            if (LOGGING_KEY.equals(propNamedValue.getName())) {
                RampartObject propValue = ((RampartNamedValue) prop).getRampartObject();
                if (ON_KEY.equals(propValue)) {
                    shouldLog = Optional.of(RampartBoolean.TRUE);
                    continue;
                }
                if (OFF_KEY.equals(propValue)) {
                    shouldLog = Optional.of(RampartBoolean.FALSE);
                    continue;
                }
                throw new InvalidRampartRuleException(
                        "\"" + LOGGING_KEY + "\" declaration in sanitize rule only supports \""
                        + ON_KEY + "\" or \"" + OFF_KEY + "\" constants as parameters");
            }
        }
        if (isSafe == RampartBoolean.FALSE
                && shouldLog.isPresent()
                && shouldLog.get() == RampartBoolean.FALSE) {
            throw new InvalidRampartRuleException(
                    "Invalid Sanitization rule configuration."
                    + " Unsupported combination of parameters. "
                    + "\"" + LOGGING_KEY + "\" can not be \"" + OFF_KEY + "\""
                    + " when "
                    + "\"" + VALUES_KEY + "\" is \"" + UNSAFE_KEY + "\"");
        }
        return new RampartUndeterminedImpl2_8(isSafe, shouldLog);
    }

}
