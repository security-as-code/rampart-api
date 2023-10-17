package org.rampart.lang.impl.core.validators.v1;

import static org.rampart.lang.java.RampartPrimitives.newRampartConstant;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.java.RampartPrimitives;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class to encapsulate behaviour common to all Rampart rule structure validators
 */
@Deprecated
public abstract class RampartRuleStructureValidatorUpTo1_5 {
    protected final Map<String, RampartList> visitorSymbolTable;

    protected RampartRuleStructureValidatorUpTo1_5(Map<String, RampartList> visitorSymbolTable) {
        this.visitorSymbolTable = visitorSymbolTable;
    }

    /**
     * Checks if the `visitorSymbolTable` key set contains all of the `keys` within the list provided.
     * @param keys list of keys to check against
     * @return if all keys are present
     */
    protected boolean keySetContainsAllKeys(RampartList keys) {
        return visitorSymbolTable.keySet().containsAll(createJavaStringListFromRampartList(keys));
    }

    /**
     * Checks if the `visitorSymbolTable` keyset contains an invalid key.
     * An invalid key is defined as not being present in the given RampartList
     * @param keys list of keys to check against
     * @return if `visitorSymbolTable` contains a key not included in the key list passed as an argument
     */
    protected boolean keySetContainsInvalidKey(RampartList keys) {
        for (String key : visitorSymbolTable.keySet()) {
            if (keys.contains(newRampartConstant(key)) == RampartBoolean.FALSE) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the key set has exactly one of the `keys` contained in the list
     * provided.
     * @param keyList list of keys to check against
     * @return if there is only one key from the `keys` list
     */
    protected boolean keySetContainsExactlyOneOf(RampartList keyList) {
        int count = 0;
        RampartObjectIterator it = keyList.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            if (visitorSymbolTable.containsKey(it.next().toString())) {
                count++;
            }
        }
        return count == 1;
    }

    private static List<String> createJavaStringListFromRampartList(RampartList keys) {
        List<String> keyNames = new ArrayList<String>(RampartPrimitives.toJavaInt(keys.size()));
        RampartObjectIterator it = keys.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            keyNames.add(it.next().toString());
        }
        return keyNames;
    }
}
