package org.rampart.lang.api.constants;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.api.patch.RampartPatchType;

/**
 * Class to store constant values specific to Rampart patches
 */
public final class RampartPatchConstants extends RampartGeneralConstants {
    private RampartPatchConstants() {}

    public static final RampartConstant PATCH_KEY = RampartRuleType.PATCH.getName();
    public static final RampartConstant CHECKSUMS_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "checksums";
        }
    };

    public static final RampartConstant FUNCTION_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "function";
        }
    };

    public static final RampartConstant OCCURRENCES_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "occurrences";
        }
    };

    // Location specifiers
    public static final RampartConstant CALL_KEY = RampartPatchType.CALL.getName();
    public static final RampartConstant READ_KEY = RampartPatchType.READ.getName();
    public static final RampartConstant WRITE_KEY = RampartPatchType.WRITE.getName();
    public static final RampartConstant ERROR_KEY = RampartPatchType.ERROR.getName();
}
