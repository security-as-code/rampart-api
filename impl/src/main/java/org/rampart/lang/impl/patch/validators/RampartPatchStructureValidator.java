package org.rampart.lang.impl.patch.validators;

import static org.rampart.lang.api.constants.RampartPatchConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.patch.RampartPatchType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.v1.RampartRuleStructureValidatorUpTo1_5;
import org.rampart.lang.java.parser.RampartSingleAppVisitor;

import java.util.Map;

/**
 * Class to verify the structure of the symbol table values
 * populated with patch rule contents returned by the visitor
 * @see RampartSingleAppVisitor
 */
public class RampartPatchStructureValidator extends RampartRuleStructureValidatorUpTo1_5 {
    static final RampartList PATCH_LOCATION_FIELDS = newRampartList(
            RampartPatchType.CALL.getName(),
            RampartPatchType.CALLRETURN.getName(),
            RampartPatchType.CALLSITE.getName(),
            RampartPatchType.ENTRY.getName(),
            RampartPatchType.ERROR.getName(),
            RampartPatchType.EXIT.getName(),
            RampartPatchType.INSTRUCTION.getName(),
            RampartPatchType.LINE.getName(),
            RampartPatchType.READ.getName(),
            RampartPatchType.READRETURN.getName(),
            RampartPatchType.READSITE.getName(),
            RampartPatchType.WRITE.getName(),
            RampartPatchType.WRITESITE.getName(),
            RampartPatchType.WRITERETURN.getName());
    public static final RampartList MANDATORY_PATCH_FIELDS =
            newRampartList(PATCH_KEY, FUNCTION_KEY, CODE_KEY, SOURCE_CODE_KEY);

    public RampartPatchStructureValidator(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
    }

    /**
     * Validates the key set of the map returned by the visitor for this patch rule.
     * The keyset is valid in accordance with the following criteria:
     *  1. It contains all mandatory Patch rule keys.
     *  2. It does not contain any unknown keys.
     *  3. It contains exactly one location specifier key.
     * @return RampartPatchType representing the location specifier the patch specified.
     * @throws InvalidRampartRuleException if the above criteria are not met.
     */
    public RampartPatchType validatePatchStructure() throws InvalidRampartRuleException {
        if (!keySetContainsAllKeys(MANDATORY_PATCH_FIELDS)) {
            throw new InvalidRampartRuleException(
                    "missing one of the patch elements: "
                            + MANDATORY_PATCH_FIELDS);
        }

        if (!keySetContainsExactlyOneOf(PATCH_LOCATION_FIELDS)) {
            throw new InvalidRampartRuleException("invalid location specifier, patch must contain only one of "
                            + PATCH_LOCATION_FIELDS);
        }

        RampartList possiblePatchKeys = MANDATORY_PATCH_FIELDS
                .addAll(PATCH_LOCATION_FIELDS);

        if (keySetContainsInvalidKey(possiblePatchKeys)) {
            throw new InvalidRampartRuleException("unsupported element found. "
                    + "All patch elements must be one of " + possiblePatchKeys);
        }
        // At this point we can be guaranteed there is exactly one location specifier key
        return RampartPatchType.fromRampartString(deduceLocationKey());
    }

    private RampartConstant deduceLocationKey() {
        RampartConstant locationKey = null;
        RampartObjectIterator it = PATCH_LOCATION_FIELDS.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject key = it.next();
            if (visitorSymbolTable.containsKey(key.toString())) {
                locationKey = (RampartConstant) key;
                break; // no need to iterate further.
            }
        }
        return locationKey;
    }
}
