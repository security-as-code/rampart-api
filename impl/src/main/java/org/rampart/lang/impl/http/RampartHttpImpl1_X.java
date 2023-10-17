package org.rampart.lang.impl.http;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.api.http.*;
import org.rampart.lang.impl.core.RampartActionableRuleBase;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;
import org.rampart.lang.impl.utils.ObjectUtils;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;

/**
 * Class to model an Rampart Http rule
 * Eg.
 *  http("http request - input validation rule"):
 *      request()
 *      validate(parameter: ["name"], is : ["alphanumeric"])
 *      action(protect: "log message", severity: 10)
 *  endhttp
 * @since 1.4
 */
@Deprecated
public class RampartHttpImpl1_X extends RampartActionableRuleBase implements RampartHttp {

    private final RampartHttpFeaturePattern securityFeature;
    private final RampartList uriValues;
    private final RampartHttpIOType httpIOType;
    private final RampartHttpValidationType validationType;
    private final RampartList validationMap;
    private final String toStringValue;
    private final int hashCode;
    private final RampartHttpInjectionType injectionType;

    public RampartHttpImpl1_X(RampartString appName, RampartString ruleName, RampartHttpFeaturePattern securityFeature, RampartList uriValues,
                              RampartHttpIOType httpIOType, RampartHttpValidationType validationType, RampartList validationMap,
                              RampartHttpInjectionType injectionType, RampartAction action, RampartList targetOSList) {
        super(appName, ruleName, action, targetOSList, null);
        this.securityFeature = securityFeature;
        this.uriValues = uriValues;
        this.httpIOType = httpIOType;
        this.validationType = validationType;
        this.validationMap = validationMap;
        this.injectionType = injectionType;
        this.toStringValue = super.toString();
        this.hashCode = ObjectUtils
                .hash(securityFeature, uriValues, httpIOType, validationType, validationMap, super.hashCode());
    }
    // @Override
    public RampartHttpFeaturePattern getSecurityFeature() {
        return securityFeature;
    }

    // @Override
    public RampartList getUriValues() {
        return uriValues;
    }

    // @Override
    public RampartHttpIOType getHttpIOType() {
        return httpIOType;
    }

    // @Override
    public RampartHttpValidationType getHttpValidationType() {
        return validationType;
    }

    // @Override
    public RampartList getValidationMap() {
        return validationMap;
    }

    // this is an RAMPART 2.X method
    // @Override
    public RampartHttpInputValidation getInputValidation() {
        return null;
    }

    // this is an RAMPART 2.X method
    // @Override
    public RampartBoolean isOpenRedirect() {
        return null;
    }

    // this is an RAMPART 2.X method
    // @Override
    public RampartOpenRedirect getOpenRedirectConfiguration() {
        return null;
    }

    // this is an RAMPART 2.X method
    // @Override
    public RampartBoolean isAuthenticate() {
        return null;
    }

    // this is an RAMPART 2.X method
    // @Override
    public RampartCsrf getCsrfConfiguration() {
        return null;
    }

    // this is an RAMPART 2.X method
    // @Override
    public RampartXss getXssConfiguration() {
        return null;
    }

    // @Override
    public RampartHttpInjectionType getInjectionType() {
        return injectionType;
    }

    // this is an RAMPART 2.X method
    // @Override
    public RampartList getDataInputs() {
        return null;
    }

    // @Override
    public RampartRuleType getRuleType() {
        return RampartRuleType.HTTP;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartHttpImpl1_X)) {
            return false;
        }
        RampartHttpImpl1_X otherHttp = (RampartHttpImpl1_X) other;
        return  ObjectUtils.equals(securityFeature, otherHttp.securityFeature)
                && ObjectUtils.equals(uriValues, otherHttp.uriValues)
                && ObjectUtils.equals(httpIOType, otherHttp.httpIOType)
                && ObjectUtils.equals(validationType, otherHttp.validationType)
                && ObjectUtils.equals(validationMap, otherHttp.validationMap)
                && super.equals(otherHttp);
    }

    @Override
    public String toString() {
        return toStringValue;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    protected void appendRuleBody(StringBuilder builder) {
        builder.append('\t').append(httpIOType.name().toLowerCase()).append('(');
        if (uriValues.isEmpty() == RampartBoolean.FALSE) {
            builder.append("uri: ").append(uriValues);
        }
        builder.append(')').append(LINE_SEPARATOR);
        if (securityFeature == RampartHttpFeaturePattern.INPUT_VALIDATION) {
            builder.append('\t').append(validateToStringUpto1_6()).append(LINE_SEPARATOR);
        } else if (securityFeature == RampartHttpFeaturePattern.RESPONSE_HEADER_INJECTION) {
            builder.append('\t').append(INJECTION_KEY).append('(')
                   .append(injectionType.getName().asRampartString().formatted()).append(')').append(LINE_SEPARATOR);
        }
        // FIXME: Change the way action is printed to the normal standard getAction(). Only this way
        // to pass unit tests for now
        builder.append('\t').append(actionToStringUpto1_6()).append(LINE_SEPARATOR);
    }

    private String validateToStringUpto1_6() {
        StringBuilder builder = new StringBuilder(VALIDATE_KEY.toString()).append('(');
        RampartList validationTypeValues =
                (RampartList) RampartInterpreterUtils.findRampartNamedValue(validationType.getName(), validationMap);
        if (validationTypeValues == null) {
            builder.append(validationType.getName().asRampartString().formatted());
        } else {
            builder.append(validationType.getName()).append(": ").append(validationTypeValues);
        }
        builder.append(", ");
        if (validationType == RampartHttpValidationType.CSRF) {
            RampartList hostsList = (RampartList) RampartInterpreterUtils.findRampartNamedValue(HOSTS_KEY, validationMap);
            if (hostsList != null) {
                builder.append(HOSTS_KEY).append(": ").append(hostsList);
            }
        } else { // COOKIE, HEADER, PARAMETER
            RampartList enforceList = (RampartList) RampartInterpreterUtils.findRampartNamedValue(ENFORCE_KEY, validationMap);
            builder.append(ENFORCE_KEY).append(": ").append(enforceList);
        }
        return builder.append(')').toString();
    }

    private String actionToStringUpto1_6() {
        RampartAction action = getAction();
        String message = (action.shouldLog() == RampartBoolean.FALSE ? "" : action.getLogMessage().formatted().toString());
        return ACTION_KEY + "(" + action.getActionType().getName() + ": " + message + ", " + SEVERITY_KEY + ": \""
                + action.getSeverity() + "\")";
    }
}
