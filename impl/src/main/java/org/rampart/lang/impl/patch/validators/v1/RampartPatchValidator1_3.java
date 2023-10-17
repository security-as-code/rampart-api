package org.rampart.lang.impl.patch.validators.v1;

import static org.rampart.lang.api.constants.RampartPatchConstants.*;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;
import org.rampart.lang.java.builder.RampartPatchBuilder;
import org.rampart.lang.impl.patch.validators.RampartChecksumValidator;
import java.util.Map;

/**
 * Class to validate an RAMPART patch containing features introduced at RAMPART/1.3
 */
public class RampartPatchValidator1_3 extends RampartPatchValidator1_2 {
    private final RampartChecksumValidator checksumValidator;

    public RampartPatchValidator1_3(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        checksumValidator = new RampartChecksumValidator(
                RampartInterpreterUtils.findRampartNamedValue(CHECKSUMS_KEY, visitorSymbolTable.get(FUNCTION_KEY.toString())));
    }

    @Override
    public RampartPatchBuilder validate() throws InvalidRampartRuleException {
        super.validate();
        return builder.addChecksums(checksumValidator.validateChecksumValues());
    }
}
