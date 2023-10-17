package org.rampart.lang.impl.http.validators.v2;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;

import java.util.List;
import java.util.Map;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartActionAttribute;
import org.rampart.lang.api.core.RampartActionTarget;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.api.core.RampartActionWithAttribute;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.api.http.RampartHttpFeaturePattern;
import org.rampart.lang.api.http.RampartHttpIOType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.v2.RampartRuleStructureValidator2_0Plus;
import org.rampart.lang.java.builder.RampartHttpBuilder;

public class RampartHttpStructureValidator2_0 extends RampartRuleStructureValidator2_0Plus {
    protected final RampartHttpBuilder builder;

    public RampartHttpStructureValidator2_0(Map<String, RampartList> visitorSymbolTable, RampartHttpBuilder builder) {
        super(visitorSymbolTable, RampartRuleType.HTTP);
        this.builder = builder;
    }

    public RampartHttpFeaturePattern crossValidate(RampartHttpBuilder builder) throws InvalidRampartRuleException {
        List<RampartHttpFeaturePattern> securityFeatures = builder.getDeclaredSecurityFeatures();
        if (securityFeatures.size() != 1) {
            if (securityFeatures.size() == 0) {
                throw new IllegalStateException(
                        "should not happen - no security feature identified from RAMPART http rule");
            }
            // Only needs first two for a message
            throw new InvalidRampartRuleException("\"" + securityFeatures.get(0).getDeclarationTerm() + "\" and \""
                    + securityFeatures.get(1).getDeclarationTerm() + "\" cannot be declared together");
        }

        RampartHttpFeaturePattern securityFeature = securityFeatures.get(0);
        if (!isDataInputCompatibleWith(securityFeature)) {
            throw new InvalidRampartRuleException(getIncompatibleDeclarationErrorMessage(INPUT_KEY, securityFeature));
        }
        if (!isHttpIOTypeCompatibleWith(securityFeature)) {
            throw new InvalidRampartRuleException(
                    getIncompatibleDeclarationErrorMessage(builder.getHttpIOType().getName(), securityFeature));
        }
        RampartAction action = builder.getRampartAction();
        if (!isActionCompatibleWith(securityFeature)) {
            throw new InvalidRampartRuleException(getIncompatibleDeclarationErrorMessage(action.getActionType().getName(), securityFeature));
        }

        if (RampartActionType.PROTECT.equals(action.getActionType())) {
            checkProtectActionWithTargets(action, securityFeature);
        }
        return securityFeature;
    }

    private static void checkProtectActionWithTargets(RampartAction action, RampartHttpFeaturePattern securityFeature)
            throws InvalidRampartRuleException {
        if (securityFeature == RampartHttpFeaturePattern.SESSION_FIXATION
                && !hasAttributesForSessionFixation(action)) {
            throw new InvalidRampartRuleException(
                    "action \"" + action.getActionType() + "\" is missing the action attribute \""
                            + REGENERATE_ID_KEY + "\" for target \"" + HTTP_SESSION_KEY + "\"");
        } else if (securityFeature == RampartHttpFeaturePattern.SET_HEADERS && !hasAttributesForSetHeaders(action)) {
            throw new InvalidRampartRuleException(
                    "action \"" + action.getActionType() + "\" is missing the action attribute \"" + SET_HEADER_KEY
                            + "\" for target \"" + HTTP_RESPONSE_KEY + "\"");
        } else if (action instanceof RampartActionWithAttribute
                && !(securityFeature == RampartHttpFeaturePattern.SET_HEADERS
                        || securityFeature == RampartHttpFeaturePattern.SESSION_FIXATION)) {
            throw new InvalidRampartRuleException(
                    "declaration \"" + securityFeature.getDeclarationTerm() + "\" does not support action target \""
                            + ((RampartActionWithAttribute) action).getTarget() + "\"");
        }
    }

    private static boolean hasAttributesForSessionFixation(RampartAction action) {
        return action instanceof RampartActionWithAttribute
                && ((RampartActionWithAttribute) action).getTarget() == RampartActionTarget.HTTP_SESSION
                && ((RampartActionWithAttribute) action).getAttribute() == RampartActionAttribute.REGENERATE_ID;
    }

    private static boolean hasAttributesForSetHeaders(RampartAction action) {
        return action instanceof RampartActionWithAttribute
                && ((RampartActionWithAttribute) action).getTarget() == RampartActionTarget.HTTP_RESPONSE
                && ((RampartActionWithAttribute) action).getAttribute() != null;
    }

    private static String getIncompatibleDeclarationErrorMessage(RampartConstant invalidKey, RampartHttpFeaturePattern securityFeature) {
        if(securityFeature.getDeclarationTerm() != null) {
            return "invalid declaration of \"" + invalidKey + "\" with declaration of \"" + securityFeature.getDeclarationTerm() + "\"";
        }
        return "declaration of \"" + invalidKey + "\" is invalid with the current combination of declarations for RAMPART http rule";
    }

    private boolean isDataInputCompatibleWith(RampartHttpFeaturePattern securityFeature) {
        if (builder.getDataInputs().isEmpty() == RampartBoolean.TRUE) {
            return true;
        }
        switch (securityFeature) {
            case XSS:
            case OPEN_REDIRECT:
                return true;
            default:
                return false;
        }
    }

    private boolean isHttpIOTypeCompatibleWith(RampartHttpFeaturePattern securityFeature) {
        switch (securityFeature) {
            case SESSION_FIXATION:
            case INPUT_VALIDATION:
            case CSRF:
                return builder.getHttpIOType() == RampartHttpIOType.REQUEST;
            case XSS:
            case OPEN_REDIRECT:
            case SET_HEADERS:
            case RESPONSE_HEADER_INJECTION:
                return builder.getHttpIOType() == RampartHttpIOType.RESPONSE;
        }
        return false;
    }

    protected boolean isActionCompatibleWith(RampartHttpFeaturePattern securityFeature) {
        RampartActionType rampartActionType = builder.getRampartAction().getActionType();
        switch (securityFeature) {
            case INPUT_VALIDATION:
                return RampartActionType.PROTECT.equals(rampartActionType)
                        || RampartActionType.DETECT.equals(rampartActionType)
                        || RampartActionType.ALLOW.equals(rampartActionType);
            default:
                return RampartActionType.PROTECT.equals(rampartActionType)
                        || RampartActionType.DETECT.equals(rampartActionType);
        }
    }
}
