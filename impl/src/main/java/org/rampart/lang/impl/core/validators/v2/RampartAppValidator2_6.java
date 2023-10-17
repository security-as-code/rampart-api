package org.rampart.lang.impl.core.validators.v2;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartVersion;
import org.rampart.lang.impl.core.ValidationError;
import org.rampart.lang.java.InvalidRampartAppException;

import java.util.Collections;
import java.util.Map;

import static org.rampart.lang.api.constants.RampartGeneralConstants.METADATA_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.VERSION_KEY;

public class RampartAppValidator2_6 extends RampartAppValidator2_3 {
    private final RampartMetadataValidator metadataValidator;

    public RampartAppValidator2_6(RampartList appValues, RampartVersion requiredVersion, Map<String, RampartList> appDeclarations) {
        super(appValues, requiredVersion, appDeclarations);
        this.metadataValidator = new RampartMetadataValidator(appDeclarations.get(METADATA_KEY.toString()));
    }

    @Override
    protected void validateAppDeclarations(Map<String, RampartList> appDeclarations) {
        for (String declaration : appDeclarations.keySet()) {
            if (VERSION_KEY.toString().equals(declaration)) {
                super.validateAppDeclarations(
                        Collections.singletonMap(declaration, appDeclarations.get(VERSION_KEY.toString())));
            } else if (!METADATA_KEY.toString().equals(declaration)) {
                throw new InvalidRampartAppException(
                        "unrecognized declaration \"" + declaration + "\" within the RAMPART app");
            }
        }
    }

    @Override
    protected void addToBuilder(String key, RampartList parameters) {
        super.addToBuilder(key, parameters);
        if (METADATA_KEY.toString().equals(key)) {
            try {
                builder.addMetadata(metadataValidator.validateMetadata());
            } catch(ValidationError ve) {
                throw new InvalidRampartAppException(ve.getMessage(), ve);
            }
        }
    }

}
