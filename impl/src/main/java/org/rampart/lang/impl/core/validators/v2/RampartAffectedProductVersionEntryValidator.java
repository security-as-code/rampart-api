package org.rampart.lang.impl.core.validators.v2;

import org.rampart.lang.api.*;
import org.rampart.lang.api.core.RampartAffectedProductVersion;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.impl.core.RampartAffectedProductVersionImpl;
import org.rampart.lang.impl.core.ValidationError;
import org.rampart.lang.impl.core.validators.RampartMetadataEntryValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;

public class RampartAffectedProductVersionEntryValidator extends RampartMetadataEntryValidator {
    public RampartAffectedProductVersion validateValue(RampartConstant entryKey, RampartObject value) throws ValidationError {
        if (value instanceof RampartString) {
            List<RampartString> version = Collections.singletonList((RampartString) value);
            return new RampartAffectedProductVersionImpl(version, version);
        }
        if (!(value instanceof RampartList)) {
            throw new ValidationError("metadata value of \"" + entryKey
                    + "\" key must be a list of ranges or a single string literal");
        }
        RampartObjectIterator it = ((RampartList) value).getObjectIterator();
        ArrayList<RampartString> fromVersions = new ArrayList<RampartString>();
        ArrayList<RampartString> toVersions = new ArrayList<RampartString>();
        while (toJavaBoolean(it.hasNext())) {
            RampartObject rangeObject = it.next();
            if (!(rangeObject instanceof RampartNamedValue)
                    || !RANGE_KEY.equals(((RampartNamedValue) rangeObject).getName())) {
                throw new ValidationError(
                        "only key value pairs with \"" + RANGE_KEY + "\" as key are allowed within \""
                                + entryKey + "\"");
            }
            RampartNamedValue range = (RampartNamedValue) rangeObject;
            if (!(range.getRampartObject() instanceof RampartList)
                    || toJavaInt(((RampartList) range.getRampartObject()).size()) != 2)
            {
                throw new ValidationError(
                        "\"" + RANGE_KEY + "\" config must be comprised of 2 key value pairs");
            }
            fromVersions.add(getRangeLimit((RampartList) range.getRampartObject(), FROM_KEY));
            toVersions.add(getRangeLimit((RampartList) range.getRampartObject(), TO_KEY));
        }
        return new RampartAffectedProductVersionImpl(fromVersions, toVersions);
    }

    private static RampartString getRangeLimit(RampartList rangeList, RampartConstant key) throws ValidationError {
        RampartObjectIterator it = rangeList.getObjectIterator();
        while (toJavaBoolean(it.hasNext())) {
            RampartObject element = it.next();
            if (!(element instanceof RampartNamedValue)) {
                throw new ValidationError(
                        "invalid parameter \"" + element + "\" for \"" + RANGE_KEY + "\" config");
            }
            RampartNamedValue keyValuePair = (RampartNamedValue) element;
            if (key.equals(keyValuePair.getName())) {
                if (!(keyValuePair.getRampartObject() instanceof RampartString)) {
                    throw new ValidationError(
                            "\"" + key + "\" in \"" + RANGE_KEY + "\" config must contain a string value");
                }
                return (RampartString) keyValuePair.getRampartObject();
            }
        }
        throw new ValidationError(
                "missing mandatory \"" + key + "\" parameter in \"" + RANGE_KEY + "\" config");
    }
}
