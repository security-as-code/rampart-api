package org.rampart.lang.impl.http.validators.v1;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.http.RampartHttpValidationType;
import org.rampart.lang.api.http.matchers.RampartHttpMatcherType;
import org.rampart.lang.api.http.matchers.RampartHttpMethodMatcher;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;

@Deprecated
public class RampartHttpValidationValidator1_6 extends RampartHttpValidationValidatorUpTo1_5 {
    public RampartHttpValidationValidator1_6(RampartObject validateValues) {
        super(validateValues);
    }

    @Override
    public RampartHttpValidationType validateHttpValidationType() throws InvalidRampartRuleException {
        if (validateValues == null) {
            // not mandatory in 1.6
            return null;
        } else if (RampartInterpreterUtils.findFirstRampartString(newRampartList(METHOD_KEY, validateValues)) != null) {
            httpValidationType = RampartHttpValidationType.METHOD;
            return httpValidationType;
        }
        try {
            return super.validateHttpValidationType();
        } catch (InvalidRampartRuleException iare) {
            throw new InvalidRampartRuleException(
                    "http validate values must contain one, and only one, of: " +
                            newRampartList(VALID_INPUT_VALIDATION_TYPES).addAll(newRampartList(METHOD_KEY)));
        }
    }

    @Override
    void validateRampartHttpMatcherType(RampartString enforceValue) throws InvalidRampartRuleException {
        RampartHttpMatcherType type = RampartHttpMatcherType.fromRampartString(enforceValue);
        if (getHttpValidationType() != RampartHttpValidationType.METHOD) {
            // check super validator if it has any supported matcher
            super.validateRampartHttpMatcherType(enforceValue);
        } else if (getHttpValidationType() == RampartHttpValidationType.METHOD
                && !(type instanceof RampartHttpMethodMatcher)) {
            throw new InvalidRampartRuleException("\"" + enforceValue + "\" is an invalid enforcement type");
        }
    }

    @Override
    public RampartList validateHttpValidationValues() throws InvalidRampartRuleException {
        if (validateValues == null) {
            // not mandatory in 1.6
            return null;
        }
        if (getHttpValidationType() == RampartHttpValidationType.METHOD) {
            if (RampartInterpreterUtils.findRampartNamedValue(getHttpValidationType().getName(), validateValues) != null) {
                throw new InvalidRampartRuleException(
                        "http validation type \"" + METHOD_KEY + "\" must be a string literal");
            }
            validateEnforceValues();
            return (RampartList) validateValues;
        }
        return super.validateHttpValidationValues();
    }
}
