package org.rampart.lang.impl.http.validators.v2;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.rampart.lang.api.*;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.http.RampartHttpInputValidation;
import org.rampart.lang.api.http.matchers.RampartHttpMatcherType;
import org.rampart.lang.api.http.RampartHttpValidationType;
import org.rampart.lang.api.http.matchers.RampartPatternMatcher;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.FirstClassRuleObjectValidator;
import org.rampart.lang.impl.http.RampartHttpInputValidationImpl;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;

public class HttpInputValidationValidator2_0 implements FirstClassRuleObjectValidator {
    private static final EnumSet<RampartHttpValidationType> SUPPORTED_VALIDATION_TYPES = EnumSet
            .of(RampartHttpValidationType.HEADERS, RampartHttpValidationType.PARAMETERS, RampartHttpValidationType.COOKIES);

    private final RampartList validateParameters;
    protected RampartHttpValidationType inputValidationType = null;
    protected RampartList targets = null;
    protected RampartList builtInMatchers = null;
    protected RampartString regexPattern = null;

    public HttpInputValidationValidator2_0(RampartList validateParameters) {
        this.validateParameters = validateParameters;
    }

    private static RampartObject getMatchers(RampartNamedValue value) {
        return IS_KEY.equals(value.getName()) ? value.getRampartObject() : null;
    }

    protected static RampartList validateNonEmptyList(String context, RampartList list) throws InvalidRampartRuleException {
        if (list.isEmpty() == RampartBoolean.TRUE) {
            throw new InvalidRampartRuleException(context + " must have a non empty list of values");
        }
        return list;
    }

    /**
     * Validates all parameters of the validate declaration, e.g.:
     *
     * validate(parameter: ["blabla"], is: ["integer"])
     *
     * @return an object that models the validate declaration as an API to get the data from,
     *         programmatically
     * @throws InvalidRampartRuleException
     */
    public RampartHttpInputValidation validateHttpValidationValues() throws InvalidRampartRuleException {
        if (!findParameters()) {
            return null;
        }
        return new RampartHttpInputValidationImpl(inputValidationType, targets, builtInMatchers, regexPattern);
    }

    boolean findParameters() throws InvalidRampartRuleException {
        if (validateParameters == null) {
            // Only mandatory during cross validation
            return false;
        }
        RampartObjectIterator it = validateNonEmptyList("\"" + VALIDATE_KEY + "\" declaratation", validateParameters).getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject parameter = it.next();
            if (parameter instanceof RampartNamedValue) {
                if (!isParameterRecognized((RampartNamedValue) parameter)) {
                    throw new InvalidRampartRuleException(
                            "unrecognized parameter \"" + parameter + "\" to the \"" + VALIDATE_KEY + "\" declaration");
                }
            } else if (parameter instanceof RampartConstant) {
                if (!isParameterRecognized((RampartConstant) parameter)) {
                    throw new InvalidRampartRuleException(
                            "unrecognized parameter \"" + parameter + "\" to the \"" + VALIDATE_KEY + "\" declaration");
                }
            } else {
                throw new InvalidRampartRuleException(
                        "invalid \"" + VALIDATE_KEY + "\" declaration parameter \"" + parameter + "\" - only constants and key value pairs are allowed");
            }
        }
        if (inputValidationType == null) {
            throw new InvalidRampartRuleException("\"" + VALIDATE_KEY
                    + "\" must contain at least one key value pair with any of the keys: \"cookies\", \"parameters\" and \"headers\"");
        }
        crossValidate();
        return true;
    }

    /**
     * Do some cross validation within the `validate` declaration as some combinations of parameters
     * might not be supported when used together or might not be valid if other parameters are
     * missing.
     *
     * @throws InvalidRampartRuleException
     */
    protected void crossValidate() throws InvalidRampartRuleException {
        if (builtInMatchers != null) {
            RampartObjectIterator it = builtInMatchers.getObjectIterator();
            while (it.hasNext() == RampartBoolean.TRUE) {
                RampartObject matcher = it.next();
                if (!(matcher instanceof RampartPatternMatcher)) {
                    throw new InvalidRampartRuleException(
                            "matcher \"" + matcher + "\" is incompatible with \"" + inputValidationType
                                    + "\" validation type");
                }
            }
        } else if (regexPattern == null) {
            throw new InvalidRampartRuleException(
                    "\"" + VALIDATE_KEY + "\" must contain a key value pair with key \"" + IS_KEY + "\"");
        }
    }

    /**
     * Check if the name value pair is a valid and recognized parameter to the `validate`
     * declaration.
     *
     * @param nameValuePair parameter to check if it's recognized and valid
     * @return true if parameter is recognized, false otherwise
     * @throws InvalidRampartRuleException exception is thrown if parameter is recognized but not valid
     */
    protected boolean isParameterRecognized(RampartNamedValue nameValuePair) throws InvalidRampartRuleException {
        RampartHttpValidationType validateType;
        RampartObject matchersObject;
        if ((validateType = getValidationType(nameValuePair)) != null) {
            checkNullForKey(inputValidationType, validateType.getName());
            inputValidationType = validateType;
            targets = lookForTargets(nameValuePair);
            return true;
        }
        if ((matchersObject = getMatchers(nameValuePair)) != null) {
            checkDuplicateKey(builtInMatchers, IS_KEY);
            regexPattern = isValidRegexPattern(RampartInterpreterUtils.findFirstRampartString(matchersObject));
            builtInMatchers = getBuiltInMatchers(matchersObject);
            return true;
        }
        return false;
    }

    /**
     * Check if the constant is a valid and recognized parameter to the `validate`
     * declaration. version 2.0 and 2.1 of RAMPART only allows for name value pairs so this always returns false
     *
     * @param constant parameter to check if it's recognized and valid
     * @return true if parameter is recognized, false otherwise
     * @throws InvalidRampartRuleException exception is thrown if parameter is recognized but not valid
     */
    protected boolean isParameterRecognized(RampartConstant constant) throws InvalidRampartRuleException {
        return false;
    }

    protected RampartHttpValidationType getValidationType(RampartNamedValue nameValuePair) {
        RampartHttpValidationType validationType = RampartHttpValidationType.fromConstant(nameValuePair.getName());
        if (SUPPORTED_VALIDATION_TYPES.contains(validationType)) {
            return validationType;
        }
        return null;
    }

    /**
     * Specifically look for validation targets within the `validate` declaration. There are other
     * versions of this validator that will look for different targets.
     *
     * @param nameValuePair parameter to look for targets
     * @return an RampartList of targets that could contain either RampartStrings or RampartConstants
     * @throws InvalidRampartRuleException
     */
    protected RampartList lookForTargets(RampartNamedValue nameValuePair) throws InvalidRampartRuleException {
        return validateListNonEmptyStringValues(nameValuePair.getName(), nameValuePair.getRampartObject());
    }

    private static RampartList getBuiltInMatchers(RampartObject rampartObj) throws InvalidRampartRuleException {
        if (rampartObj instanceof RampartString) {
            return RampartList.EMPTY;
        }
        if (rampartObj instanceof RampartConstant) {
            return newRampartList(validateBuiltInMatcher((RampartConstant) rampartObj));
        }
        if (rampartObj instanceof RampartList) {
            LinkedHashSet<RampartObject> matchers = new LinkedHashSet<RampartObject>();
            RampartObjectIterator it = validateNonEmptyList("\""+ IS_KEY + "\" parameter", (RampartList) rampartObj).getObjectIterator();
            while (it.hasNext() == RampartBoolean.TRUE) {
                RampartObject entry = it.next();
                if (entry instanceof RampartConstant) {
                    if (!matchers.add(validateBuiltInMatcher((RampartConstant) entry))) {
                        throw new InvalidRampartRuleException(
                                "duplicate entry \"" + entry + "\" to matchers in \"" + IS_KEY + "\" parameter");
                    }
                } else if (!(entry instanceof RampartString)) {
                    throw new InvalidRampartRuleException(
                            "\"" + IS_KEY + "\" parameter can only contain strings and constant values");
                }
            }
            return newRampartList(matchers.toArray(new RampartObject[matchers.size()]));
        }
        throw new InvalidRampartRuleException("\"" + rampartObj+ "\" must be a string literal or a list of string literals");
    }

    private static RampartHttpMatcherType validateBuiltInMatcher(RampartConstant entry) throws InvalidRampartRuleException {
        RampartHttpMatcherType matcher = RampartHttpMatcherType.fromConstant(entry);
        if (matcher == null) {
            throw new InvalidRampartRuleException("invalid matcher \"" + entry + "\" for \"" + IS_KEY + "\" parameter");
        }
        return matcher;
    }

    private static RampartList validateListNonEmptyStringValues(RampartConstant key, RampartObject rampartObj) throws InvalidRampartRuleException {
        if (rampartObj instanceof RampartString) {
            return newRampartList(validateNonEmptyStringValue(rampartObj));
        }
        if (rampartObj instanceof RampartList) {
            RampartList rampartList = validateNonEmptyList("\"" + key + "\" parameter", (RampartList) rampartObj);
            RampartObjectIterator it = rampartList.getObjectIterator();
            while (it.hasNext() == RampartBoolean.TRUE) {
                validateNonEmptyStringValue(it.next());
            }
            return rampartList;
        }
        throw new InvalidRampartRuleException("\"" + rampartObj + "\" must be a string literal or a list of string literals");
    }

    private static RampartString validateNonEmptyStringValue(RampartObject entry) throws InvalidRampartRuleException {
        if (!(entry instanceof RampartString)) {
            throw new InvalidRampartRuleException("list value must be a quoted string");
        }
        if (entry.toString().trim().length() == 0) {
            throw new InvalidRampartRuleException("list value must be a non empty string");
        }
        return (RampartString) entry;
    }

    /**
     * Helper method to validate whether a given String is a valid regex pattern
     *
     * @param regexPattern pattern to be validated
     * @return if pattern is a valid regex pattern
     * @throws InvalidRampartRuleException
     */
    private static RampartString isValidRegexPattern(RampartString regexPattern) throws InvalidRampartRuleException {
        if (regexPattern == null) {
            return null;
        }
        try {
            Pattern.compile(regexPattern.toString());
            return regexPattern;
        } catch (PatternSyntaxException pse) {
            throw new InvalidRampartRuleException("\"" + regexPattern + "\" is an invalid regex matcher", pse);
        }
    }

    // @Override
    public List<RampartConstant> allowedKeys() {
        return Arrays.asList(VALIDATE_KEY);
    }

    static void checkNullForKey(RampartObject rampartObj, RampartConstant key) throws InvalidRampartRuleException {
        if (rampartObj != null) {
            throw new InvalidRampartRuleException(
                    "\"" + rampartObj + "\" and \"" + key + "\" detected - a single validation type parameter is"
                            + " allowed for \"" + VALIDATE_KEY + "\" declaration");
        }
    }

    static void checkDuplicateKey(RampartObject rampartObj, RampartConstant key) throws InvalidRampartRuleException {
        if (rampartObj != null) {
            throw new InvalidRampartRuleException(
                    "duplicate parameter \"" + key + "\" detected for \"" + VALIDATE_KEY + "\" declaration");
        }
    }
}
