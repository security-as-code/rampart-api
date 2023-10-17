package org.rampart.lang.impl.patch.validators.v2;

import static org.rampart.lang.api.constants.RampartPatchConstants.*;

import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.RampartValidatorBase;

/**
 * Class to validate an Rampart function specifier
 * Eg.
 *  function("com/foo/bar.fn()V")
 */
public class RampartFunctionValidatorUpTo2_0 extends RampartValidatorBase {
    public RampartFunctionValidatorUpTo2_0(RampartObject functionObject) {
        super(functionObject);
    }

    /**
     * Ensures function declaration string is not null or empty
     * @throws InvalidRampartRuleException when function string is null or empty
     */
    @Deprecated
    public RampartString validateFunctionString() throws InvalidRampartRuleException {
        return validateIsNotEmptyString("\"" + FUNCTION_KEY + "\" declaration");
    }
}
