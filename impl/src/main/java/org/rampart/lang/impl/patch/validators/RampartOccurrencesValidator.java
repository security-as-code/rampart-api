package org.rampart.lang.impl.patch.validators;

import static org.rampart.lang.api.constants.RampartPatchConstants.*;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartInteger;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.patch.RampartPatchType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;
import org.rampart.lang.java.RampartPrimitives;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class to validate Rampart location specifier occurrences
 * Eg.
 *  call("com/foo/bar.method()V", occurrences: [1,2])
 */
public class RampartOccurrencesValidator {
    private static final String OCCURRENCES_ERROR_PREFIX = "invalid occurrences field: ";

    private final Map<String, RampartList> visitorSymbolTable;

    public RampartOccurrencesValidator(Map<String, RampartList> visitorSymbolTable) {
        this.visitorSymbolTable = visitorSymbolTable;
    }

    public RampartList validateOccurrencesValues(RampartPatchType rulePatchType) throws InvalidRampartRuleException {
        RampartList locationValues = visitorSymbolTable.get(rulePatchType.getName().toString());
        RampartObject occurrenceValues = RampartInterpreterUtils
                        .findRampartNamedValue(OCCURRENCES_KEY, locationValues);

        // Separate null / instance checks are required here as occurrences are optional
        if (occurrenceValues == null) {
            return null;
        }
        if (!(occurrenceValues instanceof RampartList)) {
            throw new InvalidRampartRuleException(OCCURRENCES_ERROR_PREFIX +
                    "must be an RampartList and not " + occurrenceValues.getClass());
        }

        RampartList occurrencesList = (RampartList) occurrenceValues;
        if (occurrencesList.isEmpty() == RampartBoolean.TRUE) {
            throw new InvalidRampartRuleException(OCCURRENCES_ERROR_PREFIX +
                    "must be a non empty RampartList");
        }
        
        Set<Integer> uniqueOccValues = new HashSet<Integer>();
        RampartObjectIterator it = occurrencesList.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            validateOccurrenceValue(it.next(), uniqueOccValues);
        }
        return occurrencesList;
    }

    private void validateOccurrenceValue(RampartObject occurrence, Set<Integer> uniqueOccValues) throws InvalidRampartRuleException {
        if (!(occurrence instanceof RampartInteger)) {
            throw new InvalidRampartRuleException(OCCURRENCES_ERROR_PREFIX +
                    "occurrence [" + occurrence + "] is not an integer");
        }
        int occurrenceInteger = RampartPrimitives.toJavaInt((RampartInteger) occurrence);
        if (occurrenceInteger <= 0) {
            throw new InvalidRampartRuleException(OCCURRENCES_ERROR_PREFIX + "invalid occurrence index: "
                    + occurrenceInteger + ". Valid occurrence indexes begin at 1");
        }
        if (!uniqueOccValues.add(occurrenceInteger)) {
            throw new InvalidRampartRuleException(
                    OCCURRENCES_ERROR_PREFIX + "duplicate occurrence [" + occurrenceInteger + "]");
        }
    }
}
