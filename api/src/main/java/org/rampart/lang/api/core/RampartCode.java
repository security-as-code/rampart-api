package org.rampart.lang.api.core;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;

public interface RampartCode extends RampartObject {
    RampartConstant getLanguage();

    RampartString getSourceCode();

    RampartList getImports();
}
