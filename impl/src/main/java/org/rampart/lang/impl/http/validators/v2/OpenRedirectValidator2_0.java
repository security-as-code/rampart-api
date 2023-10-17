package org.rampart.lang.impl.http.validators.v2;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.FirstClassRuleObjectValidator;

public class OpenRedirectValidator2_0 implements FirstClassRuleObjectValidator {

    protected final Map<String, RampartList> visitorSymbolTable;

    public OpenRedirectValidator2_0(Map<String, RampartList> visitorSymbolTable) {
        this.visitorSymbolTable = visitorSymbolTable;
    }

    public RampartBoolean validateRedirect() throws InvalidRampartRuleException {
        RampartList openRedirectParameters = visitorSymbolTable.get(OPEN_REDIRECT_KEY.toString());
        if (openRedirectParameters == null) {
            return RampartBoolean.FALSE;
        }
        if (openRedirectParameters.isEmpty() == RampartBoolean.FALSE) {
            throw new InvalidRampartRuleException("\"" + OPEN_REDIRECT_KEY + "\" must be an empty declaration");
        }
        return RampartBoolean.TRUE;
    }

    // @Override
    public List<RampartConstant> allowedKeys() {
        return Arrays.asList(OPEN_REDIRECT_KEY);
    }

}
