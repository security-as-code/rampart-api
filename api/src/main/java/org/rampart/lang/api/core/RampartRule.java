package org.rampart.lang.api.core;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;

public interface RampartRule extends RampartObject {

    RampartString getRuleName();

    RampartCode getCode();

    RampartRuleType getRuleType();

    RampartApp getApp();

    RampartList getTargetOSList();

    RampartMetadata getMetadata();
}
