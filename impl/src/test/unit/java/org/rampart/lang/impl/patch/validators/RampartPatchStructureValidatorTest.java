package org.rampart.lang.impl.patch.validators;

import static org.rampart.lang.api.constants.RampartPatchConstants.*;

import static org.rampart.lang.impl.patch.validators.RampartPatchStructureValidator.PATCH_LOCATION_FIELDS;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.patch.RampartPatchType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RampartPatchStructureValidatorTest {
    private static final String LOCATION_KEY = "entry";
    private Map<String, RampartList> symbolTable;

    @BeforeEach
    public void setup() {
        symbolTable = new LinkedHashMap<>();
        symbolTable.put(PATCH_KEY.toString(), newRampartList(newRampartString("")));
        symbolTable.put(FUNCTION_KEY.toString(), newRampartList(newRampartString("")));
        symbolTable.put(CODE_KEY.toString(), newRampartList(newRampartString("")));
        symbolTable.put(SOURCE_CODE_KEY.toString(), newRampartList(newRampartString("")));
        symbolTable.put(LOCATION_KEY, null);
    }

    @Test
    public void patchKeysetValidatesSuccessfully() throws InvalidRampartRuleException {
        new RampartPatchStructureValidator(symbolTable).validatePatchStructure();
    }

    @Test
    public void missingMandatoryKeyThrowsException() {
        symbolTable.remove(PATCH_KEY.toString());

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartPatchStructureValidator(symbolTable).validatePatchStructure());

        assertThat(thrown.getMessage(), startsWith("missing one of the patch elements:"));
    }

    @Test
    public void missingLocationThrowsException() {
        symbolTable.remove("entry");

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartPatchStructureValidator(symbolTable).validatePatchStructure());

        assertThat(thrown.getMessage(), startsWith("invalid location specifier, patch must contain only one of"));
    }

    @Test
    public void keysetContainingInvalidElementThrowsException() {
        symbolTable.put("Invalid Key", newRampartList(newRampartString("")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartPatchStructureValidator(symbolTable).validatePatchStructure());

        assertThat(thrown.getMessage(), startsWith("unsupported element found. All patch elements must be one of "));
    }

    @Test
    public void emptySymbolTableThrowsException() {
        symbolTable.clear();

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartPatchStructureValidator(symbolTable).validatePatchStructure());

        assertThat(thrown.getMessage(), equalTo("missing one of the patch elements: [patch, function, code, src]"));
    }

    @Test
    public void locationKeyReturnedWhenPatchStructureValidatesSuccessfully() throws InvalidRampartRuleException {
        RampartPatchType patchType = new RampartPatchStructureValidator(symbolTable)
                .validatePatchStructure();
        assertThat(patchType, equalTo(RampartPatchType.fromRampartString(newRampartConstant(LOCATION_KEY))));
    }

    @Test
    public void duplicateValidLocationInSymbolTableThrowsException() {
        symbolTable.put("call", newRampartList(newRampartString("com/foo/bar.fn()V")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartPatchStructureValidator(symbolTable).validatePatchStructure());

        assertThat(thrown.getMessage(), startsWith("invalid location specifier, patch must contain only one of"));
    }

    @Test
    public void ketSetContainingMoreThanOneLocationSpecifierKeyThrowsException() {
        symbolTable.put("exit", RampartList.EMPTY);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartPatchStructureValidator(symbolTable).validatePatchStructure());

        assertThat(thrown.getMessage(),
                equalTo("invalid location specifier, patch must contain only one of " + PATCH_LOCATION_FIELDS));
    }
}
