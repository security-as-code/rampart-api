package org.rampart.lang.impl.core.validators.v2;

import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.parsers.RampartPathParser;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;

import java.util.Map;

/**
 * Class to validate a path in RAMPART which can support wildcards
 * Eg.
 * "/etc/shadow", "/etc/passwd", "/bin/*"
 */
public class RampartPathValidator {

    private final Map<String, RampartList> visitorSymbolTable;

    public RampartPathValidator(Map<String, RampartList> visitorSymbolTable) {
        this.visitorSymbolTable = visitorSymbolTable;
    }

    public RampartList validatePaths(RampartConstant pathKey, RampartList targetOSList) throws InvalidRampartRuleException {
        return RampartPathParser.parsePaths(visitorSymbolTable, pathKey, targetOSList);
    }
}
