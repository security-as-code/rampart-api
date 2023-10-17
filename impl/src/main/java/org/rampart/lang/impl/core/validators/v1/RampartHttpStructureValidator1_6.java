package org.rampart.lang.impl.core.validators.v1;

import static org.rampart.lang.java.RampartPrimitives.newRampartList;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.http.RampartHttpIOType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.http.validators.v1.RampartHttpStructureValidatorUpTo1_5;

import java.util.Map;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;

@Deprecated
public class RampartHttpStructureValidator1_6 extends RampartHttpStructureValidatorUpTo1_5 {

    private static final RampartList VALID_HTTP_SYMBOL_TABLE_KEYS =
            newRampartList(HTTP_KEY, REQUEST_KEY, RESPONSE_KEY, INJECTION_KEY, VALIDATE_KEY, ACTION_KEY);

    public RampartHttpStructureValidator1_6(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
    }

    @Override
    public RampartHttpIOType validateHttpStructure() throws InvalidRampartRuleException {
        if (keySetContainsInvalidKey(VALID_HTTP_SYMBOL_TABLE_KEYS)) {
            throw new InvalidRampartRuleException("unsupported declaration found. "
                    + "All http elements must be one of: "
                    + VALID_HTTP_SYMBOL_TABLE_KEYS);
        }
        ensureMandatoryKeysPresent();
        return validateHttpIOType();
    }

    private RampartHttpIOType validateHttpIOType() throws InvalidRampartRuleException {
        if (!(visitorSymbolTable.containsKey(REQUEST_KEY.toString()) ^ visitorSymbolTable.containsKey(RESPONSE_KEY.toString()))) {
            throw new InvalidRampartRuleException(
                    "a single declaration must be specified: \"" + REQUEST_KEY + "\" or \"" + RESPONSE_KEY + "\"");
        }
        if (visitorSymbolTable.containsKey(REQUEST_KEY.toString())) {
            if (visitorSymbolTable.containsKey(INJECTION_KEY.toString())) {
                throw new InvalidRampartRuleException(
                        "\"" + REQUEST_KEY + "\" declaration is only supported with \"" + VALIDATE_KEY
                                + "\" declaration");
            }
            return RampartHttpIOType.REQUEST;
        }
        if (visitorSymbolTable.containsKey(VALIDATE_KEY.toString())) {
            throw new InvalidRampartRuleException(
                    "\"" + RESPONSE_KEY + "\" declaration is only supported with \"" + INJECTION_KEY
                            + "\" declaration");
        }
        return RampartHttpIOType.RESPONSE;
    }

    private void ensureMandatoryKeysPresent() throws InvalidRampartRuleException {
        if (!(visitorSymbolTable.containsKey(VALIDATE_KEY.toString()) || visitorSymbolTable.containsKey(INJECTION_KEY.toString()))
                || !visitorSymbolTable.containsKey(ACTION_KEY.toString())) {
            throw new InvalidRampartRuleException(
                    "RAMPART http rules must contain a \"" + VALIDATE_KEY + "\" or \"" + INJECTION_KEY
                            + "\" declarations and an \"" + ACTION_KEY + "\" declaration");
        }
    }

}
