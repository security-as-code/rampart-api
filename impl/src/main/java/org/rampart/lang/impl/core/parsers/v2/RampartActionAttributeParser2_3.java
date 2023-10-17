package org.rampart.lang.impl.core.parsers.v2;

import java.util.List;
import java.util.Map;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartActionTarget;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.api.core.RampartActionWithAttribute;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.parsers.utils.ParserUtils;


/**
 * Parsers and extensions for the action attribute 2_3.
 *
 * This class does not introduce many new methods as the <em>attribute</em>
 * parsing is exactly the same as in RAMPART/2.0-2.2 versions. What changes is how the
 * original action is parsed, not the attribute(s).
 */
public final class RampartActionAttributeParser2_3 {
    private RampartActionAttributeParser2_3() {
        throw new UnsupportedOperationException();
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
                RampartActionAttributeParser2_0.AttributeConfigMapParser configMapParser,
                RampartActionTarget... supportedTargets
            ) throws InvalidRampartRuleException {
        final RampartActionParser2_0.ActionSelection actionSelection =
            RampartActionParser2_0.getActionSelection(ruleName, symbolTable, supportedActions);
        if (actionSelection == null) {
            return null;
        }
        final List<RampartObject> parameters = ParserUtils.toArrayList(actionSelection.parameters);
        final ParserUtils.Pair<RampartAction, List<RampartObject>> baseAction =
                RampartActionParser2_3.extractRampartAction(actionSelection.actionType, parameters);
        return RampartActionAttributeParser2_0.enrichActionWithAttribute(
                baseAction.first, baseAction.second, configMapParser, supportedTargets);
    }

}
