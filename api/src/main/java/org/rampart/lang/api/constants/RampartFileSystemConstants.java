package org.rampart.lang.api.constants;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.api.filesystem.RampartFileSystemOperation;

public final class RampartFileSystemConstants extends RampartGeneralConstants {

    public static final RampartConstant FILESYSTEM_KEY = RampartRuleType.FILESYSTEM.getName();
    public static final RampartConstant READ_KEY = RampartFileSystemOperation.READ.getName();
    public static final RampartConstant WRITE_KEY = RampartFileSystemOperation.WRITE.getName();

    public static final RampartConstant TRAVERSAL_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "traversal";
        }
    };
    public static final RampartConstant RELATIVE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "relative";
        }
    };
    public static final RampartConstant ABSOLUTE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "absolute";
        }
    };
}
