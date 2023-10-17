package org.rampart.lang.impl.core.parsers.v2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartNamedValue;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartActionAttribute;
import org.rampart.lang.api.core.RampartActionTarget;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.api.core.RampartActionWithAttribute;
import org.rampart.lang.impl.core.RampartActionWithAttributeImpl;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.parsers.utils.ParserUtils;
import org.rampart.lang.java.RampartPrimitives;

/**
 * Parser and utilities for extracting additional action attributes.
 * For example:
 *
 * <code>
 *   protect(severity: 5, http-session: "regenerate-id")
 * </code>
 *
 * This parser only handles the attribute but not the RAMPART Action.
 */
public final class RampartActionAttributeParser2_0 {
    /** Result of parsing the attribute. */
    public static final class Result {
        /** What is the target of the action. */
        public final RampartActionTarget actionTarget;
        /** Action attribute - what else to do with the action. */
        public final RampartActionAttribute actionAttribute;
        /** Source of the attribute - what was used to build the attribute. May be used for additional validations. */
        public final RampartObject actionAttributeSource;

        private Result(RampartActionTarget actionTarget, RampartActionAttribute actionAttribute,
                       RampartObject actionAttributeSource) {
            this.actionTarget = actionTarget;
            this.actionAttribute = actionAttribute;
            this.actionAttributeSource = actionAttributeSource;
        }
    }


    /**  Selection of the attribute to parse. */
    public static final class AttributeSelection {
        /** Target action type. */
        public final RampartActionTarget target;
        /** Data associated with the attribute. */
        public final RampartObject attributeData;

        public AttributeSelection(RampartActionTarget target, RampartObject attributeData) {
            this.target = target;
            this.attributeData = attributeData;
        }
    }


    /** Parser of the attribute configuration map. */
    public static interface AttributeConfigMapParser {
        /**
         * Parses the <code>targetObjValue</code> into the Attribute's
         * configuration map.
         * @param attributeData data assigned to the attribute.
         * @param attributeType type of the attribute being parsed.
         * @return attribute's configuration maps
         */
        RampartList parseAttributeConfigMap(RampartObject attributeData, RampartActionAttribute attributeType)
            throws InvalidRampartRuleException;
    }


    /**
     * Parses an action with an optional attribute.
     *
     * @param symbolTable symbol table of a rule.
     * @param ruleName name of the rule being parsed. Used in error messages.
     * @param supportedActions action types supported by the caller.
     * @param configMapParser description of how to parse configuration map of
     *   the attribute.
     * @param supportedTargets action targets that are supported by the caller.
     *   These action types could appear as attributes for the action.
     * @return A simple {@link RampartAction} if there are no additional attributes
     *   or an {@link RampartActionWithAttribute} if there is an attribute that was found.
     *   Returns <code>null</code> if there is no appropriate action.
     */
    public static RampartAction parseActionWithOptionalAttribute(
            Map<String, RampartList> symbolTable,
            RampartString ruleName,
            RampartActionType[] supportedActions,
            AttributeConfigMapParser configMapParser,
            RampartActionTarget... supportedTargets) throws InvalidRampartRuleException {
        final RampartActionParser2_0.ActionSelection actionSelection =
            RampartActionParser2_0.getActionSelection(ruleName, symbolTable, supportedActions);
        final List<RampartObject> parameters = ParserUtils.toArrayList(actionSelection.parameters);
        final ParserUtils.Pair<RampartAction, List<RampartObject>> baseAction =
                RampartActionParser2_0.extractRampartAction(actionSelection.actionType, parameters);
        return enrichActionWithAttribute(baseAction.first, baseAction.second, configMapParser, supportedTargets);
    }


    /**
     * Enriches the base action with an additional attribute. Also validates that the passed parameters were
     * completely consumed (i.e. treated as an attribute).
     * @param baseAction base action to process.
     * @param parameters additional action parameters to check.
     * @param configMapParser description of how to parse configuration map of
     *   the attribute.
     * @param supportedTargets action targets that are supported by the caller.
     *   These action types could appear as attributes for the action.
     * @return either base action (if there are no additional attributes) or enriched action.
     */
    static RampartAction enrichActionWithAttribute(
            RampartAction baseAction,
            List<RampartObject> parameters,
            AttributeConfigMapParser configMapParser,
            RampartActionTarget... supportedTargets) throws InvalidRampartRuleException {
        final ParserUtils.Pair<RampartActionAttributeParser2_0.Result, List<RampartObject>> attributeAndParameters =
                extractLegacyAttribute(
                        parameters, baseAction.getActionType(), supportedTargets
                );
        RampartActionParser2_0.throwOnUnsupportedActionParameter(
                attributeAndParameters.second, baseAction.getActionType());

        final RampartActionAttributeParser2_0.Result attributeData = attributeAndParameters.first;
        if (attributeData == null) {
            return baseAction;
        }

        final RampartList actionConfigMap = configMapParser.parseAttributeConfigMap(
                attributeData.actionAttributeSource, attributeData.actionAttribute);
        return new RampartActionWithAttributeImpl(
                baseAction, attributeData.actionTarget, attributeData.actionAttribute, actionConfigMap);
    }


    /**
     * Extracts a (legacy) attribute for the given rule parameters.
     * @param input input parameter list.
     * @param actionType type of the action for which attributes are extracted.
     * @param supportedTargets action targets that are supported by parsing.
     * @return a pair of (optional result, unused parameter definitions).
     */
    public static ParserUtils.Pair<Result, List<RampartObject>> extractLegacyAttribute(
            List<RampartObject> input,
            RampartActionType actionType,
            RampartActionTarget... supportedTargets) throws InvalidRampartRuleException {
        final ParserUtils.Pair<AttributeSelection, List<RampartObject>> targetAndParams =
            chooseTarget(input, actionType, supportedTargets);
        return new ParserUtils.Pair<Result, List<RampartObject>>(
                parseLegacySelection(actionType, targetAndParams.first),
                targetAndParams.second
        );
    }


    /**
     * Parses selection into a more specific object.
     * @param actionType type of the action for which attribute is being parsed.
     * @param data attribute selection with attribute data. May be <code>null</code>.
     * @return parsed (structured) attribute data. Returns <code>null</code> if the
     *   <code>data</code> is <code>null</code>.
     * @throws InvalidRampartRuleException if the action attribute is not valid, if the
     *   action attribute is not applicable to the action target or if the attribute
     *   is not applicable to the action type (due to the legacy reasons).
     */
    public static Result parseLegacySelection(
            RampartActionType actionType,
            AttributeSelection data) throws InvalidRampartRuleException {
        if (data == null) {
            return null;
        }

        if (actionType != RampartActionType.PROTECT) {
            /* This check is ONLY for compatibility with the legacy parsing and just for making
             * the same consistent error message.
             *
             * The client of this parser has More flexibility. The client may check the action
             * type before invoking the parser and decide if the attributes should be consumed or not.
             * If it decides to not parse the attributes, all "extra flags" would remain as unrecognized
             * (not consumed) fields and eventually would be reported as such.
             *
             * Moreover, the client may decide to have different sets of attributes for
             * different actions. But we just have this for compatibility and consistency in the error message.
             */
            throw new InvalidRampartRuleException(
                    "action type \"" + actionType + "\" does not support targets and attributes");
        }

        final RampartActionAttribute attribute = validateActionAttribute(data.attributeData, data.target);
        return new Result(data.target, attribute, data.attributeData);
    }


    /**
     * Selects an attribute (target) to be parsed from the list of provided attributes.
     * @param input input parameters to use.
     * @param actionType action type being checked, used only in debug messages.
     * @param supportedTargets attribute types supported by this specific parser.
     * @return a pair of (selected attribute, unused parameters). The selected attribute is the
     *   one matching the supported attributes (if any). The unused parameter(s) are the other parameters
     *   except the selected one. The order of unused parameters is the same as the original list.
     * @throws InvalidRampartRuleException if there are more than two supported attributes/targets.
     */
    public static ParserUtils.Pair<AttributeSelection, List<RampartObject>> chooseTarget(
            List<RampartObject> input,
            RampartActionType actionType,
            RampartActionTarget... supportedTargets) throws InvalidRampartRuleException {
        final List<RampartObject> unusedParameters = new ArrayList<RampartObject>();
        AttributeSelection result = null;

        for (RampartObject actionParam: input) {
            if ((actionParam instanceof RampartConstant)) {
                if (findActionTarget((RampartConstant) actionParam, supportedTargets) != null) {
                    /* This is a very special case. To quote the previous code from which this branch was ported:
                     *
                     * > All supported action attributes are instances of NamedValue,
                     * > so this one is NOT supported.
                     * > Unless, it is one of valid action targets, but in wrong format.
                     * > for example: http-session
                     * > instead of full: http-session: "regenerate-id"
                     * > We skip here, because there are dedicated validations for this case
                     * > in the extending classes.
                     *
                     * Obviously, there is no subclassing in this specific case (the previous code was using
                     * inheritance instead of configuration). At the same time at the time of refactoring
                     * NO special handling of this situation was observed in subclasses. Anyway, this code branch
                     * remains for "backward compatibility".
                     */
                    continue;
                } else {
                    unusedParameters.add(actionParam);
                }
            } else if ((actionParam instanceof RampartNamedValue)) {
                final RampartNamedValue namedParam = (RampartNamedValue) actionParam;
                final RampartActionTarget actionTarget = findActionTarget(namedParam.getName(), supportedTargets);
                if (actionTarget == null) {
                    unusedParameters.add(actionParam);
                    continue;
                }

                if (result != null) {
                    /* Not strictly true in this case. The real issues is that two "mutually exclusive" attributes
                     * were provided. The parser itself (and this specific method) is perfectly capable to handle
                     * multiple attributes, it has to be invoked once for every supported attribute (or set of
                     * disjoint attributes).
                     *
                     * The message is left intact for backward compatibility.
                     */
                    throw new InvalidRampartRuleException("invalid specified target \"" + actionTarget
                            + "\". Only a single target is allowed for \"" + actionType
                            + "\" declaration");
                }
                result = new AttributeSelection(actionTarget, namedParam.getRampartObject());
            } else {
                unusedParameters.add(actionParam);
            }
        }

        return new ParserUtils.Pair<AttributeSelection, List<RampartObject>>(result, unusedParameters);
    }


    /** Finds an action target for the given "keyword" and a list of options to choose from. */
    private static RampartActionTarget findActionTarget(RampartConstant actionParam, RampartActionTarget[] supportedTargets) {
        for (RampartActionTarget maybeTarget : supportedTargets) {
            if (maybeTarget.getName().equals(actionParam)) {
                return maybeTarget;
            }
        }
        return null;
    }


    public static RampartActionAttribute validateActionAttribute(RampartObject targetObjValue, RampartActionTarget target)
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
}
