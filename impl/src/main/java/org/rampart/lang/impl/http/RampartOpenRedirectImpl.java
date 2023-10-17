package org.rampart.lang.impl.http;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.http.RampartOpenRedirect;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;
import org.rampart.lang.impl.utils.ObjectUtils;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;

public class RampartOpenRedirectImpl implements RampartOpenRedirect {

    protected final RampartList options;
    private final String toStringValue;
    private final int hashCode;

    public RampartOpenRedirectImpl() {
        this(RampartList.EMPTY);
    }

    public RampartOpenRedirectImpl(RampartList options) {
        this.options = options;
        this.toStringValue = createStringRepresentation();
        this.hashCode = ObjectUtils.hash(options);
    }

    /**
     * @since RAMPART/2.7
     */
    public RampartList getHosts() {
        return RampartList.EMPTY;
    }

    /**
     * @since library version 4.0.0
     */
    public RampartBoolean shouldExcludeSubdomains() {
        RampartObject excludeOption = RampartInterpreterUtils.findRampartNamedValue(EXCLUDE_KEY, options);
        return SUBDOMAINS_KEY.equals(excludeOption) ? RampartBoolean.TRUE : RampartBoolean.FALSE;
    }

    @Override
    public String toString() {
        return toStringValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartOpenRedirectImpl)) {
            return false;
        }
        RampartOpenRedirectImpl otherOpenRedirectImplImpl = (RampartOpenRedirectImpl) other;
        return ObjectUtils.equals(options, otherOpenRedirectImplImpl.options);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    private String createStringRepresentation() {
        StringBuilder builder = new StringBuilder(OPEN_REDIRECT_KEY.toString()).append('(');
        if (options.isEmpty() == RampartBoolean.FALSE) {
            builder.append(OPTIONS_KEY).append(": {");

            // this is a special RampartList, cannot use plain toString
            String delim = "";
            RampartObjectIterator it = options.getObjectIterator();
            while (it.hasNext() == RampartBoolean.TRUE) {
                builder.append(delim).append(it.next());
                delim = ", ";
            }
            builder.append('}');
        }
        return builder.append(')').toString();
    }

}
