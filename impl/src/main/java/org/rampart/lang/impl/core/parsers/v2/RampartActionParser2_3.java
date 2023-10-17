package org.rampart.lang.impl.core.parsers.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.STACKTRACE_KEY;

import java.util.List;
import java.util.Map;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.api.core.RampartSeverity;
import org.rampart.lang.impl.core.RampartActionImpl;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.parsers.utils.ParserUtils;

/**
 * Parser for RAMPART actions introduced in RAMPART 2.3.
 *
 * Eg.
 *    protect(message: "informative log message..", severity: 9, stacktrace: "full")
 */
public class RampartActionParser2_3 {

    /** Draft of the action - contents of the fields but not the actual data. */
    public static final class ActionDraft {
        /** Log message definition. */
        public final RampartObject logMessage;
        /** Severity definition. */
        public final RampartObject severity;
        /** Stacktrace definition. */
        public final RampartObject stacktrace;

        public ActionDraft(RampartObject logMessage, RampartObject severity, RampartObject stacktrace) {
            this.logMessage = logMessage;
            this.severity = severity;
            this.stacktrace = stacktrace;
        }

        public ActionDraft(RampartActionParser2_0.ActionDraft previousDraft, RampartObject stacktrace) {
            this(previousDraft.logMessage, previousDraft.severity, stacktrace);
        }
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
        RampartActionParser2_0.throwOnUnsupportedActionParameter(draftPair.second, actionType);
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
        final ParserUtils.Pair<RampartActionParser2_0.ActionDraft, List<RampartObject>> prevDraftAndParams =
            RampartActionParser2_0.extractDraft(params);
        final ParserUtils.Pair<RampartObject, List<RampartObject>> stacktraceAndParams =
            ParserUtils.extractLastNamedValue(STACKTRACE_KEY, prevDraftAndParams.second);
        return new ParserUtils.Pair<ActionDraft, List<RampartObject>>(
                new ActionDraft(prevDraftAndParams.first, stacktraceAndParams.first),
                stacktraceAndParams.second
        );
    }


    /**
     * Creates a new action from the draft. Parses the corresponding components and validates
     * the overall structure of the rule.
     */
    public static RampartActionImpl createAction(RampartActionType actionType, ActionDraft draft)
            throws InvalidRampartRuleException {
        final RampartString logMessage = RampartActionParser2_0.parseLogMessage(draft.logMessage);
        final RampartSeverity severity = RampartSeverityParser2_0.parseSeverityValue(draft.severity);
        final RampartString stacktrace = parseStacktraceValue(draft.stacktrace);
        RampartActionParser2_0.checkLogMessageValidForAction(actionType, logMessage);
        return new RampartActionImpl(
                actionType,
                logMessage,
                severity == null ? RampartSeverity.UNKNOWN : severity,
                RampartActionParser2_0.shouldLogMessage(logMessage),
                stacktrace
        );
    }

    /**
     * Parses stacktrace specified.
     *
     * @param stacktrace object representing the stacktrace in the symbol table
     * @return RampartString instance representing the value specified
     *         or null if the value is not specified
     * @throws InvalidRampartRuleException when stacktrace specified is not an RampartString
     */
    public static RampartString parseStacktraceValue(RampartObject stacktrace)
            throws InvalidRampartRuleException {
        if (stacktrace == null) {
            return null;
        }
        if (!(stacktrace instanceof RampartString)) {
            throw new InvalidRampartRuleException("value for the stacktrace must be a string literal");
        }
        return (RampartString) stacktrace;
    }
}
