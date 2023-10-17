package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartFloat;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartCvss;
import org.rampart.lang.impl.core.RampartCvssImpl;

public class RampartCvssBuilder implements RampartObjectBuilder<RampartCvss> {
    private RampartFloat score;
    private RampartString vector;
    private RampartFloat version;

    public RampartCvss createRampartObject() {
        if (score == null || vector == null || version == null) {
            throw new NullPointerException("one of score, vector or version values is null");
        }
        return new RampartCvssImpl(score, vector, version);
    }

    public RampartCvssBuilder addScore(RampartFloat score) {
        this.score = score;
        return this;
    }

    public RampartCvssBuilder addVersion(RampartFloat version) {
        this.version = version;
        return this;
    }

    public RampartCvssBuilder addVector(RampartString vector) {
        this.vector = vector;
        return this;
    }
}
