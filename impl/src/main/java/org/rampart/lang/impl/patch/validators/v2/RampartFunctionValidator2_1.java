package org.rampart.lang.impl.patch.validators.v2;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.patch.RampartPatchType;
import org.rampart.lang.api.constants.RampartPatchConstants;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.patch.validators.signatures.SignatureValidatorFactory;

public class RampartFunctionValidator2_1 extends RampartFunctionValidatorUpTo2_0 {

    public RampartFunctionValidator2_1(RampartObject functionObject) {
        super(functionObject);
    }

    @Override
    public RampartString validateFunctionString() {
        // calls diverted now to:
        //      RampartString validateFunctionString(RampartConstant language)
        return null;
    }

    public RampartString validateFunctionString(RampartConstant language) throws InvalidRampartRuleException {
        RampartString functionRampartString = super.validateFunctionString();
        if (RampartPatchConstants.JAVA_KEY.equals(language)) {
            SignatureValidatorFactory.createSignatureValidator(functionRampartString.toString(), RampartPatchType.CALL)
                    .validate();
        }
        return functionRampartString;
    }
}
