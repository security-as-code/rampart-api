package org.rampart.lang.impl.http.validators.v2;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.validators.v2.RampartConfigMapValidator;
import org.rampart.lang.impl.http.RampartXssOptions2_5;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;

import java.util.Map;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;

public class HttpXssValidator2_5 extends HttpXssValidator2_0 {
    protected HttpXssValidator2_5(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        optionsValidator = new RampartConfigMapValidator(
                RampartInterpreterUtils.findRampartNamedValue(OPTIONS_KEY, xssRampart), new RampartXssOptions2_5());
    }

}
