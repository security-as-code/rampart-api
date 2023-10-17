package org.rampart.lang.api.http;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;

public interface RampartXss extends RampartObject {
    RampartList getConfigMap();
}
