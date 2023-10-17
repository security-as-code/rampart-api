package org.rampart.lang.impl.marshal;

import static org.rampart.lang.api.constants.RampartMarshalConstants.REFERENCE_EXPANSION_LIMIT_KEY;
import static org.rampart.lang.api.constants.RampartMarshalConstants.REFERENCE_KEY;
import static org.rampart.lang.api.constants.RampartMarshalConstants.REFERENCE_LIMIT_KEY;
import static org.rampart.lang.api.constants.RampartMarshalConstants.URI_KEY;
import static org.rampart.lang.api.constants.RampartMarshalConstants.XXE_KEY;
import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartInteger;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.marshal.ExternalXmlEntityConfig;
import org.rampart.lang.impl.utils.ObjectUtils;
import org.rampart.lang.java.RampartPrimitives;

public class ExternalXmlEntityConfigImpl implements ExternalXmlEntityConfig {
    private static final RampartInteger ZERO = RampartPrimitives.newRampartInteger(0);

    private final int hashCode;
    private final RampartList uris;
    private final RampartInteger referenceLimit;
    private final RampartInteger referenceExpansionLimit;

    public ExternalXmlEntityConfigImpl() {
        this(RampartList.EMPTY, ZERO, ZERO);
    }

    public ExternalXmlEntityConfigImpl(RampartList uris,
                                       RampartInteger referenceLimit,
                                       RampartInteger referenceExpansionLimit) {
        this.uris = (uris == null) ? RampartList.EMPTY : uris;
        this.referenceLimit = (referenceLimit == null) ? ZERO : referenceLimit;
        this.referenceExpansionLimit = (referenceExpansionLimit == null) ? ZERO : referenceExpansionLimit;
        hashCode = ObjectUtils.hash(
                this.uris,
                this.referenceLimit,
                this.referenceExpansionLimit);
    }

    // @Override
    public RampartList getUris() {
        return this.uris;
    }

    // @Override
    public RampartInteger getReferenceLimit() {
        return this.referenceLimit;
    }

    // @Override
    public RampartInteger getReferenceExpansionLimit() {
        return this.referenceExpansionLimit;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(XXE_KEY).append("(");
        String delimiter = "";
        if (this.uris.size().isGreaterThan(ZERO)  == RampartBoolean.TRUE) {
            builder.append(URI_KEY).append(": ");
            builder.append(this.uris);
            delimiter = ',' + LINE_SEPARATOR + '\t';
        }
        if (this.referenceLimit.isGreaterThan(ZERO) == RampartBoolean.TRUE
                || this.referenceExpansionLimit.isGreaterThan(ZERO) == RampartBoolean.TRUE) {
            builder.append(delimiter).append(REFERENCE_KEY).append(": {");
            delimiter = "";
            if (this.referenceLimit.isGreaterThan(ZERO) == RampartBoolean.TRUE) {
                builder.append(REFERENCE_LIMIT_KEY).append(": ").append(this.referenceLimit);
                delimiter = ", ";
            }
            if (this.referenceExpansionLimit.isGreaterThan(ZERO) == RampartBoolean.TRUE) {
                builder.append(delimiter).append(REFERENCE_EXPANSION_LIMIT_KEY).append(": ").append(this.referenceExpansionLimit);
            }
            builder.append("}");
        }
        builder.append(")").append(LINE_SEPARATOR);
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ExternalXmlEntityConfigImpl)) {
            return false;
        }
        ExternalXmlEntityConfigImpl that = (ExternalXmlEntityConfigImpl) obj;
        return super.equals(that)
               && ObjectUtils.equals(uris, that.uris)
               && ObjectUtils.equals(referenceLimit, that.referenceLimit)
               && ObjectUtils.equals(referenceExpansionLimit, that.referenceExpansionLimit);
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

}
