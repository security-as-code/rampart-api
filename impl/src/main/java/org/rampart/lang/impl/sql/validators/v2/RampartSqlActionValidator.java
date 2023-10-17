package org.rampart.lang.impl.sql.validators.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.impl.core.validators.v2.RampartActionValidator2_0Plus;

public class RampartSqlActionValidator extends RampartActionValidator2_0Plus {
    public static final List<RampartConstant> SUPPORTED_ACTIONS = Arrays.asList(ALLOW_KEY, DETECT_KEY, PROTECT_KEY);

    public RampartSqlActionValidator(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable, RampartRuleType.SQL);
    }

    public List<RampartConstant> allowedKeys() {
        return SUPPORTED_ACTIONS;
    }
}
