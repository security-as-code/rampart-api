package org.rampart.lang.impl.core.validators;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.impl.core.ValidationError;

/**
 * This class is used to validate metadata entries that are standardized by Rampart.
 */
public abstract class RampartMetadataEntryValidator {
    /**
     * Validates the value of the metadata entry. This method is configured to suit the specific standardized metadata
     * entry.
     * @param entryKey key of the entry (i.e. key that was associated with the value). Useful
     *   for generating error messages.
     * @param value RampartObject to validate
     * @return an RampartObject for this entry's value. The returned value could be different from what was received as a
     *         parameter as the RampartObject type can change during validation for certain metadata.
     * @throws ValidationError
     */
    public abstract RampartObject validateValue(RampartConstant entryKey, RampartObject value) throws ValidationError;

}
