package org.rampart.lang.impl.filesystem.parsers.v2;

import static org.rampart.lang.api.constants.RampartFileSystemConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.rampart.lang.api.RampartList;
import org.junit.jupiter.api.Test;

import org.rampart.lang.api.filesystem.RampartFileSystemOperation;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.filesystem.parsers.v2.RampartFileSystemOperationParser;

public class RampartRampartSystemOperationParserTest {

    private static final String TEST_KEY = "test";

    @Test
    public void readAndWriteOperationsAreMissing() throws InvalidRampartRuleException {
        final Map<String, RampartList> symbolTable = new HashMap<>();
        symbolTable.put(TEST_KEY, RampartList.EMPTY);
        RampartFileSystemOperation operation = RampartFileSystemOperationParser.parseOperation(symbolTable);
        assertThat(operation, equalTo(RampartFileSystemOperation.NOOP));
    }

    @Test
    public void noKeysDeclared() throws InvalidRampartRuleException {
        RampartFileSystemOperation operation = RampartFileSystemOperationParser.parseOperation(Collections.emptyMap());
        assertThat(operation, equalTo(RampartFileSystemOperation.NOOP));
    }

    @Test
    public void bothReadAndWriteOperationsDeclared() {
        final Map<String, RampartList> symbolTable = new HashMap<>();
        symbolTable.put(READ_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(WRITE_KEY.toString(), RampartList.EMPTY);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> RampartFileSystemOperationParser.parseOperation(symbolTable));

        assertThat(thrown.getMessage(), equalTo("RAMPART filesystem rules must contain only one of \"read\" or \"write\""));
    }

    @Test
    public void readOperationsDeclared() throws InvalidRampartRuleException {
        final Map<String, RampartList> symbolTable = new HashMap<>();
        symbolTable.put(READ_KEY.toString(), RampartList.EMPTY);
        RampartFileSystemOperationParser.parseOperation(symbolTable);
    }

    @Test
    public void writeOperationsDeclared() throws InvalidRampartRuleException {
        final Map<String, RampartList> symbolTable = new HashMap<>();
        symbolTable.put(WRITE_KEY.toString(), RampartList.EMPTY);
        RampartFileSystemOperationParser.parseOperation(symbolTable);
    }
}
