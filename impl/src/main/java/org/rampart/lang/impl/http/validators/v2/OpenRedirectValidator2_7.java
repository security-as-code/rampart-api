package org.rampart.lang.impl.http.validators.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.OPTIONS_KEY;
import static org.rampart.lang.api.constants.RampartHttpConstants.HOSTS_KEY;
import static org.rampart.lang.api.constants.RampartHttpConstants.OPEN_REDIRECT_KEY;

import java.util.Map;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartNamedValue;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.http.RampartOpenRedirect;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.v2.ConfigValueValidator;
import org.rampart.lang.impl.http.RampartOpenRedirectImpl2_7;

public class OpenRedirectValidator2_7 extends OpenRedirectValidator2_2 {

    public OpenRedirectValidator2_7(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
    }

    @Override
    public RampartOpenRedirect validateRedirectDeclaration() throws InvalidRampartRuleException {
        if (validatableObject == null) {
            return null;
        }
        RampartList openRedirectParameters = validatableObject;
        if (openRedirectParameters.isEmpty() == RampartBoolean.TRUE) {
            return new RampartOpenRedirectImpl2_7();
        }
        RampartList hosts = RampartList.EMPTY;
        RampartObjectIterator it = openRedirectParameters.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject param = it.next();
            if (!(param instanceof RampartNamedValue)) {
                throw new InvalidRampartRuleException(
                        "invalid parameter \"" + param + "\" for \"" + OPEN_REDIRECT_KEY + "\" declaration");
            }
            if (OPTIONS_KEY.equals(((RampartNamedValue) param).getName())) {
                optionsValidator.validateOptions();
            } else if (HOSTS_KEY.equals(((RampartNamedValue) param).getName())) {
                hosts = (RampartList) ConfigValueValidator.SINGLE_OR_LIST_OF_NOT_EMPTY_HOSTS_VALIDATOR.test(
                        ((RampartNamedValue) param).getRampartObject());
                if (hosts == null) {
                    throw new InvalidRampartRuleException(
                            "invalid value of parameter \"" + HOSTS_KEY + "\" for \"" + OPEN_REDIRECT_KEY + "\" declaration");
                }
            }
        }
        return new RampartOpenRedirectImpl2_7(
                optionsValidator.getValidatedOptions(OPEN_REDIRECT_KEY),
                hosts);
    }

}
