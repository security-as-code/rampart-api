package org.rampart.lang.api.sanitization;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;

public interface RampartIgnore extends RampartObject {

    RampartList getPayload();

    RampartList getAttribute();
}
