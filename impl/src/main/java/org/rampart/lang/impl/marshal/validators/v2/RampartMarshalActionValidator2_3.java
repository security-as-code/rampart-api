package org.rampart.lang.impl.marshal.validators.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.DETECT_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.PROTECT_KEY;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.impl.core.validators.v2.RampartActionValidator2_3Plus;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RampartMarshalActionValidator2_3 extends RampartActionValidator2_3Plus {

    public RampartMarshalActionValidator2_3(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable, RampartRuleType.MARSHAL);
    }

    // @Override
    public List<RampartConstant> allowedKeys() {
        return Arrays.asList(DETECT_KEY, PROTECT_KEY);
    }

}
