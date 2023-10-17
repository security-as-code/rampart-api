package org.rampart.lang.impl.core.validators.v2;

import org.rampart.lang.api.*;
import org.rampart.lang.api.core.RampartCvss;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.impl.core.RampartCvssImpl;
import org.rampart.lang.impl.core.ValidationError;
import org.rampart.lang.impl.core.validators.RampartMetadataEntryValidator;

import java.util.HashSet;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.java.RampartPrimitives.toJavaBoolean;

public class RampartCvssEntryValidator extends RampartMetadataEntryValidator {
    public RampartCvss validateValue(RampartConstant entryKey, RampartObject value) throws ValidationError {
        if (!(value instanceof RampartList)) {
            throw new ValidationError("metadata value of \"" + entryKey + "\" key must be a list");
        }
        RampartObjectIterator it = ((RampartList) value).getObjectIterator();
        HashSet<RampartConstant> validKeys = new HashSet<RampartConstant>() {
            {
                add(VERSION_KEY);
                add(SCORE_KEY);
                add(VECTOR_KEY);
            }
        };
        RampartFloat version = null;
        RampartFloat score = null;
        RampartString vector = null;
        while (toJavaBoolean(it.hasNext())) {
            RampartObject obj = it.next();
            if (!(obj instanceof RampartNamedValue)) {
                throw new ValidationError(
                        "each value of metadata key \"" + entryKey + "\" must be a key value pair");
            }
            RampartNamedValue attribute = (RampartNamedValue) obj;
            if (attribute.getName().equals(VERSION_KEY)
                    && attribute.getRampartObject() instanceof RampartFloat) {
                version = (RampartFloat) attribute.getRampartObject();
            } else if (attribute.getName().equals(SCORE_KEY)
                    && attribute.getRampartObject() instanceof RampartFloat) {
                score = (RampartFloat) attribute.getRampartObject();
            } else if (attribute.getName().equals(VECTOR_KEY)
                    && attribute.getRampartObject() instanceof RampartString) {
                vector = (RampartString) attribute.getRampartObject();
            } else {
                throw new ValidationError(
                        "invalid config (" + attribute + ") for \"" + entryKey + "\" metadata entry");
            }
            validKeys.remove(attribute.getName());
        }
        if (!validKeys.isEmpty()) {
            throw new ValidationError(
                    "metadata entry \"" + entryKey + "\" is missing the following configs: " + validKeys);
        }
        return new RampartCvssImpl(score, vector, version);
    }
}
