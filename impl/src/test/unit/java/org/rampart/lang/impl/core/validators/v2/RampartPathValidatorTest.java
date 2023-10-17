package org.rampart.lang.impl.core.validators.v2;

import static org.rampart.lang.api.constants.RampartFileSystemConstants.*;
import static org.rampart.lang.api.constants.RampartHttpConstants.PATHS_KEY;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RampartPathValidatorTest {

    private static final RampartList TARGET_OS_LIST_LINUX = newRampartList(LINUX_KEY);
    private static final RampartList TARGET_OS_LIST_WINDOWS_LINUX = newRampartList(WINDOWS_KEY, LINUX_KEY);
    private static final RampartList TARGET_OS_LIST_ANY = newRampartList(ANY_KEY);
    private Map<String, RampartList> symbolTable;

    @BeforeEach
    public void setup() {
        symbolTable = new LinkedHashMap<>();
    }

    @Test
    public void emptyFileListThrowsException() {
        symbolTable.put(READ_KEY.toString(), RampartList.EMPTY);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_LINUX));

        assertThat(thrown.getMessage(), equalTo("\"read\" declaration must not be empty"));
    }

    @Test
    public void foreignTypesThrowsExceptionAnyTargetOS() {
        symbolTable.put(READ_KEY.toString(), newRampartList(
                newRampartNamedValue(PATHS_KEY, newRampartString("/etc/shadow"))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_ANY));

        assertThat(thrown.getMessage(), equalTo("all entries in \"read\" declaration must be quoted strings"));
    }

    @Test
    public void invalidPathEntryTypeThrowsException() {
        symbolTable.put(READ_KEY.toString(), newRampartList(newRampartString("/etc/shadow"), newRampartInteger(2)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_LINUX));

        assertThat(thrown.getMessage(), equalTo("all entries in \"read\" declaration must be quoted strings"));
    }

    @Test
    public void emptyPathEntryThrowsException() {
        symbolTable.put(READ_KEY.toString(), newRampartList(newRampartString("")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_LINUX));

        assertThat(thrown.getMessage(), equalTo("path entry in \"read\" declaration must not be empty"));
    }

    @Test
    public void relativePathThrowsException() {
        String filePath = "./etc/shadow";
        symbolTable.put(READ_KEY.toString(), newRampartList(newRampartString(filePath)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_LINUX));

        assertThat(thrown.getMessage(), equalTo("cannot be a relative path: \"" + filePath + "\""));
    }

    @Test
    public void absolutePathWithDotsThrowsException() {
        String filePath = "/etc/../root";
        symbolTable.put(READ_KEY.toString(), newRampartList(newRampartString(filePath)));
        RampartPathValidator validator = new RampartPathValidator(symbolTable);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> validator.validatePaths(READ_KEY, TARGET_OS_LIST_LINUX));

        assertThat(thrown.getMessage(),
                equalTo("filepath: \"" + filePath + "\" contains an illegal character sequence: \"./\""));
    }

    @Test
    public void doubleSlashInPathThrowsException() {
        String filePath = "/etc//";
        symbolTable.put(READ_KEY.toString(), newRampartList(newRampartString(filePath)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_LINUX));

        assertThat(thrown.getMessage(), equalTo("double or trailing slash present in the path: `" + filePath + "`"));
    }

    @Test
    public void pathWithMoreThanOneWildcardThrowsException() {
        String filePath = "/root/*/*secrets";
        symbolTable.put(READ_KEY.toString(), newRampartList(newRampartString(filePath)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_LINUX));

        assertThat(thrown.getMessage(), equalTo("file path: \"" + filePath + "\" contains more than one"
                + " wildcard character"));
    }

    @Test
    public void pathWithWildcardedDirectory() throws Exception {
        String filePath = "/usr/lib/*";
        symbolTable.put(READ_KEY.toString(), newRampartList(newRampartString(filePath)));
        new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_LINUX);
    }

    @Test
    public void pathToHiddenFileValidatedSuccessfully() throws Exception {
        String filePath = "/home/users/users/.bashrc";
        symbolTable.put(READ_KEY.toString(), newRampartList(newRampartString(filePath)));
        new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_LINUX);
    }

    @Test
    public void pathToFileNameBeginningWithTwoDotsValidatedSuccessfully() throws Exception {
        String filePath = "/home/users/users/..bashrc";
        symbolTable.put(READ_KEY.toString(), newRampartList(newRampartString(filePath)));
        new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_LINUX);
    }

    @Test
    public void pathToFileNameContainingDotsValidatedSuccessfully() throws Exception {
        String filePath = "/home/users/users/bas.h.rc";
        symbolTable.put(READ_KEY.toString(), newRampartList(newRampartString(filePath)));
        new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_LINUX);
    }

    @Test
    public void invalidPathForAnyTargetOS() throws Exception {
        String filePath = "\\home\\users\\users";
        symbolTable.put(READ_KEY.toString(), newRampartList(newRampartString(filePath)));
        new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_ANY);
    }

    @Test
    public void validWindowsPathForAnyTargetOS() throws Exception {
        String filePath = "C:\\file.txt";
        symbolTable.put(READ_KEY.toString(), newRampartList(newRampartString(filePath)));
        new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_ANY);
    }

    @Test
    public void validUnixPathForAnyTargetOS() throws Exception {
        String filePath = "/usr/lib/*";
        symbolTable.put(READ_KEY.toString(), newRampartList(newRampartString(filePath)));
        new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_ANY);
    }

    @Test
    public void invalidPathForWindowsAndLinuxTargetOS() throws Exception {
        String filePath = "\\home\\users\\users";
        symbolTable.put(READ_KEY.toString(), newRampartList(newRampartString(filePath)));
        new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_WINDOWS_LINUX);
    }

    @Test
    public void validWindowsPathForWindowsAndLinuxTargetOS() throws Exception {
        String filePath = "C:\\file.txt";
        symbolTable.put(READ_KEY.toString(), newRampartList(newRampartString(filePath)));
        new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_WINDOWS_LINUX);
    }

    @Test
    public void validUnixPathForWindowsAndLinuxTargetOS() throws Exception {
        String filePath = "/usr/lib/*";
        symbolTable.put(READ_KEY.toString(), newRampartList(newRampartString(filePath)));
        new RampartPathValidator(symbolTable).validatePaths(READ_KEY, TARGET_OS_LIST_WINDOWS_LINUX);
    }

}
