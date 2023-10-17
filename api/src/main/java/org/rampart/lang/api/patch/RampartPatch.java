package org.rampart.lang.api.patch;

import org.rampart.lang.api.core.RampartRule;

public interface RampartPatch extends RampartRule {

    RampartFunction getFunction();

    RampartLocation getLocation();
}
