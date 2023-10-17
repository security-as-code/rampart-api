package org.rampart.lang.impl.core;

import org.rampart.lang.api.RampartFloat;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartCvss;
import org.rampart.lang.impl.utils.ObjectUtils;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;

public class RampartCvssImpl implements RampartCvss {
    private final RampartFloat score;
    private final RampartFloat version;
    private final RampartString vector;
    private final String toString;
    private final int hashCode;

    public RampartCvssImpl(RampartFloat score, RampartString vector, RampartFloat version) {
        this.score = score;
        this.version = version;
        this.vector = vector;
        this.hashCode = ObjectUtils.hash(score, version, vector);
        this.toString = createToStringValue();
    }

    public RampartFloat getScore() {
        return score;
    }

    public RampartFloat getVersion() {
        return version;
    }

    public RampartString getVector() {
        return vector;
    }

    @Override
    public String toString() {
        return toString;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartCvssImpl)) {
            return false;
        }
        RampartCvssImpl otherRampartCvss = (RampartCvssImpl) other;
        return ObjectUtils.equals(score, otherRampartCvss.score)
                && ObjectUtils.equals(version, otherRampartCvss.version)
                && ObjectUtils.equals(vector, otherRampartCvss.vector);
    }

    private String createToStringValue() {
        return new StringBuilder("{").append(SCORE_KEY).append(": ").append(score).append(", ")
                .append(VERSION_KEY).append(": ").append(version).append(", ")
                .append(VECTOR_KEY).append(": ").append(vector.formatted()).append("}").toString();
    }
}
