package org.rampart.lang.api.http;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;

public interface RampartOpenRedirect extends RampartObject {
    RampartList getHosts();
    RampartBoolean shouldExcludeSubdomains();
}
