package org.rampart.lang.impl.marshal.validators.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.ALLOW_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.DETECT_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.PROTECT_KEY;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RampartMarshalActionValidator2_6 extends RampartMarshalActionValidator2_3 {

    public RampartMarshalActionValidator2_6(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
    }

    // @Override
    public List<RampartConstant> allowedKeys() {
        return Arrays.asList(DETECT_KEY, PROTECT_KEY, ALLOW_KEY);
    }

}
