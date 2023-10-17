package org.rampart.lang.impl.patch.validators.v1;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.java.builder.RampartPatchBuilder;
import org.rampart.lang.impl.patch.validators.RampartOccurrencesValidator;
import java.util.Map;

/**
 * Class to validate an RAMPART patch containing features introduced at RAMPART/1.2
 */

public class RampartPatchValidator1_2 extends RampartPatchValidator1_1 {
    private final RampartOccurrencesValidator occurrencesValidator;

    public RampartPatchValidator1_2(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        occurrencesValidator = new RampartOccurrencesValidator(visitorSymbolTable);
    }

    @Override
    public RampartPatchBuilder validate() throws InvalidRampartRuleException {
        super.validate();
        return builder.addOccurrences(occurrencesValidator.validateOccurrencesValues(rulepatchType));
    }
}
