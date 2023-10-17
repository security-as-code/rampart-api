package org.rampart.lang.impl.sanitization.validators.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.DETECT_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.PROTECT_KEY;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.impl.core.validators.v2.RampartActionValidator2_0Plus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RampartSanitizationActionValidator extends RampartActionValidator2_0Plus {
    private static final List<RampartConstant> SUPPORTED_ACTIONS = Arrays.asList(DETECT_KEY, PROTECT_KEY);

    public RampartSanitizationActionValidator(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable, RampartRuleType.SANITIZATION);
    }

    public List<RampartConstant> allowedKeys() {
        return SUPPORTED_ACTIONS;
    }

}
