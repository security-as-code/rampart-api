package org.rampart.lang.api.sanitization;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartObject;

public interface RampartUndetermined extends RampartObject {
    RampartBoolean isSafe();
    // @since 2.8
    RampartBoolean shouldLog();
}
