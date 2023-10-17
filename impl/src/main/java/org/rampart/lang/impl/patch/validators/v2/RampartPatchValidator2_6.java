package org.rampart.lang.impl.patch.validators.v2;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.ValidationError;
import org.rampart.lang.impl.core.validators.v2.RampartMetadataValidator;
import org.rampart.lang.java.builder.RampartPatchBuilder;

import java.util.Map;

import static org.rampart.lang.api.constants.RampartGeneralConstants.METADATA_KEY;

public class RampartPatchValidator2_6 extends RampartPatchValidator2_1 {
    private final RampartMetadataValidator metadataValidator;

    public RampartPatchValidator2_6(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        metadataValidator = new RampartMetadataValidator(visitorSymbolTable.get(METADATA_KEY.toString()));
        this.patchStructureValidator = new RampartPatchStructureValidator2_6(visitorSymbolTable);
    }

    //@Override
    public RampartPatchBuilder validate() throws InvalidRampartRuleException {
        super.validate();
        try {
            builder.addMetadata(metadataValidator.validateMetadata());
        } catch (ValidationError ve) {
            throw new InvalidRampartRuleException(ve.getMessage(), ve);
        }
        return builder;
    }
}
