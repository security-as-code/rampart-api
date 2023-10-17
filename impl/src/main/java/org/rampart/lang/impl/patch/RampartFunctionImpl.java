package org.rampart.lang.impl.patch;

import static org.rampart.lang.api.constants.RampartPatchConstants.*;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.patch.RampartFunction;
import org.rampart.lang.impl.utils.ObjectUtils;

public class RampartFunctionImpl implements RampartFunction {

    private final RampartString functionName;
    private final RampartList checksums;
    private final int hashCode;
    private final String toStringValue;

    public RampartFunctionImpl(RampartString functionName, RampartList checksums) {
        this.functionName = functionName;
        this.checksums = checksums != null ? checksums : RampartList.EMPTY;
        this.hashCode = ObjectUtils.hash(functionName, checksums);
        this.toStringValue = createStringRepresentation();
    }

    //@Override
    public RampartString getFunctionName() {
        return functionName;
    }

    //@Override
    public RampartList getChecksums() {
        return checksums;
    }

    //@Override
    public int hashCode() {
        return hashCode;
    }

    //@Override
    public String toString() {
        return toStringValue;
    }

    private String createStringRepresentation() {
        StringBuilder builder = new StringBuilder();
        builder.append(FUNCTION_KEY).append("(").append(functionName.formatted());
        if (checksums.isEmpty() == RampartBoolean.FALSE) {
            // Note : Checksum list will have been validated at this stage
            // so we can safely assume the list is well formed & non-empty.
            addChecksumStringRep(builder);
        }
        return builder.append(')').toString();
    }

    //@Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartFunctionImpl)) {
            return false;
        }
        RampartFunctionImpl otherFunctionImpl = (RampartFunctionImpl) other;
        return ObjectUtils.equals(functionName, otherFunctionImpl.functionName)
                && ObjectUtils.equals(checksums, otherFunctionImpl.checksums);
    }

    /**
     * Creates String representation of the checksum list
     * Format: checksums: ["checksumValue1", "checksumValue2", "checksumValueN"]
     */
    private void addChecksumStringRep(StringBuilder builder) {
        String delim = "";
        builder.append(", ").append(CHECKSUMS_KEY).append(": [");
        RampartObjectIterator it = checksums.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject checksum = it.next();
            builder.append(delim).append(checksum instanceof RampartString ?
                            ((RampartString) checksum).formatted()
                            : checksum);
            delim = ", ";
        }
        builder.append(']');
    }
}
