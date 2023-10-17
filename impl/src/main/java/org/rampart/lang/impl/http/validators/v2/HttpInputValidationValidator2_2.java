package org.rampart.lang.impl.http.validators.v2;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.http.RampartHttpValidationType;
import org.rampart.lang.api.http.matchers.RampartHttpMethodMatcher;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;

public class HttpInputValidationValidator2_2 extends HttpInputValidationValidator2_1 {

    public HttpInputValidationValidator2_2(RampartList validateParameters) {
        super(validateParameters);
    }

    @Override
    protected boolean isParameterRecognized(RampartConstant constant) throws InvalidRampartRuleException {
        RampartHttpValidationType validateType = RampartHttpValidationType.fromConstant(constant);
        if (validateType == RampartHttpValidationType.METHOD) {
            checkNullForKey(inputValidationType, validateType.getName());
            inputValidationType = validateType;
            return true;
        }
        return false;
    }

    @Override
    protected void crossValidate() throws InvalidRampartRuleException {
        if (inputValidationType != RampartHttpValidationType.METHOD) {
            super.crossValidate();
            return;
        }
        if (omitRules != null) {
            throw new InvalidRampartRuleException(
                    "parameter \"" + inputValidationType + "\" does not support \"" + OMITS_KEY + "\"");
        } else if (builtInMatchers == null) {
            throw new InvalidRampartRuleException(
                    "\"" + VALIDATE_KEY + "\" declaration must contain a key value pair with key \"" + IS_KEY + "\"");
        } else if (regexPattern != null) {
            throw new InvalidRampartRuleException(
                    "regex patterns are incompatible with \"" + inputValidationType + "\" validation type");
        }
        RampartObjectIterator it = builtInMatchers.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject matcher = it.next();
            if (!(matcher instanceof RampartHttpMethodMatcher)) {
                throw new InvalidRampartRuleException(
                        "matcher \"" + matcher + "\" is incompatible with \"" + inputValidationType
                                + "\" validation type");
            }
        }
    }
}
