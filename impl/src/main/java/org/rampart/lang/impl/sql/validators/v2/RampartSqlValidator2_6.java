package org.rampart.lang.impl.sql.validators.v2;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.ValidationError;
import org.rampart.lang.impl.core.validators.v2.RampartMetadataValidator;
import org.rampart.lang.java.builder.RampartSqlBuilder;

import java.util.Map;

import static org.rampart.lang.api.constants.RampartGeneralConstants.METADATA_KEY;

public class RampartSqlValidator2_6 extends RampartSqlValidator2_4 {
    private final RampartMetadataValidator metadataValidator;

    public RampartSqlValidator2_6(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        metadataValidator = new RampartMetadataValidator(visitorSymbolTable.get(METADATA_KEY.toString()));
    }

    //@Override
    public RampartSqlBuilder validate() throws InvalidRampartRuleException {
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
