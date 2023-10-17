package org.rampart.lang.api.constants;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.api.library.RampartLibraryOperation;

public final class RampartLibraryConstants extends RampartGeneralConstants {

    public static final RampartConstant LIBRARY_KEY = RampartRuleType.LIBRARY.getName();
    public static final RampartConstant LOAD_KEY = RampartLibraryOperation.LOAD.getName();
}
