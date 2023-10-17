package org.rampart.lang.api.constants;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.api.process.RampartProcessOperation;

public final class RampartProcessConstants extends RampartGeneralConstants {
    public static final RampartConstant PROCESS_KEY = RampartRuleType.PROCESS.getName();
    public static final RampartConstant EXECUTE_KEY = RampartProcessOperation.EXECUTE.getName();
}
