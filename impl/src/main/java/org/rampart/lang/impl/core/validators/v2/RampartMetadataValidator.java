package org.rampart.lang.impl.core.validators.v2;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.impl.core.ValidationError;
import org.rampart.lang.impl.core.parsers.v2.RampartMetadataParser;
import org.rampart.lang.impl.core.validators.RampartMetadataEntryValidator;
import org.rampart.lang.impl.core.validators.FirstClassRuleObjectValidator;

import java.util.*;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;

public class RampartMetadataValidator implements FirstClassRuleObjectValidator {

    private final RampartList metadataList;

    private final Map<RampartConstant, RampartMetadataEntryValidator> specificFields;

    public RampartMetadataValidator(RampartList metadataList, Map<RampartConstant, RampartMetadataEntryValidator> specificFields) {
        this.metadataList = metadataList;
        this.specificFields = specificFields;
    }

    public RampartMetadataValidator(RampartList metadataList) {
        this(metadataList, RampartMetadataParser.DEFAULT_FIELD_HANDLERS);
    }


    public RampartMetadata validateMetadata() throws ValidationError {
        return RampartMetadataParser.parseMetadata(metadataList, specificFields);
    }


    public List<RampartConstant> allowedKeys() {
        return Collections.singletonList(METADATA_KEY);
    }
}
