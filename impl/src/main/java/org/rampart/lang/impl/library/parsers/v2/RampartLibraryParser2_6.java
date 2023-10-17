package org.rampart.lang.impl.library.parsers.v2;

import java.util.Map;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.impl.core.parsers.RampartPathParser;
import org.rampart.lang.impl.core.parsers.RampartRuleNameParser;
import org.rampart.lang.impl.core.parsers.RampartTargetOsParser;
import org.rampart.lang.impl.core.parsers.v2.RampartActionParser2_3;
import org.rampart.lang.impl.core.parsers.v2.RampartMetadataParser;
import org.rampart.lang.impl.core.validators.RuleStructureValidator;
import org.rampart.lang.java.builder.RampartLibraryBuilder;


/** Parser for the RAMPART Library clause. */
public class RampartLibraryParser2_6 implements Validatable<RampartLibraryBuilder, InvalidRampartRuleException> {
    private final Map<String, RampartList> symbolTable;

    public RampartLibraryParser2_6(Map<String, RampartList> symbolTable) {
        this.symbolTable = symbolTable;
    }

    //@Override
    public RampartLibraryBuilder validate() throws InvalidRampartRuleException {
        return parse(symbolTable);
    }

    public static RampartLibraryBuilder parse(Map<String, RampartList> symbolTable) throws InvalidRampartRuleException {
        final RampartString ruleName = RampartRuleNameParser.getRuleName(symbolTable, RampartLibraryParser2_0.THIS_RULE_KEY);
        RuleStructureValidator.validateDeclarations(
            ruleName, symbolTable,
            RampartLibraryParser2_0.REQUIRED_FIELDS,
            RampartLibraryParser2_0.THIS_RULE_KEYS,
            RampartLibraryParser2_0.SUPPORTED_ACTION_KEYS,
            RampartMetadataParser.DEFAULT_METADATA_KEYS
        );
        RuleStructureValidator.validateRequiredKeys(
            RampartLibraryParser2_0.MODEL_NAME, symbolTable.keySet(),
            RampartLibraryParser2_0.REQUIRED_FIELDS
        );

        final RampartList targetOsList = RampartTargetOsParser.parseTargetOs(symbolTable, RampartLibraryParser2_0.THIS_RULE_KEY);

        return new RampartLibraryBuilder()
                .addRuleName(ruleName)
                .addLibraryList(RampartPathParser.parsePaths(
                        symbolTable, RampartLibraryParser2_0.PATH_ELEMENT_KEY, targetOsList
                ))
                .addAction(RampartActionParser2_3.parseRampartAction(
                        symbolTable, ruleName,
                        RampartLibraryParser2_0.SUPPORTED_ACTIONS
                ))
                .addTargetOSList(targetOsList)
                .addMetadata(RampartMetadataParser.parseRuleMetadata(symbolTable));
    }
}
