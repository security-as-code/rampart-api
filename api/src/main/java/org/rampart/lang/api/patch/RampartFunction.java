package org.rampart.lang.api.patch;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;

public interface RampartFunction extends RampartObject {
    RampartString getFunctionName();
    RampartList getChecksums();
}
