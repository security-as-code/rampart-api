package org.rampart.lang.impl.http;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartNamedValue;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.http.RampartXss;
import org.rampart.lang.impl.utils.ObjectUtils;

public class RampartXssImpl implements RampartXss {
    private final RampartList options;
    private final String toStringValue;
    private final int hashCode;

    public RampartXssImpl(RampartList options) {
        this.options = options;
        this.toStringValue = createStringRepresentation();
        this.hashCode = ObjectUtils.hash(options);
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
        } else if (!(other instanceof RampartXssImpl)) {
            return false;
        }
        RampartXssImpl otherXssImpl = (RampartXssImpl) other;
        return ObjectUtils.equals(options, otherXssImpl.options);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    private String createStringRepresentation() {
        StringBuilder builder = new StringBuilder(
                XSS_KEY.toString()).append('(').append(HTML_KEY);
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
        return EXCLUDE_KEY.equals(option.getName())
                && ((RampartList) option.getRampartObject()).isEmpty() == RampartBoolean.TRUE;
    }
}
