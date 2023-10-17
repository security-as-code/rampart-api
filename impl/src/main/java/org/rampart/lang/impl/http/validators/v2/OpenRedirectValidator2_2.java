package org.rampart.lang.impl.http.validators.v2;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartNamedValue;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.http.RampartOpenRedirect;
import org.rampart.lang.impl.core.RampartOptions;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.v2.RampartConfigMapValidator;
import org.rampart.lang.impl.http.RampartOpenRedirectImpl;
import org.rampart.lang.impl.http.OpenRedirectOptions;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;

import java.util.Map;

import static org.rampart.lang.api.constants.RampartGeneralConstants.OPTIONS_KEY;
import static org.rampart.lang.api.constants.RampartHttpConstants.OPEN_REDIRECT_KEY;

public class OpenRedirectValidator2_2 extends OpenRedirectValidator2_0 {
    protected final RampartList validatableObject;
    protected final RampartConfigMapValidator optionsValidator;
    private static final RampartOptions VALID_OPTIONS = new OpenRedirectOptions();

    public OpenRedirectValidator2_2(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        this.validatableObject = visitorSymbolTable.get(OPEN_REDIRECT_KEY.toString());
        this.optionsValidator = new RampartConfigMapValidator(
                RampartInterpreterUtils.findRampartNamedValue(OPTIONS_KEY, validatableObject), VALID_OPTIONS);
    }

    public RampartOpenRedirect validateRedirectDeclaration() throws InvalidRampartRuleException {
        if (validatableObject == null) {
            return null;
        }
        RampartList openRedirectParameters = validatableObject;
        if (openRedirectParameters.isEmpty() == RampartBoolean.TRUE) {
            return new RampartOpenRedirectImpl();
        }
        RampartObjectIterator it = openRedirectParameters.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject param = it.next();
            if (param instanceof RampartNamedValue && OPTIONS_KEY.equals(((RampartNamedValue) param).getName())) {
                optionsValidator.validateOptions();
            } else {
                throw new InvalidRampartRuleException(
                        "invalid parameter \"" + param + "\" for \"" + OPEN_REDIRECT_KEY + "\" declaration");
            }
        }
        return new RampartOpenRedirectImpl(optionsValidator.getValidatedOptions(OPEN_REDIRECT_KEY));
    }

    @Override
    public RampartBoolean validateRedirect() {
        return validatableObject == null ? RampartBoolean.FALSE : RampartBoolean.TRUE;
    }
}
