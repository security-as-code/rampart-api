package org.rampart.lang.impl.core.parsers.v2;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.constants.RampartGeneralConstants;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.core.RampartInput;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.java.RampartPrimitives;

import static org.rampart.lang.java.RampartPrimitives.newRampartList;

import java.util.Map;

/**
 * Parsers for RAMPART input sources.
 */
public final class RampartInputParser {
    /** Default field used for parsing. */
    public static final RampartConstant INPUT_KEY = RampartGeneralConstants.INPUT_KEY;

    /**
     * Default fields used by the parser - this is just a convenience
     * method for using with structure validation (like supported or required fields).
     */
    public static final RampartConstant[] DEFAULT_KEYS = { INPUT_KEY };

    private RampartInputParser() {
        throw new UnsupportedOperationException();
    }


    /**
     * Parses (tainting) inputs data.
     * @param data data to parse.
     * @param inputName name of the input field (used for message generation).
     * @return list containing RampartInputs.
     */
    public static RampartList parseDataInputs(RampartList data, RampartConstant inputName)
            throws InvalidRampartRuleException {
        if (data == null) {
            return RampartList.EMPTY;
        }

        final int inputSize = RampartPrimitives.toJavaInt(data.size());
        if (inputSize <= 0) {
            throw new InvalidRampartRuleException(
                    "\"" + inputName + "\" declaration must be followed by a non empty list");
        }
        RampartInput[] inputArray = new RampartInput[inputSize];
        RampartObjectIterator it = data.getObjectIterator();
        for (int i = 0; i < inputArray.length; i++) {
            final RampartObject param = it.next();
            if (!(param instanceof RampartConstant)) {
                throw new InvalidRampartRuleException("\"" + inputName + "\" declaration list entries must be constants");
            }
            RampartConstant inputsParam = (RampartConstant) param;
            final RampartInput rampartInput = RampartInput.valueOf(inputsParam);
            if (rampartInput == null) {
                throw new InvalidRampartRuleException("unrecognized parameter \"" + inputsParam + "\" for RAMPART rule");
            }
            inputArray[i] = rampartInput;
        }

        return newRampartList(inputArray);
    }


    /**
     * Parses (tainting) inputs data from the general lookup table.
     * @param lookupTable lookup table to be used.
     * @param inputName name of the input field used to access the data.
     */
    public static RampartList parseDataInputs(Map<String, RampartList> lookupTable, RampartConstant inputName)
            throws InvalidRampartRuleException {
        final RampartList input = lookupTable.get(inputName.toString());
        return parseDataInputs(input, inputName);
    }


    /**
     * Parses (tainting) inputs data from the general lookup table using default parameter name.
     * @param lookupTable lookup table to be used.
     */
    public static RampartList parseDataInputs(Map<String, RampartList> lookupTable)
            throws InvalidRampartRuleException {
        return parseDataInputs(lookupTable, INPUT_KEY);
    }
}
