package org.rampart.lang.api.core;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;

public interface RampartAffectedProductVersion extends RampartObject {

    RangeIterator getRangeIterator();

    interface Range extends RampartObject {
        RampartString getFrom();
        RampartString getTo();
    }

    interface RangeIterator extends RampartObject {
        RampartBoolean hasNext();
        Range next();
    }
}
