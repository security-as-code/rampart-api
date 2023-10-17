package org.rampart.lang.impl.patch.validators.v2;

import org.rampart.lang.api.*;
import org.rampart.lang.api.patch.RampartPatchType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;
import java.util.Map;

/**
 * Class to validate an Rampart location specifier
 * Eg.
 *  call("com/foo/bar.method()V") or entry()
 */
public class RampartLocationValidatorUpTo2_0 {
    private final Map<String, RampartList> visitorSymbolTable;

    public RampartLocationValidatorUpTo2_0(Map<String, RampartList> visitorSymbolTable) {
        this.visitorSymbolTable = visitorSymbolTable;
    }

    @Deprecated
    public RampartObject validateLocationSpecifier(RampartPatchType rampartPatchType) throws InvalidRampartRuleException {
        RampartList locationValues = visitorSymbolTable.get(rampartPatchType.getName().toString());
        return validateLocationSpecifierValues(rampartPatchType, locationValues);
    }

    /**
     * Validates the parameter according to the requested location specifier type
     * @param locationValue parameter passed with the type
     * @return parameter passed with the type
     * @throws InvalidRampartRuleException if the parameter passed is invalid
     */
    private RampartObject validateLocationSpecifierValues(RampartPatchType rampartPatchType, RampartList locationValue)
            throws InvalidRampartRuleException {
        RampartConstant locationKey = rampartPatchType.getName();
        switch (rampartPatchType) {
            case ENTRY:
            case EXIT:
                if (locationValue.isEmpty() == RampartBoolean.FALSE) {
                    throw new InvalidRampartRuleException(locationKey + " location must not contain any parameters");
                }
                return locationValue;
            case INSTRUCTION:
            case LINE:
                RampartInteger integerValue = RampartInterpreterUtils.findFirstRampartInteger(locationValue);
                if (integerValue == null) {
                    throw new InvalidRampartRuleException(
                            locationKey + " location must contain an integer as a parameter");
                }
                return integerValue;
            default:
                RampartString stringValue = RampartInterpreterUtils.findFirstRampartString(locationValue);
                if (stringValue == null) {
                    throw new InvalidRampartRuleException(locationKey + " location must contain a string as a parameter");
                }
                return stringValue;
        }
    }
}
