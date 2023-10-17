package org.rampart.lang.impl.http.validators.v2;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.rampart.lang.api.*;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.http.RampartCsrf;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.v2.RampartConfigMapValidator;
import org.rampart.lang.impl.core.validators.FirstClassRuleObjectValidator;
import org.rampart.lang.impl.http.RampartCsrfImpl;
import org.rampart.lang.impl.http.RampartCsrfOptions2_5Minus;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;

public class HttpCsrfValidator implements FirstClassRuleObjectValidator {

    private final RampartList csrfRampart;
    private final RampartConfigMapValidator optionsValidator;

    protected HttpCsrfValidator(Map<String, RampartList> visitorSymbolTable, RampartCsrfOptions2_5Minus validOptions) {
        this.csrfRampart = visitorSymbolTable.get(CSRF_KEY.toString());
        this.optionsValidator = new RampartConfigMapValidator(
                RampartInterpreterUtils.findRampartNamedValue(OPTIONS_KEY, csrfRampart), validOptions);
    }

    public RampartCsrf validateCsrfConfiguration() throws InvalidRampartRuleException {
        if (csrfRampart == null) {
            return null;
        }
        RampartConstant csrfAlgorithm = null;
        RampartObjectIterator it = csrfRampart.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject param = it.next();
            if (param instanceof RampartConstant
                    && (SYNCHRONIZED_TOKENS_KEY.equals(param)
                            || SAME_ORIGIN_KEY.equals(param))) {
                if (csrfAlgorithm != null) {
                    throw new InvalidRampartRuleException(
                            "duplicate \"" + CSRF_KEY + "\" parameter specified \"" + param + "\"");
                }
                csrfAlgorithm = (RampartConstant) param;
            } else if (param instanceof RampartNamedValue
                    && OPTIONS_KEY.equals(((RampartNamedValue) param).getName())) {
                optionsValidator.validateOptions();
            } else {
                throw new InvalidRampartRuleException(
                        "invalid parameter \"" + param + "\" for \"" + CSRF_KEY + "\" declaration");
            }
        }
        if (csrfAlgorithm == null) {
            throw new InvalidRampartRuleException("missing declaration of \"" + CSRF_KEY + "\" algorithm type");
        }
        return new RampartCsrfImpl(csrfAlgorithm, optionsValidator.getValidatedOptions(csrfAlgorithm));
    }

    // @Override
    public List<RampartConstant> allowedKeys() {
        return Arrays.asList(CSRF_KEY);
    }
}
