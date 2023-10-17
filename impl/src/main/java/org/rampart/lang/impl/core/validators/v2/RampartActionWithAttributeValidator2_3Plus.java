package org.rampart.lang.impl.core.validators.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.MESSAGE_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.SEVERITY_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.STACKTRACE_KEY;

import java.util.Map;

import org.rampart.lang.api.*;
import org.rampart.lang.api.core.*;
import org.rampart.lang.impl.core.RampartActionWithAttributeImpl;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.java.RampartPrimitives;

public abstract class RampartActionWithAttributeValidator2_3Plus extends RampartActionValidator2_3Plus {

    public RampartActionWithAttributeValidator2_3Plus(Map<String, RampartList> visitorSymbolTable, RampartRuleType ruleType) {
        super(visitorSymbolTable, ruleType);
    }

    @Override
    public RampartAction validateRampartAction() throws InvalidRampartRuleException {
        RampartAction action = super.validateRampartAction();
        RampartList actionObject = visitorSymbolTable.get(action.getActionType().toString());
        RampartActionTarget target = null;
        RampartActionAttribute attribute = null;
        RampartList configMap = null;
        RampartObjectIterator it = actionObject.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject actionParam = it.next();
            if (actionParam instanceof RampartNamedValue) {
                RampartNamedValue targetParam = (RampartNamedValue) actionParam;
                RampartActionTarget tempTarget;
                if ((tempTarget = lookForActionTarget(targetParam.getName())) != null) {
                    if (action.getActionType() != RampartActionType.PROTECT) {
                        throw new InvalidRampartRuleException(
                                "action type \"" + action.getActionType() + "\" does not support targets and attributes");
                    } else if (target != null) {
                        throw new InvalidRampartRuleException("invalid specified target \"" + tempTarget
                                + "\". Only a single target is allowed for \"" + action.getActionType()
                                + "\" declaration");
                    }
                    target = tempTarget;
                    attribute = validateActionAttribute(targetParam.getRampartObject(), target);
                    configMap = validateAttributeConfigMap(targetParam.getRampartObject(), attribute);
                } else if (!targetParam.getName().equals(MESSAGE_KEY)
                        && !targetParam.getName().equals(SEVERITY_KEY)
                        && !targetParam.getName().equals(STACKTRACE_KEY)) {
                    throwOnUnsupportedActionParameter(targetParam, action.getActionType());
                }
            } else {
                // All supported action attributes are instances of NamedValue,
                // so this one is NOT supported.
                // Unless, it is one of valid action targets, but in wrong format.
                // for example: http-session
                // instead of full: http-session: "regenerate-id"
                // We skip here, because there are dedicated validations for this case
                // in the extending classes.
                if (actionParam instanceof RampartConstant) {
                    RampartConstant targetParam = (RampartConstant) actionParam;
                    if (lookForActionTarget(targetParam) != null) {
                        continue;
                    }
                }
                throwOnUnsupportedActionParameter(actionParam, action.getActionType());
            }
        }
        if (target == null) {
            return action;
        }
        return new RampartActionWithAttributeImpl(action, target, attribute, configMap);
    }

    @Override
    protected boolean throwOnUnidentifiedParameter() {
        return false;
    }

    private RampartActionAttribute validateActionAttribute(RampartObject targetObjValue, RampartActionTarget target)
            throws InvalidRampartRuleException {
        RampartConstant attributeConstant;
        if (targetObjValue instanceof RampartNamedValue) {
            RampartNamedValue namedValueAttribute = (RampartNamedValue) targetObjValue;
            attributeConstant = namedValueAttribute.getName();
        } else if (targetObjValue instanceof RampartList) {
            RampartList targetList = (RampartList) targetObjValue;
            RampartObject listValue;
            if (RampartPrimitives.toJavaInt(targetList.size()) != 1
                    || (listValue = targetList.getFirst()) instanceof RampartList) {
                throw new InvalidRampartRuleException(
                        "list \"" + targetList + "\" of action target \"" + target + "\" must contain a single value");
            }
            return validateActionAttribute(listValue, target);
        } else if (targetObjValue instanceof RampartConstant) {
            attributeConstant = (RampartConstant) targetObjValue;
        } else {
            throw new InvalidRampartRuleException(
                    "action attribute \"" + targetObjValue + "\" must be a key value pair or a constant");
        }
        RampartActionAttribute actionAttribute = RampartActionAttribute.fromConstant(attributeConstant);
        if (actionAttribute == null
                || target.getSupportedActionAttributes().contains(actionAttribute.getName()) == RampartBoolean.FALSE) {
            throw new InvalidRampartRuleException(
                    "unsupported attribute \"" + attributeConstant + "\" for action target \"" + target + "\"");
        }
        return actionAttribute;
    }

    protected abstract RampartActionTarget lookForActionTarget(RampartConstant name);

    protected abstract RampartList validateAttributeConfigMap(RampartObject targetObjValue, RampartActionAttribute attributeType)
            throws InvalidRampartRuleException;
}
