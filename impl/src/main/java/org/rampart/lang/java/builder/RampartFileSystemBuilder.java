package org.rampart.lang.java.builder;

import static org.rampart.lang.api.constants.RampartFileSystemConstants.ABSOLUTE_KEY;
import static org.rampart.lang.api.constants.RampartFileSystemConstants.RELATIVE_KEY;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.apiprotect.RampartApiFilter;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartCode;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.filesystem.RampartFileSystem;
import org.rampart.lang.api.filesystem.RampartFileSystemOperation;
import org.rampart.lang.api.core.RampartInput;
import org.rampart.lang.impl.filesystem.RampartFileSystemImpl;
import org.rampart.lang.impl.core.validators.v2.RampartPathValidator;
import org.rampart.lang.java.parser.RampartSingleAppVisitor;

import java.util.ArrayList;

/**
 * Class used by the validators to build an RampartFileSystemImpl
 * @see RampartFileSystemImpl
 * @see RampartSingleAppVisitor
 */
public class RampartFileSystemBuilder implements RampartRuleBuilder<RampartFileSystem> {
    private RampartString ruleName;
    private RampartFileSystemOperation operation;
    private RampartList paths = RampartList.EMPTY;
    private RampartList inputs = RampartList.EMPTY;
    private final ArrayList<RampartConstant> traversalOptions = new ArrayList<RampartConstant>();
    private RampartAction action;
    private RampartList targetOSList;
    private RampartMetadata metadata;
    private RampartApiFilter apiFilter;

    /**
     * For use in RampartFileSystemOperationValidator
     * @see RampartPathValidator
     */
    private RampartString operationKey;

    public RampartString getOperationKey() {
        return operationKey;
    }

    // @Override
    public RampartFileSystem createRampartRule(RampartString appName) {
        return new RampartFileSystemImpl(appName,
                ruleName,
                operation,
                paths,
                getInputs(),
                traversalOptions,
                action,
                targetOSList,
                metadata,
                apiFilter);
    }

    //@Override
    public RampartFileSystemBuilder addRuleName(RampartString ruleName) {
        this.ruleName = ruleName;
        return this;
    }

    //@Override
    public RampartFileSystemBuilder addCode(RampartCode code) {
        // Support to be added going forward
        return this;
    }

    public RampartFileSystemBuilder addAction(RampartAction action) {
        this.action = action;
        return this;
    }

    /**
     * @param targetOSList non empty RampartList of RampartConstants
     * @return
     */
    //@Override
    public RampartFileSystemBuilder addTargetOSList(RampartList targetOSList) {
        this.targetOSList = targetOSList;
        return this;
    }

    public RampartFileSystemBuilder addMetadata(RampartMetadata metadata) {
        this.metadata = metadata;
        return this;
    }

    public RampartFileSystemBuilder addFileOperation(RampartFileSystemOperation operation) {
        this.operation = operation;
        return this;
    }

    /**
     * @param filePaths non empty RampartList of RampartStrings
     * @return
     */
    public RampartFileSystemBuilder addFilePaths(RampartList filePaths) {
        this.paths = filePaths;
        return this;
    }

    /**
     * @param inputs non empty RampartList of RampartInputs
     * @return
     */
    public RampartFileSystemBuilder addDataInputs(RampartList inputs) {
        this.inputs = inputs;
        return this;
    }

    public RampartFileSystemBuilder addApiFilter(RampartApiFilter apiFilter) {
        this.apiFilter = apiFilter;
        return this;
    }

    public RampartFileSystemBuilder protectOnRelativePaths() {
        this.traversalOptions.add(RELATIVE_KEY);
        return this;
    }

    public RampartFileSystemBuilder protectOnAbsolutePaths() {
        this.traversalOptions.add(ABSOLUTE_KEY);
        return this;
    }

    public void setOperationKey(RampartString fileOperationKey) {
        this.operationKey = fileOperationKey;
    }


    /**
     * Returns taintintg input list to be used with the rule.
     * The rules are:
     *  * In traversal cases HTTP input is default unless the input clause is explicitly provided
     *  * In read/write rules (RAMPART/2.9) inputs are taken as provided.
     */
    private RampartList getInputs() {
        /* No traversal enabled, should be read/write. */
        if (this.traversalOptions.isEmpty()) {
            return inputs;
        }

        /* Traversal rule. This one defaults to HTTP. */
        return inputs.isEmpty() == RampartBoolean.TRUE ? newRampartList(RampartInput.HTTP) : inputs;
    }
}
