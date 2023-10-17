package org.rampart.lang.api.socket;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;

public interface RampartAddress extends RampartObject {
    RampartBoolean hasHostname();
    RampartString getHostname();
    RampartBoolean hasIpAddress();
    RampartString getIpAddress();
}
