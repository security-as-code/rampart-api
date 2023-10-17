package org.rampart.lang.api.filesystem;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.apiprotect.RampartApiFilterRule;
import org.rampart.lang.api.core.RampartActionableRule;
import org.rampart.lang.api.core.RampartDataInputsRule;

public interface RampartFileSystem extends RampartActionableRule, RampartDataInputsRule, RampartApiFilterRule {
    RampartFileSystemOperation getOperation();
    RampartBoolean onPathTraversalRelative();
    RampartBoolean onPathTraversalAbsolute();
    RampartList getPaths();
}
