package org.rampart.lang.impl.http.validators.v2;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;

import java.util.Arrays;
import java.util.List;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.RampartValidatorBase;
import org.rampart.lang.impl.core.validators.FirstClassRuleObjectValidator;
import org.rampart.lang.java.RampartPrimitives;

public class AuthenticateValidator extends RampartValidatorBase implements FirstClassRuleObjectValidator {

    public AuthenticateValidator(RampartObject validatableObject) {
        super(validatableObject);
    }

    public RampartBoolean validateAuthenticate() throws InvalidRampartRuleException {
        if (validatableObject == null) {
            return RampartBoolean.FALSE;
        }
        RampartList authenticateParams = validateIsRampartListOfNonEmptyEntries("\"" + AUTHENTICATE_KEY + "\" declaration");
        if (RampartPrimitives.toJavaInt(authenticateParams.size()) > 1
                || !authenticateParams.getFirst().equals(USER_KEY)) {
            throw new InvalidRampartRuleException("constant \"" + USER_KEY + "\" is the only valid parameter to the \""
                    + AUTHENTICATE_KEY + "\" declaration");
        }
        return RampartBoolean.TRUE;
    }

    @Override
    protected void validateListEntry(RampartObject entry, String entryContext) throws InvalidRampartRuleException {
        if (!(entry instanceof RampartConstant)) {
            throw new InvalidRampartRuleException(entryContext + " list entries must be constants");
        }
    }

    // @Override
    public List<RampartConstant> allowedKeys() {
        return Arrays.asList(AUTHENTICATE_KEY);
    }
}
