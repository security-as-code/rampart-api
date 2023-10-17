package org.rampart.lang.impl.core.validators.v2;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.impl.core.ValidationError;
import org.rampart.lang.impl.core.validators.RampartMetadataEntryValidator;

import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.rampart.lang.java.RampartPrimitives.toJavaBoolean;

public class RampartListOrRampartStringEntryValidator extends RampartMetadataEntryValidator {

    public RampartList validateValue(RampartConstant entryKey, RampartObject value) throws ValidationError {
        if (value instanceof RampartString
                && !toJavaBoolean(((RampartString) value).isEmpty())) {
            return newRampartList(value);
        }
        if (value instanceof RampartList
                && !toJavaBoolean(((RampartList) value).isEmpty())) {
            RampartObjectIterator it = ((RampartList) value).getObjectIterator();
            while (toJavaBoolean(it.hasNext())) {
                RampartObject obj = it.next();
                if (!(obj instanceof RampartString)
                        || toJavaBoolean(((RampartString) obj).isEmpty())) {
                    throwValidationError(entryKey);
                }
            }
        } else {
            throwValidationError(entryKey);
        }
        return (RampartList) value;
    }

    private void throwValidationError(RampartConstant entryKey) throws ValidationError {
        throw new ValidationError("metadata value of \"" + entryKey
                + "\" key must be either a non empty string literal or a non empty list of non empty strings");
    }

}
