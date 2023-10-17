package org.rampart.lang.impl.core.validators.v2;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.impl.core.ValidationError;
import org.rampart.lang.impl.core.validators.RampartMetadataEntryValidator;

import static org.rampart.lang.java.RampartPrimitives.toJavaBoolean;

public class RampartStringEntryValidator extends RampartMetadataEntryValidator {
    public RampartString validateValue(RampartConstant entryKey, RampartObject value) throws ValidationError {
        if (!(value instanceof RampartString)) {
            throw new ValidationError("metadata value of \"" + entryKey + "\" key must be a string literal");
        }
        if (toJavaBoolean(((RampartString) value).isEmpty())) {
            throw new ValidationError("metadata value of \"" + entryKey + "\" key cannot be empty");
        }
        return (RampartString) value;
    }
}
