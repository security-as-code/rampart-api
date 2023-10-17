package org.rampart.lang.impl.http.validators.v2;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;

import org.rampart.lang.api.*;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.http.RampartHttpInputValidation;
import org.rampart.lang.api.http.RampartHttpValidationType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.http.RampartHttpInputValidationImpl;
import org.rampart.lang.java.RampartPrimitives;

public class HttpInputValidationValidator2_1 extends HttpInputValidationValidator2_0 {

    protected RampartList omitRules = null;

    public HttpInputValidationValidator2_1(RampartList validateParameters) {
        super(validateParameters);
    }

    private static RampartObject getOmitsValue(RampartNamedValue value) {
        return OMITS_KEY.equals(value.getName()) ? value.getRampartObject() : null;
    }

    public RampartHttpInputValidation validateHttpValidationValues() throws InvalidRampartRuleException {
        if (!findParameters()) {
            return null;
        }
        return new RampartHttpInputValidationImpl(inputValidationType, targets, builtInMatchers, regexPattern, omitRules);
    }

    @Override
    protected boolean isParameterRecognized(RampartNamedValue nameValuePair) throws InvalidRampartRuleException {
        RampartObject matchersObject;
        if ((matchersObject = getOmitsValue(nameValuePair)) != null) {
            checkNullForKey(omitRules, OMITS_KEY);
            omitRules = getOmitRules(matchersObject);
            return true;
        }
        return super.isParameterRecognized(nameValuePair);
    }

    @Override
    protected RampartHttpValidationType getValidationType(RampartNamedValue nameValuePair) {
        RampartHttpValidationType validationType = RampartHttpValidationType.fromConstant(nameValuePair.getName());
        if (validationType != null && validationType == RampartHttpValidationType.REQUEST) {
            return validationType;
        }
        return super.getValidationType(nameValuePair);
    }

    @Override
    protected RampartList lookForTargets(RampartNamedValue nameValuePair) throws InvalidRampartRuleException {
        if (inputValidationType == RampartHttpValidationType.REQUEST) {
            RampartList targets = validateListNonEmptyConstants(nameValuePair.getName(), nameValuePair.getRampartObject());
            if (RampartPrimitives.toJavaInt(targets.size()) != 1
                    || targets.contains(PATH_KEY) == RampartBoolean.FALSE) {
                throw new InvalidRampartRuleException(
                        "\"" + RampartHttpValidationType.REQUEST + "\" parameter only supports the target \"" + PATH_KEY
                                + "\"");
            }
            return targets;
        }
        return super.lookForTargets(nameValuePair);
    }

    @Override
    protected void crossValidate() throws InvalidRampartRuleException {
        if (inputValidationType == RampartHttpValidationType.COOKIES
                || inputValidationType == RampartHttpValidationType.HEADERS
                || inputValidationType == RampartHttpValidationType.PARAMETERS) {
            crossValidateForMatchers(inputValidationType, omitRules);
        } else if(inputValidationType == RampartHttpValidationType.REQUEST) {
            crossValidateForOmitRules(inputValidationType, builtInMatchers, regexPattern, omitRules);
        }
    }

    private void crossValidateForMatchers(RampartHttpValidationType validationType, RampartList omitRules) throws InvalidRampartRuleException {
        if (omitRules != null) {
            throw new InvalidRampartRuleException(
                    "parameter \"" + validationType + "\" does not support \"" + OMITS_KEY + "\" key");
        }
        super.crossValidate();
    }

    private void crossValidateForOmitRules(RampartHttpValidationType validationType, RampartList builtInMatchers,
                                           RampartString regexPattern, RampartList omitRules) throws InvalidRampartRuleException {
        if (builtInMatchers != null || regexPattern != null) {
            throw new InvalidRampartRuleException(
                    "parameter \"" + validationType + "\" does not support \"" + IS_KEY + "\" key");
        } else if (omitRules == null) {
            throw new InvalidRampartRuleException(
                    "\"" + VALIDATE_KEY + "\" must contain a key value pair with key \"" + OMITS_KEY + "\" key");
        }
    }

    private RampartList getOmitRules(RampartObject rampartObj) throws InvalidRampartRuleException {
        if (rampartObj instanceof RampartString) {
            return newRampartList(rampartObj);
        }
        if (rampartObj instanceof RampartList) {
            RampartObjectIterator it =
                    validateNonEmptyList("\"" + OMITS_KEY + "\" parameter", (RampartList) rampartObj).getObjectIterator();
            while (it.hasNext() == RampartBoolean.TRUE) {
                if (!(it.next() instanceof RampartString)) {
                    throw new InvalidRampartRuleException(
                            "entry of \"" + OMITS_KEY + "\" parameter list must be a string literal");
                }
            }
            return (RampartList) rampartObj;
        }
        throw new InvalidRampartRuleException("\"" + OMITS_KEY + "\" parameter value \"" + rampartObj
                + "\" must be a string literal or a list of string literals");
    }

    private static RampartList validateListNonEmptyConstants(RampartConstant key, RampartObject rampartObj)
            throws InvalidRampartRuleException {
        if (rampartObj instanceof RampartConstant) {
            return newRampartList(rampartObj);
        }
        if (rampartObj instanceof RampartList) {
            RampartList rampartList = validateNonEmptyList("\"" + key + "\" parameter", (RampartList) rampartObj);
            RampartObjectIterator it = rampartList.getObjectIterator();
            while (it.hasNext() == RampartBoolean.TRUE) {
                if (!(it.next() instanceof RampartConstant)) {
                    throw new InvalidRampartRuleException("\""+ key + "\" parameter must contain a list of constants");

                }
            }
            return rampartList;
        }
        throw new InvalidRampartRuleException("\"" + key + "\" parameter must contain a constant or a list of constants");
    }
}
