package org.rampart.lang.impl.filesystem;

import static org.rampart.lang.api.constants.RampartFileSystemConstants.*;

import static org.rampart.lang.api.core.RampartRuleType.FILESYSTEM;

import org.rampart.lang.api.*;
import org.rampart.lang.api.apiprotect.RampartApiFilter;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.api.filesystem.RampartFileSystem;
import org.rampart.lang.api.filesystem.RampartFileSystemOperation;
import org.rampart.lang.impl.apiprotect.writers.RampartApiFilterWriter;
import org.rampart.lang.impl.core.RampartActionableRuleBase;
import org.rampart.lang.impl.core.writers.v2.RampartInputWriter;
import org.rampart.lang.impl.utils.ObjectUtils;

import java.util.List;

/**
 * Class to model an Rampart filesystem rule
 * Eg.
 *  filesystem("deny /etc/shadow reading rule"):
 *      read("/etc/shadow")
 *      protect(message: "an attempt to read /etc/shadow was made", severity: 10)
 *  endfilesystem
 * @since 1.4
 */
public class RampartFileSystemImpl extends RampartActionableRuleBase implements RampartFileSystem {
    private final RampartFileSystemOperation operation;
    private final RampartList paths;
    private final RampartList inputs;
    private RampartBoolean onRelativeTraversal = RampartBoolean.FALSE;
    private RampartBoolean onAbsoluteTraversal = RampartBoolean.FALSE;
    private final RampartApiFilter apiFilter;
    private final String toStringValue;
    private final int hashCode;

    public RampartFileSystemImpl(RampartString appName, RampartString ruleName, RampartFileSystemOperation operation, RampartList paths, RampartList inputs,
                                 List<RampartConstant> traversalOptions, RampartAction action, RampartList targetOSList, RampartMetadata metadata,
                                 RampartApiFilter apiFilter) {
        super(appName, ruleName, action, targetOSList, metadata);
        this.operation = operation;
        this.paths = paths;
        this.inputs = inputs;
        for (RampartConstant option : traversalOptions) {
            if (option.equals(RELATIVE_KEY)) {
                onRelativeTraversal = RampartBoolean.TRUE;
            } else if (option.equals(ABSOLUTE_KEY)) {
                onAbsoluteTraversal = RampartBoolean.TRUE;
            }
        }
        this.apiFilter = apiFilter;
        this.toStringValue = super.toString();
        this.hashCode = ObjectUtils.hash(operation, paths, inputs, apiFilter, super.hashCode());
    }

    // @Override
    public RampartFileSystemOperation getOperation() {
        return operation;
    }

    // @Override
    public RampartList getPaths() {
        return paths;
    }

    // @Override
    public RampartList getDataInputs() {
        return inputs;
    }

    // @Override
    public RampartBoolean onPathTraversalRelative() {
        return onRelativeTraversal;
    }

    // @Override
    public RampartBoolean onPathTraversalAbsolute() {
        return onAbsoluteTraversal;
    }

    // @Override
    public RampartRuleType getRuleType() {
        return FILESYSTEM;
    }

    // @Override
    public RampartApiFilter getApiFilter() {
        return apiFilter;
    }

    @Override
    public String toString() {
        return toStringValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartFileSystemImpl)) {
            return false;
        }
        RampartFileSystemImpl otherFileSystem = (RampartFileSystemImpl) other;
        return ObjectUtils.equals(operation, otherFileSystem.operation)
                && ObjectUtils.equals(paths, otherFileSystem.paths)
                && ObjectUtils.equals(inputs, otherFileSystem.inputs)
                && ObjectUtils.equals(apiFilter, otherFileSystem.apiFilter)
                && super.equals(otherFileSystem);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    protected void appendRuleBody(StringBuilder builder) {
        appendOperation(builder);
        RampartApiFilterWriter.appendTo(builder, apiFilter);
        appendInput(builder);
        appendTraversalOption(builder);
        super.appendRuleBody(builder);
    }

    private void appendOperation(StringBuilder builder) {
        if(operation == RampartFileSystemOperation.NOOP) {
            return;
        }
        builder.append("\t").append(operation).append('(').append(pathListToString()).append(')').append(LINE_SEPARATOR);
    }

    private void appendInput(StringBuilder builder) {
        RampartInputWriter.appendTo(builder, inputs);
    }

    private void appendTraversalOption(StringBuilder builder) {
        if (onRelativeTraversal == RampartBoolean.FALSE
                && onAbsoluteTraversal == RampartBoolean.FALSE) {
            return;
        }
        builder.append('\t').append(TRAVERSAL_KEY).append('(');
        if (onRelativeTraversal == RampartBoolean.FALSE
                || onAbsoluteTraversal == RampartBoolean.FALSE) {
            if (onRelativeTraversal == RampartBoolean.TRUE) {
                builder.append(RELATIVE_KEY);
            } else {
                builder.append(ABSOLUTE_KEY);
            }
        }
        builder.append(')').append(LINE_SEPARATOR);
    }

    /**
     * Creates String representation of the path list
     * Format: <filesys-op>("path1", "path2", "path3")
     * @return String - representation of the path list
     */
    private String pathListToString() {
        StringBuilder builder = new StringBuilder();
        String delim = "";
        RampartObjectIterator it = paths.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject path = it.next();
            builder.append(delim)
                   .append(path instanceof RampartString ? ((RampartString) path).formatted() : path);
            delim = ", ";
        }
        return builder.toString();
    }
}
