package org.rampart.lang.impl.core.parsers.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.DETECT_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.MESSAGE_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.SEVERITY_KEY;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.api.core.RampartSeverity;
import org.rampart.lang.impl.core.RampartActionImpl;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.parsers.utils.ParserUtils;
import org.rampart.lang.java.RampartPrimitives;

/**
 * Parsers and utilities for the RAMPART 2.0 parsers.
 *
 * Eg.
 *    protect(message: "informative log message..", severity: 9)
 */
public final class RampartActionParser2_0 {

    /** Entry that contains an action. */
    public static final class ActionSelection {
        /** Type of the action to perform. */
        public final RampartActionType actionType;

        /** Rampart action parameters. */
        public final RampartList parameters;


        public ActionSelection(RampartActionType actionType, RampartList parameters) {
            this.actionType = actionType;
            this.parameters = parameters;
        }
    }

    /** Draft of the action - contents of the fields but not the actual data. */
    public static final class ActionDraft {
        /** Log message definition. */
        public final RampartObject logMessage;
        /** Severity definition. */
        public final RampartObject severity;

        public ActionDraft(RampartObject logMessage, RampartObject severity) {
            this.logMessage = logMessage;
            this.severity = severity;
        }
    }


    private RampartActionParser2_0() {
        throw new UnsupportedOperationException();
    }


    public static RampartAction parseRampartAction(
            Map<String, RampartList> entries,
            RampartString ruleName,
            RampartActionType...supportedActions) throws InvalidRampartRuleException {
        final RampartActionParser2_0.ActionSelection actionSelection =
                RampartActionParser2_0.getActionSelection(ruleName, entries, supportedActions);
        final List<RampartObject> parameterList = ParserUtils.toArrayList(actionSelection.parameters);
        return parseRampartAction(actionSelection.actionType, parameterList);
    }


    /**
     * Parses RAMPART action from the given data.
     * @param actionType type of the action being created.
     * @param parameters parameters of the given action.
     */
    public static RampartAction parseRampartAction(
            RampartActionType actionType,
            List<RampartObject> parameters) throws InvalidRampartRuleException {
        final ParserUtils.Pair<ActionDraft, List<RampartObject>> draftPair = extractDraft(parameters);
        throwOnUnsupportedActionParameter(draftPair.second, actionType);
        return createAction(actionType, draftPair.first);
    }


    /**
     * Extracts RAMPART action from the given data. This method is very similar to
     * {@link #parseRampartAction(RampartActionType, List)} but instead of failing on unrecognized parameters this
     * method just returns those parameters as the second element of the pair.
     *
     * @param actionType type of the action being created.
     * @param parameters parameters of the given action.
     * @return a pair of (RAMPART action, unused parameters).
     */
    public static ParserUtils.Pair<RampartAction, List<RampartObject>> extractRampartAction(
            RampartActionType actionType,
            List<RampartObject> parameters) throws InvalidRampartRuleException {
        final ParserUtils.Pair<ActionDraft, List<RampartObject>> draftPair = extractDraft(parameters);
        return new ParserUtils.Pair<RampartAction, List<RampartObject>>(
                createAction(actionType, draftPair.first),
                draftPair.second
        );
    }

    /**
     * Extracts the supported parameters from the list of action parameters and
     * builds a draft of the action structure. The draft may later be passed to
     * {@link #createAction(RampartActionType, ActionDraft)} to create a new action.
     * @param params list of the action parameters to extract data from.
     * @return a pair of (action draft, unused parameters). The first is the draft based on the
     *   supported element parameters. The second element is list of parameters that were not used
     *   by this parser.
     */
    public static ParserUtils.Pair<ActionDraft, List<RampartObject>> extractDraft(List<RampartObject> params) {
        final ParserUtils.Pair<RampartObject, List<RampartObject>> logMessageAndParams =
            ParserUtils.extractLastNamedValue(MESSAGE_KEY, params);
        final ParserUtils.Pair<RampartObject, List<RampartObject>> severityAndParams =
            ParserUtils.extractLastNamedValue(SEVERITY_KEY, logMessageAndParams.second);
        return new ParserUtils.Pair<ActionDraft, List<RampartObject>>(
                new ActionDraft(logMessageAndParams.first, severityAndParams.first),
                severityAndParams.second
        );
    }


    /**
     * Creates a new action from the draft. Parses the corresponding components and validates
     * the overall structure of the rule.
     */
    public static RampartActionImpl createAction(RampartActionType actionType, ActionDraft draft)
            throws InvalidRampartRuleException {
        final RampartString logMessage = parseLogMessage(draft.logMessage);
        final RampartSeverity severity = RampartSeverityParser2_0.parseSeverityValue(draft.severity);
        checkLogMessageValidForAction(actionType, logMessage);
        return new RampartActionImpl(
                actionType,
                logMessage,
                severity,
                shouldLogMessage(logMessage),
                null);
    }


    /** Returns constants that should be used to encode the corresponding actions. */
    public static RampartConstant[] toRampartKeys(RampartActionType... actions) {
        final RampartConstant[] result = new RampartConstant[actions.length];
        for (int i = 0; i < actions.length; i++) {
            result[i] = RampartPrimitives.newRampartConstant(actions[i].getName().toString());
        }
        return result;
    }


    /** Checks that the action is "well-formed" for the given action and parameters. */
    public static void checkLogMessageValidForAction(RampartActionType actionType, RampartString logMessage)
            throws InvalidRampartRuleException {
        if (logMessage == null && actionType == RampartActionType.DETECT) {
            throw new InvalidRampartRuleException("\"" + DETECT_KEY + "\" action must declare message");
        }
    }

    /**
     * Finds a (single) action selection among the rules.
     * @param ruleName rule name (used for error messages).
     * @param entries rule definition used for accessing action.
     * @param supportedActions list of supported actions.
     * @return pair of action and corresponding parameters.
     * @throws InvalidRampartRuleException if there are two or more actions or if there are no actions.
     */
    public static ActionSelection getActionSelection(
            RampartString ruleName,
            Map<String, RampartList> entries,
            RampartActionType...supportedActions) throws InvalidRampartRuleException {
        ActionSelection result = null;
        for (RampartActionType action: supportedActions) {
            final RampartList maybeList = entries.get(action.getName().toString());
            if (maybeList == null)
                continue;

            if (result != null) {
                throw new InvalidRampartRuleException(
                        "actions \"" + result.actionType.getName() + "\" and \"" + action.getName()
                        + "\" are declared. Declaration of more than one action type is not allowed.");
            }
            result = new ActionSelection(action, maybeList);
        }

        if (result == null) {
            throw new InvalidRampartRuleException(
                    "RAMPART \"" + ruleName + "\" action is missing. Must be one of: " + Arrays.toString(supportedActions));
        }
        return result;
    }


    /**
     * Validates the log message passed to the Rampart action
     * @param logMessageValue log message to be validated
     * @return logMessage as an RampartString
     * @throws InvalidRampartRuleException when log message is null or of the incorrect type
     */
    public static RampartString parseLogMessage(RampartObject logMessageValue)
            throws InvalidRampartRuleException {
        if (logMessageValue != null) {
            if (!(logMessageValue instanceof RampartString)) {
                throw new InvalidRampartRuleException("value for the message must be a string literal");
            }
            return (RampartString) logMessageValue;
        }
        return null;
    }


    /**
     * Determines whether the given log message should have an entry in the CEF log.
     * An empty String log message will not have an entry in the CEF log.
     * @param logMessage message to be tested
     * @return whether the log message should be written to the log file or not.
     */
    public static RampartBoolean shouldLogMessage(RampartString logMessage) {
        return logMessage == null ? RampartBoolean.FALSE : RampartBoolean.TRUE;
    }


    /**
     * Throws if there is remaining unsupported action parameter.
     * @param unusedParameters parameters that are not used by parsing process. This may be an empty list.
     */
    public static void throwOnUnsupportedActionParameter(List<RampartObject> unusedParameters, RampartActionType actionType)
            throws InvalidRampartRuleException {
        if (unusedParameters.isEmpty()) {
            return;
        }
        throwOnUnsupportedActionParameter(unusedParameters.get(0), actionType);
    }


    public static void throwOnUnsupportedActionParameter(RampartObject parameter, RampartActionType actionType)
            throws InvalidRampartRuleException {
        throw new InvalidRampartRuleException(
                "parameter \"" + parameter + "\" to the action \"" + actionType + "\" is not supported");
    }
}
