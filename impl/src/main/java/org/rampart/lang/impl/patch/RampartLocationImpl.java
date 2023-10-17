package org.rampart.lang.impl.patch;

import static org.rampart.lang.api.constants.RampartPatchConstants.*;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.patch.RampartLocation;
import org.rampart.lang.api.patch.RampartPatchType;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;
import org.rampart.lang.impl.utils.ObjectUtils;

/**
 * WARNING: This class MUST be immutable or it will affect the hashcode computation
 */
public class RampartLocationImpl implements RampartLocation {

    private final RampartPatchType type;
    private final RampartObject parameter;
    private final RampartList occurrences;
    private final int hashCode;
    private final String toStringValue;

    public RampartLocationImpl(RampartPatchType type, RampartObject parameter, RampartList occurrences) {
        this.type = type;
        this.parameter = parameter;
        this.occurrences = occurrences != null ? occurrences : RampartList.EMPTY;
        this.hashCode = ObjectUtils.hash(type, parameter, occurrences);
        this.toStringValue = createStringRepresentation();
    }

    //@Override
    public RampartObject getTarget() {
        return parameter;
    }

    //@Override
    public RampartPatchType getType() {
        return type;
    }

    //@Override
    public RampartList getOccurrences() {
        return occurrences;
    }

    @Override
    public String toString() {
        return toStringValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartLocationImpl)) {
            return false;
        }
        RampartLocationImpl otherLocationImpl = (RampartLocationImpl) other;
        return ObjectUtils.equals(type, otherLocationImpl.type)
                && ObjectUtils.equals(parameter, otherLocationImpl.parameter)
                && ObjectUtils.equals(occurrences, otherLocationImpl.occurrences);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    private String createStringRepresentation() {
        StringBuilder builder = new StringBuilder(type.toString().toLowerCase()).append('(');
        switch (type) {
            case ENTRY:
            case EXIT:
                break;
            case ERROR:
                builder.append(RampartInterpreterUtils.findFirstRampartString(parameter).formatted());
                break;
            case LINE:
            case INSTRUCTION:
                builder.append(RampartInterpreterUtils.findFirstRampartInteger(parameter));
                if (occurrences.isEmpty() == RampartBoolean.FALSE) {
                    builder.append(", ").append(OCCURRENCES_KEY).append(": ").append(occurrences);
                }
                break;
            default:
                builder.append(RampartInterpreterUtils.findFirstRampartString(parameter).formatted());
                if (occurrences.isEmpty() == RampartBoolean.FALSE) {
                    builder.append(", ").append(OCCURRENCES_KEY).append(": ").append(occurrences);
                }
        }
        return builder.append(')').toString();
    }
}
