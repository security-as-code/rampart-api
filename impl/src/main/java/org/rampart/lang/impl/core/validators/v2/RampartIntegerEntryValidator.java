package org.rampart.lang.impl.core.validators.v2;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartInteger;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.impl.core.ValidationError;
import org.rampart.lang.impl.core.validators.RampartMetadataEntryValidator;

public class RampartIntegerEntryValidator extends RampartMetadataEntryValidator {
    public RampartInteger validateValue(RampartConstant entryKey, RampartObject value) throws ValidationError {
        if (!(value instanceof RampartInteger)) {
            throw new ValidationError(
                    "metadata value of \"" + entryKey + "\" key must be an integer");
        }
        return (RampartInteger) value;
    }
}
