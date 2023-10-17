package org.rampart.lang.impl.patch.validators.v2;

import java.util.Map;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.patch.RampartPatchType;
import org.rampart.lang.api.constants.RampartPatchConstants;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.patch.validators.signatures.SignatureValidatorFactory;

public class RampartLocationValidator2_1 extends RampartLocationValidatorUpTo2_0 {

    public RampartLocationValidator2_1(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
    }

    @Override
    public RampartObject validateLocationSpecifier(RampartPatchType rampartPatchType) {
        // calls diverted now to:
        //      RampartObject validateLocationSpecifier(RampartPatchType RampartPatchType, RampartConstant language)
        return null;
    }

    public RampartObject validateLocationSpecifier(RampartPatchType rampartPatchType, RampartConstant language)
            throws InvalidRampartRuleException {
        RampartObject parameter = super.validateLocationSpecifier(rampartPatchType);
        switch (rampartPatchType) {
            case ENTRY:
            case EXIT:
            case INSTRUCTION:
            case LINE:
                return parameter;
            default:
                if (!(parameter instanceof RampartString)) {
                    throw new IllegalStateException(
                            "super class must return an RampartString for patch type \"" + rampartPatchType
                                    + "\" and returned " + parameter.getClass().getSimpleName());
                }
                if (RampartPatchConstants.JAVA_KEY.equals(language)) {
                    SignatureValidatorFactory.createSignatureValidator(parameter.toString(), rampartPatchType).validate();
                }
                return parameter;
        }
    }
}
