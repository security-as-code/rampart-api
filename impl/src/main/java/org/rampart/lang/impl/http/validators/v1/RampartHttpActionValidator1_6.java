package org.rampart.lang.impl.http.validators.v1;

import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.api.http.RampartHttpValidationType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

@Deprecated
public class RampartHttpActionValidator1_6 extends RampartHttpActionValidatorUpTo1_5 {
    public RampartHttpActionValidator1_6(RampartObject actionValuesList) {
        super(actionValuesList);
    }

    @Override
    public RampartActionType validateActionType(RampartString actionTypeString) throws InvalidRampartRuleException {
        if (httpValidationType == RampartHttpValidationType.METHOD) {
            validateHttpActionType(actionTypeString, HTTP_CSRF_AND_METHOD_VALIDATION_SUPPORTED_ACTION_TYPES);
        }
        return super.validateActionType(actionTypeString);
    }

}
