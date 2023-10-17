package org.rampart.lang.impl.sanitization;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.sanitization.RampartIgnore;
import org.rampart.lang.impl.utils.ObjectUtils;

public class RampartIgnoreImpl implements RampartIgnore {

    private final int hashCode;
    private final RampartList payload;
    private final RampartList attribute;

    public RampartIgnoreImpl(RampartList payload, RampartList attribute) {
        this.payload = payload;
        this.attribute = attribute;
        hashCode = ObjectUtils.hash(this.payload, this.attribute);
    }

    // @Override
    public RampartList getPayload() {
        return this.payload;
    }

    // @Override
    public RampartList getAttribute() {
        return this.attribute;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('\t').append("ignore(");
        boolean isNext = false;
        if (payload != null
                && payload.isEmpty() == RampartBoolean.FALSE) {
            builder.append("payload: ").append(payload);
            isNext = true;
        }
        if (attribute != null
                && attribute.isEmpty() == RampartBoolean.FALSE) {
            if (isNext) {
                builder.append(", ");
            }
            builder.append("attribute: ").append(attribute);
        }
        builder.append(")").append(LINE_SEPARATOR);
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RampartIgnoreImpl)) {
            return false;
        }
        RampartIgnoreImpl that = (RampartIgnoreImpl) obj;
        return ObjectUtils.equals(payload, that.payload)
               && ObjectUtils.equals(attribute, that.attribute);
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

}
