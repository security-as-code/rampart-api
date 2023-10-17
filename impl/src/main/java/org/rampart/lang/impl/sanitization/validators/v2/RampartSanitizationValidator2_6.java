package org.rampart.lang.impl.sanitization.validators.v2;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.ValidationError;
import org.rampart.lang.impl.core.validators.v2.RampartMetadataValidator;
import org.rampart.lang.java.builder.RampartSanitizationBuilder;

import java.util.Map;

import static org.rampart.lang.api.constants.RampartGeneralConstants.METADATA_KEY;

public class RampartSanitizationValidator2_6 extends RampartSanitizationValidator2_3 {
    private final RampartMetadataValidator metadataValidator;

    public RampartSanitizationValidator2_6(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        metadataValidator = new RampartMetadataValidator(visitorSymbolTable.get(METADATA_KEY.toString()));
    }

    //@Override
    public RampartSanitizationBuilder validate() throws InvalidRampartRuleException {
        structureValidator.feedValidators(metadataValidator);
        super.validate();
        try {
            builder.addMetadata(metadataValidator.validateMetadata());
        } catch (ValidationError ve) {
            throw new InvalidRampartRuleException(ve.getMessage(), ve);
        }
        return builder;
    }
}
