package org.rampart.lang.api.sql;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;

public interface RampartVendor extends RampartObject {
    RampartConstant getName();

    RampartList getOptions();
}
