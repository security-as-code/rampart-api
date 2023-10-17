package org.rampart.lang.impl.apiprotect;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.apiprotect.RampartApiFilter;
import org.rampart.lang.impl.utils.ObjectUtils;

public final class RampartApiFilterImpl implements RampartApiFilter {
    private final RampartBoolean isWildcard;
    private final RampartList urlPatterns;

    public static final RampartApiFilter ANY = new RampartApiFilterImpl(RampartBoolean.TRUE, RampartList.EMPTY);

    private RampartApiFilterImpl(RampartBoolean isWildcard, RampartList urlPatterns) {
        this.isWildcard = isWildcard;
        this.urlPatterns = urlPatterns;
    }

    public static RampartApiFilter forPatterns(RampartList patterns) {
        return new RampartApiFilterImpl(RampartBoolean.FALSE, patterns);
    }

    //@Override
    public RampartBoolean onAllTargets() {
        return isWildcard;
    }

    //@Override
    public RampartList getUrlPatterns() {
        return urlPatterns;
    }

    @Override
    public int hashCode() {
        return ObjectUtils.hash(isWildcard, urlPatterns);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RampartApiFilterImpl other = (RampartApiFilterImpl) obj;
        return ObjectUtils.equals(isWildcard, other.isWildcard)
                && ObjectUtils.equals(urlPatterns, other.urlPatterns);
    }
}
