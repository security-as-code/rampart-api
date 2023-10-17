package org.rampart.lang.impl.http.validators.v2;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.rampart.lang.api.*;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.http.RampartXss;
import org.rampart.lang.impl.core.RampartOptions;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.v2.RampartConfigMapValidator;
import org.rampart.lang.impl.core.validators.FirstClassRuleObjectValidator;
import org.rampart.lang.impl.http.RampartXssImpl;
import org.rampart.lang.impl.http.RampartXssOptions2_0;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;

public class HttpXssValidator2_0 implements FirstClassRuleObjectValidator {

    protected final RampartList xssRampart;
    protected RampartConfigMapValidator optionsValidator;
    private static final RampartOptions VALID_OPTIONS = new RampartXssOptions2_0();

    protected HttpXssValidator2_0(Map<String, RampartList> visitorSymbolTable) {
        this.xssRampart = visitorSymbolTable.get(XSS_KEY.toString());
        this.optionsValidator = new RampartConfigMapValidator(
                RampartInterpreterUtils.findRampartNamedValue(OPTIONS_KEY, xssRampart), VALID_OPTIONS);
    }

    public RampartXss validateXssConfiguration() throws InvalidRampartRuleException {
        if (xssRampart == null) {
            return null;
        }
        boolean foundHtmlKey = false;
        RampartObjectIterator it = xssRampart.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject param = it.next();
            if (param instanceof RampartNamedValue
                    && OPTIONS_KEY.equals(((RampartNamedValue) param).getName())) {
                optionsValidator.validateOptions();
            } else if (param instanceof RampartConstant
                    && HTML_KEY.equals(param)) {
                if (foundHtmlKey) {
                    throw new InvalidRampartRuleException(
                            "duplicate \"" + XSS_KEY + "\" parameter specified \"" + param + "\"");
                }
                foundHtmlKey = true;
            } else {
                throw new InvalidRampartRuleException(
                        "invalid parameter \"" + param + "\" for \"" + XSS_KEY + "\" declaration");
            }
        }
        if (!foundHtmlKey) {
            throw new InvalidRampartRuleException("missing declaration of \"" + XSS_KEY + "\" key");
        }
        return new RampartXssImpl(optionsValidator.getValidatedOptions(HTML_KEY));
    }

    // @Override
    public List<RampartConstant> allowedKeys() {
        return Collections.singletonList(XSS_KEY);
    }

}
