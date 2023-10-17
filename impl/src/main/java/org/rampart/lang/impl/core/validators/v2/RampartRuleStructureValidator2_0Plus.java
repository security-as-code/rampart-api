package org.rampart.lang.impl.core.validators.v2;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.FirstClassRuleObjectValidator;

public class RampartRuleStructureValidator2_0Plus {
    private final Map<String, RampartList> visitorSymbolTable;

    /** Field names that may appear in the structure. */
    private final Set<String> allowedKeys = new HashSet<String>();

    public RampartRuleStructureValidator2_0Plus(Map<String, RampartList> visitorSymbolTable, RampartRuleType type) {
        this.visitorSymbolTable = visitorSymbolTable;
        allowedKeys.add(type.getName().toString());
    }

    public void feedValidators(FirstClassRuleObjectValidator... validators) {
        for (FirstClassRuleObjectValidator validator: validators) {
            for (RampartConstant allowedKey: validator.allowedKeys()) {
                allowedKeys.add(allowedKey.toString());
            }
        }
    }


    public void feedSupportedFields(RampartConstant... rampartConstants) {
        for (RampartConstant allowedKey: rampartConstants) {
            allowedKeys.add(allowedKey.toString());
        }
    }


    public void feedSupportedFields(RampartConstant[]... rampartConstants) {
        for (RampartConstant[] supportedKeys: rampartConstants) {
            feedSupportedFields(supportedKeys);
        }
    }


    public void validateDeclarations(RampartString ruleName)
            throws InvalidRampartRuleException {
        for (String key : visitorSymbolTable.keySet()) {
            if (!allowedKeys.contains(key)) {
                throw new InvalidRampartRuleException(
                        "\"" + key + "\" is not a recognized declaration in rule \"" + ruleName + "\"");
            }
        }
    }
}
