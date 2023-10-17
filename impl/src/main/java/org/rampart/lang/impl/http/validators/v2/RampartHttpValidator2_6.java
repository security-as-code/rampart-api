package org.rampart.lang.impl.http.validators.v2;

import java.util.Map;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.ValidationError;
import org.rampart.lang.impl.core.validators.v2.RampartMetadataValidator;
import org.rampart.lang.impl.http.RampartCsrfOptions2_6;
import org.rampart.lang.java.builder.RampartHttpBuilder;

import static org.rampart.lang.api.constants.RampartGeneralConstants.METADATA_KEY;

public class RampartHttpValidator2_6 extends RampartHttpValidator2_5 {
    private final RampartMetadataValidator metadataValidator;

    public RampartHttpValidator2_6(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        metadataValidator = new RampartMetadataValidator(visitorSymbolTable.get(METADATA_KEY.toString()));
        csrfValidator = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_6());
    }

    //@Override
    public RampartHttpBuilder validate() throws InvalidRampartRuleException {
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
