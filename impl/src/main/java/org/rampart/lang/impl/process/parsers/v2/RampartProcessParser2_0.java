package org.rampart.lang.impl.process.parsers.v2;

import java.util.Map;

import static org.rampart.lang.api.constants.RampartProcessConstants.EXECUTE_KEY;
import static org.rampart.lang.api.constants.RampartProcessConstants.PROCESS_KEY;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.impl.core.parsers.RampartPathParser;
import org.rampart.lang.impl.core.parsers.RampartRuleNameParser;
import org.rampart.lang.impl.core.parsers.RampartTargetOsParser;
import org.rampart.lang.impl.core.parsers.v2.RampartActionParser2_0;
import org.rampart.lang.impl.core.validators.RuleStructureValidator;
import org.rampart.lang.java.builder.RampartProcessBuilder;

public final class RampartProcessParser2_0 implements Validatable<RampartProcessBuilder, InvalidRampartRuleException> {
    /** Human-readable name of the model parsed by this parser. Used in generating error messages. */
    static final String MODEL_NAME = "RAMPART process model";

    /** Supported action types. */
    static final RampartActionType[] SUPPORTED_ACTIONS =
        {RampartActionType.ALLOW, RampartActionType.DETECT, RampartActionType.PROTECT};

    /** Actions supported by the rule. */
    static final RampartConstant[] SUPPORTED_ACTION_KEYS = RampartActionParser2_0.toRampartKeys(SUPPORTED_ACTIONS);

    /** Key used by this rule. */
    static final RampartConstant THIS_RULE_KEY = PROCESS_KEY;

    /** A convenient array of the key that could be used in the checks for supported fields. */
    static final RampartConstant[] THIS_RULE_KEYS = {THIS_RULE_KEY};

    /** A key that is used to access path element (and to avoid copy/paste). */
    static final RampartConstant PATH_ELEMENT_KEY = EXECUTE_KEY;

    /** Required rule parameters. */
    static final RampartConstant[] REQUIRED_FIELDS = {PATH_ELEMENT_KEY};


    private final Map<String, RampartList> symbolTable;

    public RampartProcessParser2_0(Map<String, RampartList> symbolTable) {
        this.symbolTable = symbolTable;
    }

    //@Override
    public RampartProcessBuilder validate() throws InvalidRampartRuleException {
        return parse(symbolTable);
    }


    public static RampartProcessBuilder parse(Map<String, RampartList> symbolTable) throws InvalidRampartRuleException {
        final RampartString ruleName = RampartRuleNameParser.getRuleName(symbolTable, THIS_RULE_KEY);
        RuleStructureValidator.validateDeclarations(
            ruleName, symbolTable,
            REQUIRED_FIELDS, THIS_RULE_KEYS, SUPPORTED_ACTION_KEYS
        );
        RuleStructureValidator.validateRequiredKeys(
            MODEL_NAME, symbolTable.keySet(),
            REQUIRED_FIELDS
        );

        final RampartList targetOsList = RampartTargetOsParser.parseTargetOs(symbolTable, THIS_RULE_KEY);

        return new RampartProcessBuilder()
                .addTargetOSList(targetOsList)
                .addRuleName(ruleName)
                .addProcessList(RampartPathParser.parsePaths(symbolTable, PATH_ELEMENT_KEY, targetOsList))
                .addAction(RampartActionParser2_0.parseRampartAction(symbolTable, ruleName, SUPPORTED_ACTIONS));
    }
}
