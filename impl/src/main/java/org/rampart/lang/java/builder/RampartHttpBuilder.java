package org.rampart.lang.java.builder;

import static org.rampart.lang.java.RampartPrimitives.newRampartList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartCode;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.core.RampartVersion;
import org.rampart.lang.api.http.*;
import org.rampart.lang.api.core.RampartInput;
import org.rampart.lang.impl.http.RampartHttpImpl1_X;
import org.rampart.lang.impl.http.RampartHttpImpl2_X;
import org.rampart.lang.impl.http.RampartOpenRedirectImpl;
import org.rampart.lang.impl.http.validators.v1.RampartHttpActionValidator1_6;
import org.rampart.lang.impl.http.validators.v1.RampartHttpActionValidatorUpTo1_5;
import org.rampart.lang.java.parser.RampartSingleAppVisitor;

import static org.rampart.lang.java.RampartPrimitives.newRampartInteger;

/**
 * Class used by the validators to build an RampartHttpImpl
 * @see RampartHttpImpl2_X
 * @see RampartSingleAppVisitor
 */
public class RampartHttpBuilder implements RampartRuleBuilder<RampartHttp> {
    private RampartString ruleName;
    private RampartList uriPaths = RampartList.EMPTY;
    private RampartHttpIOType httpIOType;
    private RampartHttpValidationType validationType;
    private RampartList validationMap = RampartList.EMPTY;
    private RampartHttpInputValidation inputValidation;
    private RampartOpenRedirect openRedirect;
    private RampartBoolean onAuthenticate = RampartBoolean.FALSE;
    private RampartCsrf csrf;
    private RampartXss xss;
    private RampartList inputs = RampartList.EMPTY;
    private RampartAction action;
    private RampartHttpInjectionType injectionType;
    private RampartList targetOSList;
    private RampartMetadata metadata;
    private final RampartVersion ruleVersion;

    public RampartHttpBuilder(RampartVersion ruleVersion) {
        this.ruleVersion = ruleVersion;
    }

    // @Override
    public RampartHttp createRampartRule(RampartString appName) {
        if (ruleVersion.getMajor().isLessThan(newRampartInteger(2)) == RampartBoolean.TRUE) {
            return new RampartHttpImpl1_X(appName, ruleName, getDeclaredSecurityFeatures().get(0), uriPaths, httpIOType,
                    validationType, validationMap, injectionType, action, targetOSList);
        }
        return new RampartHttpImpl2_X(appName,
                ruleName,
                getDeclaredSecurityFeatures().get(0),
                uriPaths,
                httpIOType,
                inputs.isEmpty() == RampartBoolean.TRUE ? newRampartList(RampartInput.HTTP) : inputs,
                inputValidation,
                openRedirect,
                onAuthenticate,
                csrf,
                xss,
                injectionType,
                action,
                targetOSList,
                metadata);
    }

    //@Override
    public RampartHttpBuilder addCode(RampartCode code) {
        // TODO: support to be added for code block in RAMPART-84
        return this;
    }

    //@Override
    public RampartHttpBuilder addRuleName(RampartString ruleName) {
        this.ruleName = ruleName;
        return this;
    }

    public RampartHttpBuilder addAction(RampartAction action) {
        this.action = action;
        return this;
    }

    public RampartAction getRampartAction() {
        return action;
    }

    /**
     * @param targetOSList non empty RampartList of RampartConstants
     * @return
     */
    //@Override
    public RampartHttpBuilder addTargetOSList(RampartList targetOSList) {
        this.targetOSList = targetOSList;
        return this;
    }

    public RampartRuleBuilder<RampartHttp> addMetadata(RampartMetadata metadata) {
        this.metadata = metadata;
        return this;
    }

    /**
     * @param uriPaths non empty RampartList of RampartStrings
     * @return
     */
    public RampartHttpBuilder addUriPaths(RampartList uriPaths) {
        this.uriPaths = uriPaths;
        return this;
    }

    public RampartHttpBuilder addHttpIOType(RampartHttpIOType httpIOType) {
        this.httpIOType = httpIOType;
        return this;
    }

    public RampartHttpIOType getHttpIOType() {
        return httpIOType;
    }

    public RampartHttpBuilder addHttpInputValidation(RampartHttpInputValidation inputValidation) {
        this.inputValidation = inputValidation;
        return this;
    }

    @Deprecated
    public RampartHttpBuilder addHttpValidationType(RampartHttpValidationType validationType) {
        this.validationType = validationType;
        return this;
    }

    @Deprecated
    public RampartHttpBuilder addHttpValidationMap(RampartList validationMap) {
        this.validationMap = validationMap;
        return this;
    }

    /**
     Required for HTTP Action Validator
     * @see RampartHttpActionValidatorUpTo1_5
     * @see RampartHttpActionValidator1_6 ;
     */
    @Deprecated
    public RampartHttpValidationType getValidationType() {
        return validationType;
    }

    /**
     * @param inputs non empty RampartList of RampartInputs
     * @return
     */
    public RampartHttpBuilder addDataInputs(RampartList inputs) {
        this.inputs = inputs;
        return this;
    }

    public RampartList getDataInputs() {
        return inputs;
    }

    @Deprecated
    public RampartHttpBuilder addOpenRedirect(RampartBoolean onOpenRedirect) {
        if (onOpenRedirect == RampartBoolean.TRUE && openRedirect == null) {
            this.openRedirect = new RampartOpenRedirectImpl();
        }
        return this;
    }

    public RampartHttpBuilder addOpenRedirect(RampartOpenRedirect openRedirect) {
        this.openRedirect = openRedirect;
        return this;
    }

    public RampartHttpBuilder addAuthenticate(RampartBoolean onAuthenticate) {
        this.onAuthenticate = onAuthenticate;
        return this;
    }

    public RampartHttpBuilder addCsrf(RampartCsrf csrf) {
        this.csrf = csrf;
        return this;
    }

    public RampartHttpBuilder addXss(RampartXss xss) {
        this.xss = xss;
        return this;
    }

    public RampartHttpBuilder addInjectionType(RampartHttpInjectionType injectionType) {
        this.injectionType = injectionType;
        return this;
    }

    private boolean declaresSecurityFeature(RampartHttpFeaturePattern securityFeature) {
        switch (securityFeature) {
            case INPUT_VALIDATION:
                // used by RAMPART language from 2.0 onwards
                return inputValidation != null
                        // used by RAMPART language up to 1.6
                        || validationType != null;
            case SESSION_FIXATION:
                return onAuthenticate == RampartBoolean.TRUE;
            case OPEN_REDIRECT:
                return openRedirect != null;
            case CSRF:
                return csrf != null;
            case XSS:
                return xss != null;
            case RESPONSE_HEADER_INJECTION:
                return injectionType != null;
            default:
                return false;
        }
    }

    public List<RampartHttpFeaturePattern> getDeclaredSecurityFeatures() {
        List<RampartHttpFeaturePattern> list = null;
        for (RampartHttpFeaturePattern feature : RampartHttpFeaturePattern.values()) {
            if (declaresSecurityFeature(feature)) {
                if (list == null) {
                    list = new ArrayList<RampartHttpFeaturePattern>();
                }
                list.add(feature);
            }
        }
        return list != null ? list : Arrays.asList(RampartHttpFeaturePattern.SET_HEADERS);
    }

}
