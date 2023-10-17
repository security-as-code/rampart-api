package org.rampart.lang.impl.http.validators.v1;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.api.http.matchers.RampartPatternMatcher.REGEX;
import static org.rampart.lang.java.RampartPrimitives.*;

import org.rampart.lang.api.*;
import org.rampart.lang.api.constants.RampartHttpConstants;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.http.matchers.RampartHttpMatcherType;
import org.rampart.lang.api.http.RampartHttpValidationType;
import org.rampart.lang.api.http.matchers.RampartPatternMatcher;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Class to validate a validate entry in Rampart Http rules
 * Eg.
 *      validate(parameter: ["name"], enforce: "alphanumeric")
 * OR
 *      validate(csrf: ["origins"], hosts: [ws28.rampart.lan])
 */
@Deprecated
public class RampartHttpValidationValidatorUpTo1_5 {
    private static final RampartList VALID_CSRF_PROTECTION_TYPES = newRampartList(ORIGINS_KEY);
    static final RampartConstant[] VALID_INPUT_VALIDATION_TYPES =
            new RampartConstant[] {COOKIE_KEY, CSRF_KEY, HEADER_KEY, PARAMETER_KEY};

    final RampartObject validateValues;
    RampartHttpValidationType httpValidationType;

    public RampartHttpValidationValidatorUpTo1_5(RampartObject validateValues) {
        this.validateValues = validateValues;
    }

    public RampartHttpValidationType validateHttpValidationType() throws InvalidRampartRuleException {
        httpValidationType = validateAndGetHttpValidationType();
        return httpValidationType;
    }

    /**
     * Ensures one, and only one, of the valid http validation types are present
     * @see RampartHttpConstants
     * @throws InvalidRampartRuleException when zero or more than 1 are present
     */
    private RampartHttpValidationType validateAndGetHttpValidationType() throws InvalidRampartRuleException {
        int count = 0;
        RampartConstant inputValidationKey = null;
        for (RampartConstant key : VALID_INPUT_VALIDATION_TYPES) {
            if (RampartInterpreterUtils.findRampartNamedValue(key, validateValues) != null) {
                count++;
                inputValidationKey = key;
            }
        }
        if (count != 1) {
            throw new InvalidRampartRuleException("http validate values must contain one, and only one, of: "
                    + Arrays.toString(VALID_INPUT_VALIDATION_TYPES));
        }
        return RampartHttpValidationType.fromConstant(inputValidationKey);
    }

    public RampartList validateHttpValidationValues() throws InvalidRampartRuleException {
        RampartObject inputValidationTargets = RampartInterpreterUtils
                .findRampartNamedValue(newRampartConstant(getHttpValidationType().getName().toString()), validateValues);

        if (!(inputValidationTargets instanceof RampartList)) {
            throw new InvalidRampartRuleException("a list of values must be specified after the http validation type");
        }
        RampartList inputValidationParametersList = (RampartList) inputValidationTargets;

        if (inputValidationParametersList.isEmpty() == RampartBoolean.TRUE) {
            throw new InvalidRampartRuleException("values list cannot be empty");
        }

        RampartObjectIterator it = inputValidationParametersList.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            validateListStringValue(it.next());
        }

        switch (getHttpValidationType()) {
            case HTTP_COOKIE:
            case HTTP_HEADER:
            case HTTP_PARAMETER:
                validateEnforceValues();
                break;
            case CSRF:
                validateCsrfProtectionValues(inputValidationParametersList);
                validateOptionalHostsValues();
                break;
        }
        return (RampartList) validateValues;
    }

    private static void validateCsrfProtectionValues(RampartList csrfValues) throws InvalidRampartRuleException {
        RampartObjectIterator it = csrfValues.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject csrfValue = it.next();
            if (VALID_CSRF_PROTECTION_TYPES.contains(newRampartConstant(csrfValue.toString())) == RampartBoolean.FALSE) {
               throw new InvalidRampartRuleException("\"" + csrfValue + "\" is an unsupported csrf protection type");
           }
       }
    }

    /**
     * Validates the given RampartObject is a quoted and non-empty String
     * @param parameter object to be tested
     * @throws InvalidRampartRuleException when the value is empty or not a quoted String
     */
    private void validateListStringValue(RampartObject parameter) throws InvalidRampartRuleException {
        if (!(parameter instanceof RampartString)) {
            throw new InvalidRampartRuleException("all list values must a quoted String");
        }
        RampartString parameterString = (RampartString)parameter;
        if (toJavaInt(parameterString.trim().length()) == 0) {
            throw new InvalidRampartRuleException("list value cannot be an empty String");
        }
    }

    /**
     * Validates the list of enforcement types specified in a rule
     * @throws InvalidRampartRuleException when enforce list is missing, empty or contains an invalid value
     */
    void validateEnforceValues() throws InvalidRampartRuleException {
        RampartObject enforceValues = RampartInterpreterUtils
                .findRampartNamedValue(ENFORCE_KEY, validateValues);
        if (!(enforceValues instanceof RampartList)) {
            throw new InvalidRampartRuleException("a list of values must be specified after"
                    + " the enforce keyword");
        }
        RampartList enforceValuesList = (RampartList) enforceValues;
        if (enforceValuesList.isEmpty() == RampartBoolean.TRUE) {
            throw new InvalidRampartRuleException("enforce value list cannot be empty");
        }
        RampartObjectIterator it = enforceValuesList.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            validateEnforceValue(it.next());
        }
    }

    /**
     * Ensures a valid enforce value was specified
     * Note: Although regex patterns are supported, it is invalid to supply a regex
     *  pattern containing only whitespace
     * @throws InvalidRampartRuleException if an invalid RampartEnforceType is specified
     */
    private void validateEnforceValue(RampartObject enforceValue) throws InvalidRampartRuleException {
        if (!(enforceValue instanceof RampartString)) {
            throw new InvalidRampartRuleException("enforce list entry value must be a quoted String");
        }

        String enforceValueString = enforceValue.toString();
        if (enforceValueString.trim().length() == 0) {
            throw new InvalidRampartRuleException("enforce list entry value must not be an empty String");
        }

        validateRampartHttpMatcherType((RampartString) enforceValue);
    }

    void validateRampartHttpMatcherType(RampartString enforceValue) throws InvalidRampartRuleException {
        RampartHttpMatcherType type = RampartHttpMatcherType.fromRampartString(enforceValue);
        if (!(type instanceof RampartPatternMatcher)) {
            throw new InvalidRampartRuleException("\"" + enforceValue + "\" is an invalid enforcement type");
        }
        if (type == REGEX) {
            isValidRegexPattern(enforceValue.toString());
        }
    }

    /**
     * Validates the `optional` hosts type in Csrf Protection
     * @throws InvalidRampartRuleException when a type other than a list follows the "hosts" keyword
     * or when an empty list follows it
     */
    private void validateOptionalHostsValues() throws InvalidRampartRuleException {
        RampartObject hostsValues = RampartInterpreterUtils.findRampartNamedValue(HOSTS_KEY, validateValues);
        if (hostsValues == null) {
            return; // hosts is an optional argument
        }

        if (!(hostsValues instanceof RampartList)) {
            throw new InvalidRampartRuleException("a list of values must be specified after"
                    + " the hosts keyword");
        }
        RampartList hostsValuesList = (RampartList) hostsValues;
        if (hostsValuesList.isEmpty() == RampartBoolean.TRUE) {
            throw new InvalidRampartRuleException("hosts value list cannot be empty");
        }

        RampartObjectIterator it = hostsValuesList.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            validateListStringValue(it.next());
        }
    }

    /**
     * Helper method to validate whether a given String is a valid regex pattern
     * @param regexPattern pattern to be validated
     * @throws InvalidRampartRuleException
     */
    private static void isValidRegexPattern(String regexPattern) throws InvalidRampartRuleException {
        try {
            Pattern.compile(regexPattern);
        } catch (PatternSyntaxException pse) {
            throw new InvalidRampartRuleException("\"" + regexPattern + "\" is an invalid enforcement type", pse);
        }
    }

    RampartHttpValidationType getHttpValidationType() {
        return httpValidationType;
    }
}
