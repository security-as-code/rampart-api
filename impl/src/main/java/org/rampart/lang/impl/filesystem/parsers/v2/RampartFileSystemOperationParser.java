package org.rampart.lang.impl.filesystem.parsers.v2;

import static org.rampart.lang.api.constants.RampartFileSystemConstants.READ_KEY;
import static org.rampart.lang.api.constants.RampartFileSystemConstants.WRITE_KEY;

import java.util.Map;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.filesystem.RampartFileSystemOperation;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.parsers.RampartPathParser;

public final class RampartFileSystemOperationParser {

    /** Keys supported by this parser. */
    public static final RampartConstant[] SUPPORTED_KEYS = {READ_KEY, WRITE_KEY};

    private RampartFileSystemOperationParser() {
        throw new UnsupportedOperationException();
    }


    /**
     * Validates the key set contains one (and only one) of `read` or `write`
     *
     * @throws InvalidRampartRuleException if both OR neither are present
     */
    public static RampartFileSystemOperation parseOperation(Map<String, RampartList> symbolTable)
            throws InvalidRampartRuleException {
        RampartFileSystemOperation operation = null;
        if (symbolTable.containsKey(READ_KEY.toString())) {
            operation = RampartFileSystemOperation.READ;
        }
        if (symbolTable.containsKey(WRITE_KEY.toString())) {
            if (operation != null) {
                throw new InvalidRampartRuleException(
                        "RAMPART filesystem rules must contain only one of \"" + READ_KEY + "\" or \"" + WRITE_KEY + "\"");
            }
            return RampartFileSystemOperation.WRITE;
        }
        if (operation == null) {
            return RampartFileSystemOperation.NOOP;
        }
        return RampartFileSystemOperation.READ;
    }


    public static RampartList parsePaths(
            Map<String, RampartList> symbolTable,
            RampartFileSystemOperation operation,
            RampartList targetOSList) throws InvalidRampartRuleException {
        if (operation == RampartFileSystemOperation.NOOP) {
            return RampartList.EMPTY;
        }
        return RampartPathParser.parsePaths(symbolTable, operation.getName(), targetOSList);
    }

}
