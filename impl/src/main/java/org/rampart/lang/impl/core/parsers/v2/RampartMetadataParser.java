package org.rampart.lang.impl.core.parsers.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.AFFECTED_OS_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.AFFECTED_PRODUCT_NAME_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.AFFECTED_PRODUCT_VERSION_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.CREATION_TIME_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.CVE_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.CVSS_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.CWE_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.DESCRIPTION_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.LOG_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.VERSION_KEY;
import static org.rampart.lang.java.RampartPrimitives.newRampartNamedValue;
import static org.rampart.lang.java.RampartPrimitives.toJavaBoolean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartNamedValue;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.constants.RampartGeneralConstants;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.impl.core.RampartMetadataImpl;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.ValidationError;
import org.rampart.lang.impl.core.validators.RampartMetadataEntryValidator;
import org.rampart.lang.impl.core.validators.v2.RampartAffectedProductVersionEntryValidator;
import org.rampart.lang.impl.core.validators.v2.RampartCvssEntryValidator;
import org.rampart.lang.impl.core.validators.v2.RampartIntegerEntryValidator;
import org.rampart.lang.impl.core.validators.v2.RampartListOrRampartStringEntryValidator;
import org.rampart.lang.impl.core.validators.v2.RampartStringEntryValidator;

public class RampartMetadataParser {
    /** Default metadata validation map. */
    public static final Map<RampartConstant, RampartMetadataEntryValidator> DEFAULT_FIELD_HANDLERS;
    static {
        final HashMap<RampartConstant, RampartMetadataEntryValidator> fieldHandlers =
                new HashMap<RampartConstant, RampartMetadataEntryValidator>();
        fieldHandlers.put(CWE_KEY, new RampartListOrRampartStringEntryValidator());
        fieldHandlers.put(CVE_KEY, new RampartListOrRampartStringEntryValidator());
        fieldHandlers.put(CVSS_KEY, new RampartCvssEntryValidator());
        fieldHandlers.put(DESCRIPTION_KEY, new RampartStringEntryValidator());
        fieldHandlers.put(AFFECTED_OS_KEY, new RampartListOrRampartStringEntryValidator());
        fieldHandlers.put(AFFECTED_PRODUCT_NAME_KEY, new RampartStringEntryValidator());
        fieldHandlers.put(AFFECTED_PRODUCT_VERSION_KEY, new RampartAffectedProductVersionEntryValidator());
        fieldHandlers.put(CREATION_TIME_KEY, new RampartStringEntryValidator());
        fieldHandlers.put(VERSION_KEY, new RampartIntegerEntryValidator());

        DEFAULT_FIELD_HANDLERS = fieldHandlers;
    }


    /** Default key to use in lookup. */
    public static final RampartConstant DEFAULT_METADATA_KEY = RampartGeneralConstants.METADATA_KEY;

    /**
     * An array consisting of the default metadata key. Provides convenience for use in
     * the required/supported argument validation APIs.
     */
    public static final RampartConstant[] DEFAULT_METADATA_KEYS = { DEFAULT_METADATA_KEY };

    /**
     * Retrieves RAMPART metadata from the provided descriptor and the map of prebuilt builders.
     * @param metadata metadata list to parse.
     * @param specificHandlers specific handlers to use during the parsing.
     */
    public static RampartMetadata parseMetadata(
            RampartList metadata,
            Map<RampartConstant, RampartMetadataEntryValidator> specificHandlers) throws ValidationError {
        if (metadata == null) {
            return RampartMetadataImpl.EMPTY;
        }

        final Map<RampartConstant, RampartNamedValue> metadataMap = new HashMap<RampartConstant, RampartNamedValue>();
        final Set<RampartConstant> logKeys = new HashSet<RampartConstant>();

        final RampartObjectIterator it = metadata.getObjectIterator();
        while (toJavaBoolean(it.hasNext())) {
            final RampartNamedValue entry = getNextMetadataEntry(it);
            if (!isLoggableEntry(entry)) {
                addMetadataRecord(metadataMap, getMetadataValue(entry, specificHandlers));
            } else {
                final RampartObjectIterator logIt = ((RampartList) entry.getRampartObject()).getObjectIterator();
                while (toJavaBoolean(logIt.hasNext())) {
                    final RampartNamedValue logEntry = getNextMetadataEntry(logIt);
                    addMetadataRecord(metadataMap, getMetadataValue(logEntry, specificHandlers));
                    logKeys.add(logEntry.getName());
                }
            }
        }

        if (metadataMap.isEmpty()) {
            throw new ValidationError("metadata declaration cannot be empty");
        }

        return new RampartMetadataImpl(metadataMap, logKeys);
    }


    /**
     * Retrieves RAMPART metadata from the provided descriptor and the map of prebuilt builders.
     * @param entityTable entity table to use.
     * @param metadataKey metadata key to use.
     * @param specificHandlers specific handlers to use during the parsing.
     */
    public static RampartMetadata parseRuleMetadata(
            Map<String, RampartList> entityTable,
            RampartConstant metadataKey,
            Map<RampartConstant, RampartMetadataEntryValidator> specificHandlers) throws InvalidRampartRuleException {
        try {
            return parseMetadata(entityTable.get(metadataKey.toString()), specificHandlers);
        } catch (ValidationError e) {
            throw new InvalidRampartRuleException(e.getMessage(), e);
        }
    }


    /**
     * Retrieves RAMPART metadata from the provided descriptor with the default field handlers.
     * @param entityTable entity table to use.
     * @param metadataKey metadata key to use.
     */
    public static RampartMetadata parseRuleMetadata(
            Map<String, RampartList> entityTable,
            RampartConstant metadataKey) throws InvalidRampartRuleException {
        return parseRuleMetadata(entityTable, metadataKey, DEFAULT_FIELD_HANDLERS);
    }


    /**
     * Retrieves RAMPART metadata from the provided descriptor and the map of prebuilt builders. Uses
     * the standard metadata key.
     * @param entityTable entity table to use.
     * @param specificHandlers specific handlers to use during the parsing.
     */
    public static RampartMetadata parseRuleMetadata(
            Map<String, RampartList> entityTable,
            Map<RampartConstant, RampartMetadataEntryValidator> specificHandlers) throws InvalidRampartRuleException {
        return parseRuleMetadata(entityTable, DEFAULT_METADATA_KEY, specificHandlers);
    }


    /**
     * Retrieves RAMPART metadata from the provided descriptor using standard metadata name
     * and standard metadata descriptor.
     * @param entityTable entity table to use.
     */
    public static RampartMetadata parseRuleMetadata(
            Map<String, RampartList> entityTable) throws InvalidRampartRuleException {
        return parseRuleMetadata(entityTable, DEFAULT_METADATA_KEY, DEFAULT_FIELD_HANDLERS);
    }


    /**
     * Registers metadata record into the metadata map.
     * @param metadataMap metadata map to populate the value.
     * @param entry entry to add.
     * @throws ValidationError if there is already an entry with the same name.
     */
    private static void addMetadataRecord(
            Map<RampartConstant, RampartNamedValue> metadataMap,
            RampartNamedValue entry) throws ValidationError {
        if (metadataMap.containsKey(entry.getName())) {
            throw new ValidationError("duplicate metadata entry \"" + entry.getName() + "\" detected");
        }
        metadataMap.put(entry.getName(), entry);
    }


    /** Retrieves next metadata entry. */
    private static RampartNamedValue getNextMetadataEntry(RampartObjectIterator iterator) throws ValidationError {
        final RampartObject entry = iterator.next();
        if (!(entry instanceof RampartNamedValue)) {
            throw new ValidationError("metadata entries can only be key value pairs");
        }
        return (RampartNamedValue) entry;
    }


    /**
     * Retrieves metadata value for the given entry. It may run some specific conversions and/or validations that
     * are known for the field name. The original value is used if there is no registered handler for the entry type.
     * @param entry entry to parse.
     * @param specificHandlers handlers for specific names of fields.
     */
    private static RampartNamedValue getMetadataValue(
            RampartNamedValue entry,
            Map<RampartConstant, RampartMetadataEntryValidator> specificHandlers) throws ValidationError {
        final RampartMetadataEntryValidator knownValidator = specificHandlers.get(entry.getName());
        if (knownValidator == null) {
            return entry;
        }
        final RampartObject newValue = knownValidator.validateValue(entry.getName(), entry.getRampartObject());
        return newRampartNamedValue(entry.getName(), newValue);
    }


    private static boolean isLoggableEntry(RampartNamedValue metadataEntry) {
        return metadataEntry.getName().equals(LOG_KEY)
                && metadataEntry.getRampartObject() instanceof RampartList;
    }
}
