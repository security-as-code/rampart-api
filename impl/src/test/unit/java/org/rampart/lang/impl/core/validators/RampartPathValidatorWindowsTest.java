package org.rampart.lang.impl.core.validators;

import static org.rampart.lang.api.constants.RampartFileSystemConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartInteger;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

import java.util.LinkedHashMap;
import java.util.Map;

import org.rampart.lang.impl.core.validators.v2.RampartPathValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RampartPathValidatorWindowsTest {

    private static final RampartList TARGET_OS_LIST_WITH_WINDOWS = newRampartList(WINDOWS_KEY);
    private Map<String, RampartList> symbolTable;

    @BeforeEach
    public void before() {
        symbolTable = new LinkedHashMap<>();
    }

    @Test
    public void emptyFileListThrowsException() {
        symbolTable.put(READ_KEY.toString(), RampartList.EMPTY);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_WITH_WINDOWS));

        assertThat(thrown.getMessage(), equalTo("\"read\" declaration must not be empty"));
    }

    @Test
    public void invalidPathEntryTypeThrowsException() {
        symbolTable.put(READ_KEY.toString(), newRampartList(newRampartString("C:\\file.txt"), newRampartInteger(2)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_WITH_WINDOWS));

        assertThat(thrown.getMessage(), equalTo("all entries in \"read\" declaration must be quoted strings"));
    }

    @Test
    public void emptyPathEntryThrowsException() {
        symbolTable.put(READ_KEY.toString(), newRampartList(newRampartString("")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_WITH_WINDOWS));

        assertThat(thrown.getMessage(), equalTo("path entry in \"read\" declaration must not be empty"));
    }

    @Test
    public void relativePathThrowsException() {
        String filePath = ".\\file.txt";
        symbolTable.put(READ_KEY.toString(), newRampartList(newRampartString(filePath)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_WITH_WINDOWS));

        assertThat(thrown.getMessage(), equalTo("cannot be a relative path: \"" + filePath + "\""));
    }

    @Test
    public void absolutePathWithDotsThrowsException() {
        String filePath = "C:\\database\\data\\..\\database.cfg";
        symbolTable.put(READ_KEY.toString(), newRampartList(newRampartString(filePath)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_WITH_WINDOWS));

        assertThat(thrown.getMessage(),
                equalTo("filepath: \"" + filePath + "\" contains an illegal character sequence: \".\\\""));
    }

    // double backslash is valid on Windows
    @Test
    public void pathWithMoreThanOneWildcardThrowsException() {
        String filePath = "C:\\DataFiles\\*\\*.dat";
        symbolTable.put(READ_KEY.toString(), newRampartList(newRampartString(filePath)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_WITH_WINDOWS));

        assertThat(thrown.getMessage(),
                equalTo("file path: \"" + filePath + "\" contains more than one" + " wildcard character"));
    }

    @Test
    public void pathToFileNameBeginningWithTwoDotsValidatedSuccessfully() throws Exception {
        String filePath = "C:\\..twoDots.file";
        symbolTable.put(READ_KEY.toString(), newRampartList(newRampartString(filePath)));
        new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_WITH_WINDOWS);
    }

    @Test
    public void notAbsolutePathThrowsException() {
        String filePath = "..\\up\\file.txt";
        symbolTable.put(READ_KEY.toString(), newRampartList(newRampartString(filePath)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_WITH_WINDOWS));

        assertThat(thrown.getMessage(), equalTo("cannot be a relative path: \"" + filePath + "\""));
    }

    @Test
    public void pathToFileNameContainingDotsValidatedSuccessfully() throws Exception {
        String filePath = "C:\\file.with.dot.s";
        symbolTable.put(READ_KEY.toString(), newRampartList(newRampartString(filePath)));
        new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_WITH_WINDOWS);
    }

    @Test
    public void pathToFileNameWithInvalidForwardSlash() {
        String filePath = "C://dir/file.txt";
        symbolTable.put(READ_KEY.toString(), newRampartList(newRampartString(filePath)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_WITH_WINDOWS));

        assertThat(thrown.getMessage(), equalTo("only backslash separator is supported in path: `" + filePath + "`"));
    }
}
