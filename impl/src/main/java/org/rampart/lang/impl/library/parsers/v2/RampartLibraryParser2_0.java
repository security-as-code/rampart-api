package org.rampart.lang.impl.library.parsers.v2;

import static org.rampart.lang.api.constants.RampartLibraryConstants.LIBRARY_KEY;
import static org.rampart.lang.api.constants.RampartLibraryConstants.LOAD_KEY;

import java.util.Map;

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
import org.rampart.lang.java.builder.RampartLibraryBuilder;

/** Parser for the RAMPART Library clause. */
public class RampartLibraryParser2_0 implements Validatable<RampartLibraryBuilder, InvalidRampartRuleException> {
    /** Human-readable name of the model parsed by this parser. Used in generating error messages. */
    static final String MODEL_NAME = "RAMPART library model";

    /** Supported action types. */
    static final RampartActionType[] SUPPORTED_ACTIONS =
        {RampartActionType.ALLOW, RampartActionType.DETECT, RampartActionType.PROTECT};

    /** Actions supported by the rule. */
    static final RampartConstant[] SUPPORTED_ACTION_KEYS = RampartActionParser2_0.toRampartKeys(SUPPORTED_ACTIONS);

    /** Key of this rule. */
    static final RampartConstant THIS_RULE_KEY = LIBRARY_KEY;

    /** A key of this rule in the array format (useful for use with validation API. */
    static final RampartConstant[] THIS_RULE_KEYS = {THIS_RULE_KEY};

    /** A key that is used to access path element (and to avoid copy/paste). */
    static final RampartConstant PATH_ELEMENT_KEY = LOAD_KEY;

    /** Required rule parameters. */
    static final RampartConstant[] REQUIRED_FIELDS = {PATH_ELEMENT_KEY};

    private final Map<String, RampartList> symbolTable;

    public RampartLibraryParser2_0(Map<String, RampartList> symbolTable) {
        this.symbolTable = symbolTable;
    }

    //@Override
    public RampartLibraryBuilder validate() throws InvalidRampartRuleException {
        return parse(symbolTable);
    }


    public static RampartLibraryBuilder parse(Map<String, RampartList> symbolTable) throws InvalidRampartRuleException {
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

        return new RampartLibraryBuilder()
                .addRuleName(ruleName)
                .addLibraryList(RampartPathParser.parsePaths(symbolTable, PATH_ELEMENT_KEY, targetOsList))
                .addAction(RampartActionParser2_0.parseRampartAction(symbolTable, ruleName, SUPPORTED_ACTIONS))
                .addTargetOSList(targetOsList);
    }
}
