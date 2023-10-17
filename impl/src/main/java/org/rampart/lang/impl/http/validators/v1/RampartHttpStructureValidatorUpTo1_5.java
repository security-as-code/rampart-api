package org.rampart.lang.impl.http.validators.v1;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.http.RampartHttpIOType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.v1.RampartRuleStructureValidatorUpTo1_5;
import org.rampart.lang.java.parser.RampartSingleAppVisitor;

import java.util.Map;

/**
 * Class to verify the structure of the symbol table values
 * populated with http rule contents returned by the visitor
 * @see RampartSingleAppVisitor
 *
 * A well formed http rule will consist of the following:
 * <code>
 *  http("descriptive rule name"):
 *      request(uri:"relative-uri"])
 *      validate({cookie OR header OR parameter}: ["paramName1"], enforce: "enforceType")
 *      action({protect OR detect OR correct}: "log message", severity: {0-9 OR "LOW" OR "MED" OR "HIGH" OR "VERY_HIGH"})
 *  endhttp
 *  </code>
 */
@Deprecated
public class RampartHttpStructureValidatorUpTo1_5 extends RampartRuleStructureValidatorUpTo1_5 {
    static final RampartList VALID_HTTP_SYMBOL_TABLE_KEYS =
            newRampartList(ACTION_KEY, HTTP_KEY, REQUEST_KEY, VALIDATE_KEY);

    public RampartHttpStructureValidatorUpTo1_5(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
    }

    /**
     * Validates the symbol table key set returned from the visitor for this rule
     * @return The RampartHttpIOType requested by the rule
     * @throws InvalidRampartRuleException if an invalid key is present,
     * a mandatory key is missing or an invalid HttpIOType is specified.
     */
    public RampartHttpIOType validateHttpStructure() throws InvalidRampartRuleException {
        if (keySetContainsInvalidKey(VALID_HTTP_SYMBOL_TABLE_KEYS)) {
            throw new InvalidRampartRuleException("unsupported element found. "
                    + "All http elements must be one of: "
                    + VALID_HTTP_SYMBOL_TABLE_KEYS);
        }
        ensureMandatoryKeysPresent();
        return validateHttpIOType();
    }

    /**
     * Validates the key set contains a `request` key
     *
     * @throws InvalidRampartRuleException if `request` key is not present
     */
    private RampartHttpIOType validateHttpIOType() throws InvalidRampartRuleException {
        if (!visitorSymbolTable.containsKey(REQUEST_KEY.toString())) {
            throw new InvalidRampartRuleException("RAMPART http rules must contain a \"request\" value");
        }
        return RampartHttpIOType.REQUEST;
    }

    private void ensureMandatoryKeysPresent() throws InvalidRampartRuleException {
        if (!(visitorSymbolTable.containsKey(VALIDATE_KEY.toString())
                && visitorSymbolTable.containsKey(ACTION_KEY.toString()))) {
            throw new InvalidRampartRuleException("RAMPART http rules must contain both \"action\" and \"validate\"");
        }
    }
}
