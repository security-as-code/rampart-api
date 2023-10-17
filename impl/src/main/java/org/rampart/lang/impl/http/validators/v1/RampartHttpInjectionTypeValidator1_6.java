package org.rampart.lang.impl.http.validators.v1;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.api.http.RampartHttpInjectionType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.java.RampartPrimitives;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;

/**
 * Validate the entirety of injection declaration in HTTP rules for 1.6 RampartApp versions.
 * This class is deprecated because there should be no more feature development in 1.x validators.
 */
@Deprecated
public class RampartHttpInjectionTypeValidator1_6 {
    private final RampartObject injectionTypeValues;

    public RampartHttpInjectionTypeValidator1_6(RampartObject injectionTypeValues) {
        this.injectionTypeValues = injectionTypeValues;
    }

    public RampartHttpInjectionType validateInjectionType() throws InvalidRampartRuleException {
        if (injectionTypeValues == null) {
            // not mandatory
            return null;
        } else if (!(injectionTypeValues instanceof RampartList)) {
            throw new InvalidRampartRuleException(INJECTION_KEY + " declaration must be followed by a list of values");
        }

        RampartList injectionTypeValuesList = (RampartList) injectionTypeValues;
        if (RampartPrimitives.toJavaInt(injectionTypeValuesList.size()) != 1) {
            throw new InvalidRampartRuleException("only a single parameter is allowed for \"" + INJECTION_KEY + "\" declaration");
        }
        RampartObject injectionTypeValue = injectionTypeValuesList.getFirst();
        if (!HEADERS_KEY.asRampartString().equals(injectionTypeValue)) {
            throw new InvalidRampartRuleException(
                    "only \"" + HEADERS_KEY + "\" string literal is supported for \"" + INJECTION_KEY + "\" declaration");
        }
        return RampartHttpInjectionType.HEADERS;
    }

    // This method should not be here as it's breaking the Single Responsibility Principle, but from how 1.x validators
    // are designed there's no way to add this to a specific structure validator class for 1.6 that would cross validate
    // http rule declarations (validate, injection) and actions, like in 2.x validators
    public void validateForActions(RampartAction rampartAction) throws InvalidRampartRuleException {
        if (injectionTypeValues != null
                && rampartAction != null
                && rampartAction.getActionType() != RampartActionType.DETECT
                && rampartAction.getActionType() != RampartActionType.PROTECT) {
            throw new InvalidRampartRuleException(
                    "action \"" + rampartAction.getActionType() + "\" is unsupported with \"" + INJECTION_KEY
                            + "\" declaration");
        }
    }
}
