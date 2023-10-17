package org.rampart.lang.impl.factory;

import java.util.Map;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartVersion;
import org.rampart.lang.impl.core.RampartVersionImpl;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.impl.sql.validators.v2.RampartSqlValidator2_6;
import org.rampart.lang.java.builder.RampartSqlBuilder;
import org.rampart.lang.impl.sql.validators.v2.RampartSqlValidator2_0;
import org.rampart.lang.impl.sql.validators.v2.RampartSqlValidator2_3;
import org.rampart.lang.impl.sql.validators.v2.RampartSqlValidator2_4;

public class RampartSqlValidatorFactory {

    public static Validatable<RampartSqlBuilder, InvalidRampartRuleException> createValidator(RampartVersion version,
                                                                                              Map<String, RampartList> visitorSymbolTable) {
        if (version.isWithinRange(RampartVersionImpl.v2_0, RampartVersionImpl.v2_2) == RampartBoolean.TRUE) {
            return new RampartSqlValidator2_0(visitorSymbolTable);
        } else if (version.equals(RampartVersionImpl.v2_3)) {
            return new RampartSqlValidator2_3(visitorSymbolTable);
        } else if (version.isWithinRange(RampartVersionImpl.v2_4, RampartVersionImpl.v2_5) == RampartBoolean.TRUE) {
            return new RampartSqlValidator2_4(visitorSymbolTable);
        } else if (version.greaterOrEqualThan(RampartVersionImpl.v2_6) == RampartBoolean.TRUE) {
            return new RampartSqlValidator2_6(visitorSymbolTable);
        }
        return null;
    }

}
