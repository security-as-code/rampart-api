package org.rampart.lang.impl.http.validators.v2;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.http.RampartHttpInjectionType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.RampartValidatorBase;
import org.rampart.lang.impl.core.validators.FirstClassRuleObjectValidator;
import org.rampart.lang.java.RampartPrimitives;

import java.util.Arrays;
import java.util.List;

import static org.rampart.lang.api.constants.RampartGeneralConstants.INJECTION_KEY;

public class RampartHttpInjectionTypeValidator2_2 extends RampartValidatorBase implements FirstClassRuleObjectValidator {
    private static final String INJECTION_DECLARATION = "\"" + INJECTION_KEY + "\" declaration";

    public RampartHttpInjectionTypeValidator2_2(RampartObject rampartObject) {
        super(rampartObject);
    }

    public List<RampartConstant> allowedKeys() {
        return Arrays.asList(INJECTION_KEY);
    }

    public RampartHttpInjectionType validateInjection() throws InvalidRampartRuleException {
        if (validatableObject == null) {
            return null;
        }
        RampartList injectionTypeList = validateIsRampartListOfNonEmptyEntries(INJECTION_DECLARATION);
        if (RampartPrimitives.toJavaInt(injectionTypeList.size()) > 1) {
            throw new InvalidRampartRuleException("only one parameter can be specified to the " + INJECTION_DECLARATION);
        }
        // safe to cast to RampartConstant since it was previously check by RampartValidatorBase
        RampartConstant injectionConstant = (RampartConstant) injectionTypeList.getFirst();
        RampartHttpInjectionType injectionType = RampartHttpInjectionType.valueOf(injectionConstant);
        if (injectionType == null) {
            throw new InvalidRampartRuleException(
                    "unrecognized parameter \"" + injectionConstant + "\" in the " + INJECTION_DECLARATION);
        }
        return injectionType;
    }

    protected void validateListEntry(RampartObject entry, String entryContext) throws InvalidRampartRuleException {
        if (!(entry instanceof RampartConstant)) {
            throw new InvalidRampartRuleException(entryContext + " list entries must be constants");
        }
    }
}
