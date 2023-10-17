package org.rampart.lang.api.patch;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;

public interface RampartLocation extends RampartObject {
    /**
     * Note target may be one of {RampartInteger, RampartString, null} for
     * {(line, instruction), (error, call, read, write), (entry, exit)} respectively
     */
    RampartObject getTarget();
    RampartPatchType getType();
    RampartList getOccurrences();

}
