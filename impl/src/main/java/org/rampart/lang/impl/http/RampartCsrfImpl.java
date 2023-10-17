package org.rampart.lang.impl.http;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;

import org.rampart.lang.api.*;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.http.RampartCsrf;
import org.rampart.lang.impl.utils.ObjectUtils;

public class RampartCsrfImpl implements RampartCsrf {

    private final RampartConstant csrfAlgorithm;
    private final RampartList options;
    private final String toStringValue;
    private final int hashCode;

    public RampartCsrfImpl(RampartConstant csrfAlgorithm, RampartList options) {
        this.csrfAlgorithm = csrfAlgorithm;
        this.options = options;
        this.toStringValue = createStringRepresentation();
        this.hashCode = ObjectUtils.hash(csrfAlgorithm, options);
    }

    // @Override
    public RampartConstant getAlgorithm() {
        return csrfAlgorithm;
    }

    // @Override
    public RampartList getConfigMap() {
        return options;
    }

    @Override
    public String toString() {
        return toStringValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartCsrfImpl)) {
            return false;
        }
        RampartCsrfImpl otherCsrfImpl = (RampartCsrfImpl) other;
        return ObjectUtils.equals(csrfAlgorithm, otherCsrfImpl.csrfAlgorithm)
                && ObjectUtils.equals(options, otherCsrfImpl.options);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    private String createStringRepresentation() {
        StringBuilder builder = new StringBuilder(
                CSRF_KEY.toString()).append('(').append(csrfAlgorithm);
        if (options.isEmpty() == RampartBoolean.FALSE) {
            builder.append(", ").append(OPTIONS_KEY).append(": {");

            // this is a special RampartList, cannot use plain toString
            String delim = "";
            RampartObjectIterator it = options.getObjectIterator();
            while (it.hasNext() == RampartBoolean.TRUE) {
                RampartObject option = it.next();
                if (skipOption((RampartNamedValue) option)) {
                    continue;
                }
                builder.append(delim).append(option);
                delim = ", ";
            }
            builder.append('}');
        }
        return builder.append(')').toString();
    }

    private static boolean skipOption(RampartNamedValue option) {
        return HOSTS_KEY.equals(option.getName())
                    && ((RampartList) option.getRampartObject()).isEmpty() == RampartBoolean.TRUE
                || EXCLUDE_KEY.equals(option.getName())
                    && ((RampartList) option.getRampartObject()).isEmpty() == RampartBoolean.TRUE;
    }
}
