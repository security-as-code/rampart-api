package org.rampart.lang.impl.http;

import static org.rampart.lang.api.constants.RampartHttpConstants.HOSTS_KEY;
import static org.rampart.lang.api.constants.RampartHttpConstants.OPEN_REDIRECT_KEY;
import static org.rampart.lang.api.constants.RampartHttpConstants.OPTIONS_KEY;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.impl.utils.ObjectUtils;

public class RampartOpenRedirectImpl2_7 extends RampartOpenRedirectImpl {

    private final RampartList hosts;
    private final String toStringValue;
    private final int hashCode;


    public RampartOpenRedirectImpl2_7() {
        this(RampartList.EMPTY, RampartList.EMPTY);
    }

    public RampartOpenRedirectImpl2_7(RampartList options, RampartList hosts) {
        super(options);
        this.hosts = hosts;
        this.toStringValue = createStringRepresentation();
        this.hashCode = ObjectUtils.hash(options, hosts);
    }

    /**
     * @since RAMPART/2.7
     */
    public RampartList getHosts() {
        return hosts;
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        } else if (!(other instanceof RampartOpenRedirectImpl2_7)) {
            return false;
        }
        RampartOpenRedirectImpl2_7 otherOpenRedirectImplImpl = (RampartOpenRedirectImpl2_7) other;
        return ObjectUtils.equals(this.hosts, otherOpenRedirectImplImpl.hosts);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return toStringValue;
    }

    private String createStringRepresentation() {
        StringBuilder builder = new StringBuilder(OPEN_REDIRECT_KEY.toString()).append('(');
        String openRedirectDelimiter = "";
        if (hosts.isEmpty() == RampartBoolean.FALSE) {
            builder.append(HOSTS_KEY).append(": ").append(hosts);
            openRedirectDelimiter = ", ";
        }
        if (options.isEmpty() == RampartBoolean.FALSE) {
            builder.append(openRedirectDelimiter).append(OPTIONS_KEY).append(": {");
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
