package org.rampart.lang.api.core;

import org.rampart.lang.api.RampartFloat;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;

public interface RampartCvss extends RampartObject {
    RampartFloat getScore();
    RampartFloat getVersion();
    RampartString getVector();
}
