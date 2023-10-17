package org.rampart.lang.impl.marshal.validators.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.METADATA_KEY;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.ValidationError;
import org.rampart.lang.impl.core.validators.v2.RampartMetadataValidator;
import org.rampart.lang.java.builder.RampartMarshalBuilder;
import java.util.Map;

public class RampartMarshalValidator2_6 extends RampartMarshalValidator2_3 {
    protected final RampartMetadataValidator metadataValidator;
    protected final ExternalXmlEntityConfigValidator2_6 xxeConfigValidator;

    public RampartMarshalValidator2_6(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        structureValidator = new RampartMarshalStructureValidator2_6(visitorSymbolTable, builder);
        actionValidator = new RampartMarshalActionValidator2_6(visitorSymbolTable);
        deserialTypeValidator = new DeserializeTypeValidator2_6(visitorSymbolTable);
        strategyValidator = new RampartDeserialStrategyValidator2_6(visitorSymbolTable);
        metadataValidator = new RampartMetadataValidator(visitorSymbolTable.get(METADATA_KEY.toString()));
        xxeConfigValidator = new ExternalXmlEntityConfigValidator2_6(visitorSymbolTable);
    }

    //@Override
    public RampartMarshalBuilder validate() throws InvalidRampartRuleException {
        structureValidator.feedValidators(metadataValidator, xxeConfigValidator);
        super.validate();
        try {
            builder.addMetadata(metadataValidator.validateMetadata());
        } catch (ValidationError ve) {
            throw new InvalidRampartRuleException(ve.getMessage(), ve);
        }
        builder.addExternalXmlEntityConfig(xxeConfigValidator.validateExternalXmlEntityConfig());
        structureValidator.crossValidate();
        return builder;
    }

}
