package org.rampart.lang.impl.http;

import static org.rampart.lang.api.core.RampartRuleType.HTTP;
import static org.rampart.lang.api.constants.RampartHttpConstants.*;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.api.http.*;
import org.rampart.lang.impl.core.RampartActionableRuleBase;
import org.rampart.lang.impl.utils.ObjectUtils;

/**
 * Class to model an Rampart Http rule
 * Eg.
 *  http("http request - input validation rule"):
 *      request()
 *      validate(parameter: ["name"], is : alphanumeric)
 *      protect(message: "log message", severity: 10)
 *  endhttp
 * @since 2.0
 */
public class RampartHttpImpl2_X extends RampartActionableRuleBase implements RampartHttp {
    private final RampartHttpFeaturePattern securityFeature;
    private final RampartList paths;
    private final RampartHttpIOType httpIOType;
    private final RampartList inputs;
    private final RampartHttpInputValidation inputValidationHandler;
    private final String toStringValue;
    private final int hashCode;
    private final RampartOpenRedirect openRedirect;
    private final RampartBoolean onAuthenticate;
    private final RampartCsrf csrf;
    private final RampartXss xss;
    private final RampartHttpInjectionType injectionType;

    public RampartHttpImpl2_X(RampartString appName, RampartString ruleName, RampartHttpFeaturePattern securityFeature, RampartList paths,
                              RampartHttpIOType httpIOType, RampartList inputs, RampartHttpInputValidation inputValidationHandler,
                              RampartOpenRedirect openRedirect, RampartBoolean onAuthenticate, RampartCsrf csrf, RampartXss xss,
                              RampartHttpInjectionType injectionType, RampartAction action, RampartList targetOSList, RampartMetadata metadata) {
        super(appName, ruleName, action, targetOSList, metadata);
        this.securityFeature = securityFeature;
        this.paths = paths;
        this.httpIOType = httpIOType;
        this.inputs = inputs;
        this.inputValidationHandler = inputValidationHandler;
        this.openRedirect = openRedirect;
        this.onAuthenticate = onAuthenticate;
        this.csrf = csrf;
        this.xss = xss;
        this.injectionType = injectionType;
        this.toStringValue = super.toString();
        this.hashCode = ObjectUtils.hash(securityFeature, paths, httpIOType, inputs, inputValidationHandler,
                openRedirect, onAuthenticate, csrf, xss, super.hashCode());
    }

    // @Override
    public RampartList getUriValues() {
        return paths;
    }

    // @Override
    public RampartHttpIOType getHttpIOType() {
        return httpIOType;
    }

    // @Override
    @Deprecated
    public RampartHttpValidationType getHttpValidationType() {
        return null;
    }

    // @Override
    @Deprecated
    public RampartList getValidationMap() {
        return null;
    }

    // @Override
    public RampartHttpInputValidation getInputValidation() {
        return inputValidationHandler;
    }

    // @Override
    public RampartBoolean isOpenRedirect() {
        return openRedirect != null ? RampartBoolean.TRUE : RampartBoolean.FALSE;
    }

    // @Override
    public RampartOpenRedirect getOpenRedirectConfiguration() {
        return openRedirect;
    }

    // @Override
    public RampartBoolean isAuthenticate() {
        return onAuthenticate;
    }

    // @Override
    public RampartCsrf getCsrfConfiguration() {
        return csrf;
    }

    // @Override
    public RampartXss getXssConfiguration() {
        return xss;
    }

    public RampartHttpInjectionType getInjectionType() {
        return injectionType;
    }

    // @Override
    public RampartList getDataInputs() {
        return inputs;
    }

    // @Override
    public RampartRuleType getRuleType() {
        return HTTP;
    }

    // @Override
    public RampartHttpFeaturePattern getSecurityFeature() {
        return securityFeature;
    }

    @Override
    public String toString() {
        return toStringValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartHttpImpl2_X)) {
            return false;
        }
        RampartHttpImpl2_X otherHttp = (RampartHttpImpl2_X) other;
        return ObjectUtils.equals(securityFeature, otherHttp.securityFeature)
                && ObjectUtils.equals(paths, otherHttp.paths)
                && ObjectUtils.equals(httpIOType, otherHttp.httpIOType)
                && ObjectUtils.equals(inputs, otherHttp.inputs)
                && ObjectUtils.equals(inputValidationHandler, otherHttp.inputValidationHandler)
                && ObjectUtils.equals(openRedirect, otherHttp.openRedirect)
                && ObjectUtils.equals(onAuthenticate, otherHttp.onAuthenticate)
                && ObjectUtils.equals(csrf, otherHttp.csrf)
                && ObjectUtils.equals(xss, otherHttp.xss)
                && super.equals(otherHttp);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    protected void appendRuleBody(StringBuilder builder) {
        builder.append('\t').append(httpIOType).append('(');
        if (paths.isEmpty() == RampartBoolean.FALSE) {
            builder.append("paths: ").append(paths);
        }
        builder.append(')').append(LINE_SEPARATOR);
        appendInput(builder);
        if (inputValidationHandler != null) {
            builder.append('\t').append(inputValidationHandler).append(LINE_SEPARATOR);
        } else if(openRedirect != null) {
            builder.append('\t').append(openRedirect).append(LINE_SEPARATOR);
        } else if(onAuthenticate == RampartBoolean.TRUE) {
            builder.append('\t').append(AUTHENTICATE_KEY).append('(').append(USER_KEY).append(')').append(LINE_SEPARATOR);
        } else if(xss != null) {
            builder.append('\t').append(xss).append(LINE_SEPARATOR);
        } else if (csrf != null) {
            builder.append('\t').append(csrf).append(LINE_SEPARATOR);
        } else if (injectionType != null) {
            builder.append('\t').append(INJECTION_KEY).append('(').append(injectionType).append(')').append(LINE_SEPARATOR);
        }
        super.appendRuleBody(builder);
    }

    private void appendInput(StringBuilder builder) {
        if (openRedirect == null && xss == null) {
            return;
        }
        builder.append('\t').append(INPUT_KEY).append('(');
        String delim = "";
        RampartObjectIterator it = inputs.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            builder.append(delim).append(it.next());
            delim = ", ";
        }
        builder.append(')').append(LINE_SEPARATOR);
    }
}
