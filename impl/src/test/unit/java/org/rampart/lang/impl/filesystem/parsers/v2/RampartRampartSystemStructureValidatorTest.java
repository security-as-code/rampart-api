package org.rampart.lang.impl.filesystem.parsers.v2;

import static org.rampart.lang.api.constants.RampartFileSystemConstants.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.api.core.RampartSeverity;
import org.rampart.lang.api.filesystem.RampartFileSystemOperation;
import org.rampart.lang.api.core.RampartInput;
import org.rampart.lang.impl.core.RampartActionImpl;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

public class RampartRampartSystemStructureValidatorTest {

    @Test
    public void noOperationRelativeTraversalNoInputsProtectAction() throws InvalidRampartRuleException {
        RampartFileSystemParser2_0.crossValidate(
                RampartFileSystemOperation.NOOP,
                Collections.singletonList(RELATIVE_KEY),
                RampartList.EMPTY,
                action(RampartActionType.PROTECT));
    }

    @Test
    public void noOperationAbsoluteTraversalWithInputsProtectAction() throws InvalidRampartRuleException {
        RampartFileSystemParser2_0.crossValidate(
                RampartFileSystemOperation.NOOP,
                Collections.singletonList(ABSOLUTE_KEY),
                newRampartList(RampartInput.HTTP),
                action(RampartActionType.PROTECT));
    }

    @Test
    public void noOperationAbsoluteAndRelativeTraversalWithInputsProtectAction() throws InvalidRampartRuleException {
        RampartFileSystemParser2_0.crossValidate(
                RampartFileSystemOperation.NOOP,
                Arrays.asList(ABSOLUTE_KEY, RELATIVE_KEY),
                newRampartList(RampartInput.HTTP),
                action(RampartActionType.PROTECT));
    }

    @Test
    public void noOperationRelativeTraversalWithInputsProtectAction() throws InvalidRampartRuleException {
        RampartFileSystemParser2_0.crossValidate(
                RampartFileSystemOperation.NOOP,
                Collections.singletonList(RELATIVE_KEY),
                newRampartList(RampartInput.HTTP, RampartInput.DESERIALIZATION),
                action(RampartActionType.PROTECT));
    }

    @Test
    public void noOperationAbsoluteTraversalNoInputsProtectAction() throws InvalidRampartRuleException {
        RampartFileSystemParser2_0.crossValidate(
                RampartFileSystemOperation.NOOP,
                Collections.singletonList(ABSOLUTE_KEY),
                RampartList.EMPTY,
                action(RampartActionType.PROTECT));
    }

    @Test
    public void noOperationAbsoluteAndRelativeTraversalNoInputsProtectAction() throws InvalidRampartRuleException {
        RampartFileSystemParser2_0.crossValidate(
                RampartFileSystemOperation.NOOP,
                Arrays.asList(ABSOLUTE_KEY, RELATIVE_KEY),
                RampartList.EMPTY,
                action(RampartActionType.PROTECT));
    }

    @Test
    public void noOperationAbsoluteTraversalNoInputsDetectAction() throws InvalidRampartRuleException {
        RampartFileSystemParser2_0.crossValidate(
                RampartFileSystemOperation.NOOP,
                Collections.singletonList(ABSOLUTE_KEY),
                RampartList.EMPTY,
                action(RampartActionType.DETECT));
    }

    @Test
    public void noOperationRelativeTraversalNoInputsDetectAction() throws InvalidRampartRuleException {
        RampartFileSystemParser2_0.crossValidate(
                RampartFileSystemOperation.NOOP,
                Collections.singletonList(RELATIVE_KEY),
                RampartList.EMPTY,
                action(RampartActionType.DETECT));
    }

    @Test
    public void noOperationAbsoluteTraversalWithInputsAllowAction() {
        assertThrows(InvalidRampartRuleException.class, () -> RampartFileSystemParser2_0.crossValidate(
                RampartFileSystemOperation.NOOP,
                Collections.singletonList(ABSOLUTE_KEY),
                newRampartList(RampartInput.HTTP, RampartInput.DESERIALIZATION),
                action(RampartActionType.ALLOW)));
    }

    @Test
    public void noOperationRelativeTraversalWithInputsAllowAction() {
        assertThrows(InvalidRampartRuleException.class, () -> RampartFileSystemParser2_0.crossValidate(
                RampartFileSystemOperation.NOOP,
                Collections.singletonList(RELATIVE_KEY),
                newRampartList(RampartInput.HTTP, RampartInput.DESERIALIZATION),
                action(RampartActionType.ALLOW)));
    }

    @Test
    public void noOperationNoTraversalNoInputProtectAction() {
        assertThrows(InvalidRampartRuleException.class, () -> RampartFileSystemParser2_0.crossValidate(
                RampartFileSystemOperation.NOOP,
                Collections.emptyList(),
                RampartList.EMPTY,
                action(RampartActionType.PROTECT)));
    }

    @Test
    public void readOperationNoTraversalNoInputsAllowAction() throws InvalidRampartRuleException {
        RampartFileSystemParser2_0.crossValidate(
                RampartFileSystemOperation.READ,
                Collections.emptyList(),
                RampartList.EMPTY,
                action(RampartActionType.ALLOW));
    }

    @Test
    public void writeOperationNoTraversalNoInputsAllowAction() throws InvalidRampartRuleException {
        RampartFileSystemParser2_0.crossValidate(
                RampartFileSystemOperation.WRITE,
                Collections.emptyList(),
                RampartList.EMPTY,
                action(RampartActionType.ALLOW));
    }

    @Test
    public void writeOperationNoTraversalNoInputsDetectAction() throws InvalidRampartRuleException {
        RampartFileSystemParser2_0.crossValidate(
                RampartFileSystemOperation.WRITE,
                Collections.emptyList(),
                RampartList.EMPTY,
                action(RampartActionType.DETECT));
    }

    @Test
    public void readOperationNoTraversalNoInputsDetectAction() throws InvalidRampartRuleException {
        RampartFileSystemParser2_0.crossValidate(
                RampartFileSystemOperation.READ,
                Collections.emptyList(),
                RampartList.EMPTY,
                action(RampartActionType.DETECT));
    }

    @Test
    public void readOperationRelativeTraversalNoInputsAllowAction() {
        assertThrows(InvalidRampartRuleException.class, () -> RampartFileSystemParser2_0.crossValidate(
                RampartFileSystemOperation.READ,
                Collections.singletonList(RELATIVE_KEY),
                RampartList.EMPTY,
                action(RampartActionType.ALLOW)));
    }

    @Test
    public void readOperationRelativeTraversalNoInputsProtectAction() {
        assertThrows(InvalidRampartRuleException.class, () -> RampartFileSystemParser2_0.crossValidate(
                RampartFileSystemOperation.READ,
                Collections.singletonList(RELATIVE_KEY),
                RampartList.EMPTY,
                action(RampartActionType.PROTECT)));
    }

    @Test
    public void readOperationAbsoluteTraversalNoInputsProtectAction() {
        assertThrows(InvalidRampartRuleException.class, () -> RampartFileSystemParser2_0.crossValidate(
                RampartFileSystemOperation.READ,
                Collections.singletonList(ABSOLUTE_KEY),
                RampartList.EMPTY,
                action(RampartActionType.PROTECT)));
    }

    @Test
    public void readOperationAbsoluteAndRelativeTraversalNoInputsProtectAction() {
        assertThrows(InvalidRampartRuleException.class, () -> RampartFileSystemParser2_0.crossValidate(
                RampartFileSystemOperation.READ,
                Arrays.asList(ABSOLUTE_KEY, RELATIVE_KEY),
                RampartList.EMPTY,
                action(RampartActionType.PROTECT)));
    }

    @Test
    public void writeOperationNoTraversalWithInputsProtectAction() {
        assertThrows(InvalidRampartRuleException.class, () -> RampartFileSystemParser2_0.crossValidate(
                RampartFileSystemOperation.WRITE,
                Collections.emptyList(),
                newRampartList(RampartInput.HTTP, RampartInput.DESERIALIZATION),
                action(RampartActionType.PROTECT)));
    }

    @Test
    public void writeOperationNoTraversalWithInputsAllowAction() {
        assertThrows(InvalidRampartRuleException.class, () -> RampartFileSystemParser2_0.crossValidate(
                RampartFileSystemOperation.WRITE,
                Collections.emptyList(),
                newRampartList(RampartInput.HTTP, RampartInput.DESERIALIZATION),
                action(RampartActionType.ALLOW)));
    }


    /** Creates a simple no-action operation. */
    private static RampartAction action(RampartActionType actionType) {
        return new RampartActionImpl(actionType, null, RampartSeverity.UNKNOWN, RampartBoolean.FALSE, null);
    }
}
