package org.rampart.lang.impl.http.validators.v1;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartConstant;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.api.http.RampartHttpValidationType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.v1.RampartActionValidatorUpTo1_6;

@Deprecated
public class RampartHttpActionValidatorUpTo1_5 extends RampartActionValidatorUpTo1_6 {
    static final RampartList HTTP_INPUT_VALIDATION_SUPPORTED_ACTION_TYPES =
            newRampartList(ALLOW_KEY, DETECT_KEY, PROTECT_KEY);
    static final RampartList HTTP_CSRF_AND_METHOD_VALIDATION_SUPPORTED_ACTION_TYPES =
            newRampartList(DETECT_KEY, PROTECT_KEY);

    RampartHttpValidationType httpValidationType;

    public RampartHttpActionValidatorUpTo1_5(RampartObject actionValuesList) {
        super(actionValuesList);
    }

    public RampartAction validateHttpAction(RampartHttpValidationType httpValidationType) throws InvalidRampartRuleException {
        this.httpValidationType = httpValidationType;
        return super.validateRampartAction();
    }

    @Override
    public RampartActionType validateActionType(RampartString actionTypeString) throws InvalidRampartRuleException {
        if (httpValidationType != null) {
            switch (httpValidationType) {
                case CSRF:
                    validateHttpActionType(actionTypeString, HTTP_CSRF_AND_METHOD_VALIDATION_SUPPORTED_ACTION_TYPES);
                    break;
                case HTTP_COOKIE:
                case HTTP_HEADER:
                case HTTP_PARAMETER:
                    validateHttpActionType(actionTypeString, HTTP_INPUT_VALIDATION_SUPPORTED_ACTION_TYPES);
                    break;
            }
        }
        return super.validateActionType(actionTypeString);
    }

    void validateHttpActionType(RampartString actionTypeString, RampartList supportedTypes) throws InvalidRampartRuleException {
        RampartString validationTypeName = httpValidationType.getName().asRampartString();
        if (supportedTypes.contains(newRampartConstant(actionTypeString.toString())) == RampartBoolean.FALSE) {
            throw new InvalidRampartRuleException("invalid Rampart http \"" +
                    validationTypeName + "\" action specified. Valid \"" + validationTypeName +
                    "\" actions are: " + supportedTypes);
        }
    }
}
