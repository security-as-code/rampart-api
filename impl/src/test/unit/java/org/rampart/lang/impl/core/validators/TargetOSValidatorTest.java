package org.rampart.lang.impl.core.validators;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.impl.core.parsers.RampartTargetOsParser.SUPPORTED_OPERATING_SYSTEMS;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

public class TargetOSValidatorTest {

    @Test
    public void missingTargetOSListReturnsAnyByDefault() throws Exception {
        assertThat(new TargetOSValidator(null).validateTargetOSList(), equalTo(newRampartList(ANY_KEY)));
    }

    @Test
    public void invalidTargetOSTypeThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new TargetOSValidator(AIX_KEY).validateTargetOSList());

        assertThat(thrown.getMessage(), equalTo("\"os\" declaration must be followed by a non empty list"));
    }

    @Test
    public void emptyTargetOSListThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new TargetOSValidator(RampartList.EMPTY).validateTargetOSList());

        assertThat(thrown.getMessage(), equalTo("\"os\" declaration must be followed by a non empty list"));
    }

    @Test
    public void invalidTargetOSListEntryTypeThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new TargetOSValidator(newRampartList(newRampartInteger(1))).validateTargetOSList());

        assertThat(thrown.getMessage(), equalTo("\"os\" declaration list entries must be constants"));
    }

    @Test
    public void emptyTargetOSListEntryThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new TargetOSValidator(newRampartList(newRampartString(""))).validateTargetOSList());

        assertThat(thrown.getMessage(), equalTo("\"os\" declaration list entries must be constants"));
    }

    @Test
    public void unsupportedOSThrowsException() {
        String unsupportedOS = "OSX";

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new TargetOSValidator(newRampartList(newRampartConstant(unsupportedOS))).validateTargetOSList());

        assertThat(thrown.getMessage(), equalTo("unsupported entry in \"os\": \"" +
                unsupportedOS + "\". Must be one of: " +
                SUPPORTED_OPERATING_SYSTEMS));
    }

    @Test
    public void invalidCaseSupportedOSThrowsException() {
        String supportedButInvalidCaseOS = "LINUX";

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new TargetOSValidator(newRampartList(newRampartConstant(supportedButInvalidCaseOS)))
                        .validateTargetOSList());

        assertThat(thrown.getMessage(), equalTo("unsupported entry in \"os\": \"" + supportedButInvalidCaseOS + "\". Must be one of: "
                + SUPPORTED_OPERATING_SYSTEMS));
    }

    @Test
    public void TargetOSListContainingAllAndOtherOSEntriesSetsListToAll() throws Exception {
        RampartList targetOSList = new TargetOSValidator(
                newRampartList(ANY_KEY, AIX_KEY, LINUX_KEY)).validateTargetOSList();
        assertThat(targetOSList, equalTo(newRampartList(ANY_KEY)));
    }
}
