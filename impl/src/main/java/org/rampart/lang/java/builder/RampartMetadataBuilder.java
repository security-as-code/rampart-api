package org.rampart.lang.java.builder;

import org.rampart.lang.api.*;
import org.rampart.lang.api.core.RampartAffectedProductVersion;
import org.rampart.lang.api.core.RampartCvss;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.impl.core.RampartMetadataImpl;

import java.util.HashMap;
import java.util.HashSet;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartNamedValue;
import static org.rampart.lang.java.RampartPrimitives.toJavaBoolean;

public class RampartMetadataBuilder implements RampartObjectBuilder<RampartMetadata> {
    private final HashMap<RampartConstant, RampartNamedValue> metadataMap = new HashMap<RampartConstant, RampartNamedValue>();
    private final HashSet<RampartConstant> loggableMetadata = new HashSet<RampartConstant>();

    public RampartMetadata createRampartObject() {
        return new RampartMetadataImpl(metadataMap, loggableMetadata);
    }

    public RampartMetadataBuilder addMetadata(RampartNamedValue entry, RampartBoolean isLoggable) {
        metadataMap.put(entry.getName(), entry);
        if (toJavaBoolean(isLoggable)) {
            loggableMetadata.add(entry.getName());
        }
        return this;
    }

    /**
     * @param cweList RampartList of user specified RampartStrings
     * @param isLoggable flag to either set or not set the metadata entry as loggable
     */
    public RampartMetadataBuilder addCweMetadata(RampartList cweList, RampartBoolean isLoggable) {
        return addMetadata(newRampartNamedValue(CWE_KEY, cweList), isLoggable);
    }

    /**
     * @param cveList RampartList of user specified RampartStrings
     * @param isLoggable flag to either set or not set the metadata entry as loggable
     */
    public RampartMetadataBuilder addCveMetadata(RampartList cveList, RampartBoolean isLoggable) {
        return addMetadata(newRampartNamedValue(CVE_KEY, cveList), isLoggable);
    }

    public RampartMetadataBuilder addCvssMetadata(RampartCvss cvssEntry, RampartBoolean isLoggable) {
        return addMetadata(newRampartNamedValue(CVSS_KEY, cvssEntry), isLoggable);
    }

    public RampartMetadataBuilder addDescriptionMetadata(RampartString descriptionEntry, RampartBoolean isLoggable) {
        return addMetadata(newRampartNamedValue(DESCRIPTION_KEY, descriptionEntry), isLoggable);
    }

    public RampartMetadataBuilder addAffectedOperatingSystemMetadata(RampartList affectedOperatingSystemEntry, RampartBoolean isLoggable) {
        return addMetadata(newRampartNamedValue(AFFECTED_OS_KEY, affectedOperatingSystemEntry), isLoggable);
    }

    public RampartMetadataBuilder addAffectedProductNameMetadata(RampartString affectedProductNameEntry, RampartBoolean isLoggable) {
        return addMetadata(newRampartNamedValue(AFFECTED_PRODUCT_NAME_KEY, affectedProductNameEntry), isLoggable);
    }

    public RampartMetadataBuilder addAffectedProductVersionMetadata(RampartAffectedProductVersion affectedProductVersionEntry, RampartBoolean isLoggable) {
        return addMetadata(newRampartNamedValue(AFFECTED_PRODUCT_VERSION_KEY, affectedProductVersionEntry), isLoggable);
    }

    public RampartMetadataBuilder addCreationTimeMetadata(RampartString creationTimeEntry, RampartBoolean isLoggable) {
        return addMetadata(newRampartNamedValue(CREATION_TIME_KEY, creationTimeEntry), isLoggable);
    }

    public RampartMetadataBuilder addVersionMetadata(RampartInteger versionEntry, RampartBoolean isLoggable) {
        return addMetadata(newRampartNamedValue(VERSION_KEY, versionEntry), isLoggable);
    }
}
