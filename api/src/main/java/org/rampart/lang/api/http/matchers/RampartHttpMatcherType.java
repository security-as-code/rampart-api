package org.rampart.lang.api.http.matchers;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.impl.utils.ObjectUtils;

/**
 * Enumeration of possible input validation enforcement types
 * @since 1.4
 */
public abstract class RampartHttpMatcherType implements RampartObject {
    private final RampartConstant name;
    private final int hashCode;

    RampartHttpMatcherType(final String name) {
        this.name = new RampartConstant() {
            @Override
            public String toString() {
                return name;
            }
        };
        this.hashCode = ObjectUtils.hash(name);
    }

    /**
     * Getter for the name RampartConstant of the instance
     * @return 'name' of the instance
     * Note: Will return null in the case of a REGEX type
     */
    public RampartConstant getName() {
        return name;
    }

    @Deprecated
    public static RampartHttpMatcherType fromRampartString(RampartString type) {
        RampartHttpMatcherType matcher;
        if ((matcher = lookupMatcher(type,  RampartPatternMatcher.VALUES)) != null
                || (matcher = lookupMatcher(type, RampartHttpMethodMatcher.VALUES)) != null) {
            return matcher;
        }
        return RampartPatternMatcher.REGEX;
    }

    private static RampartHttpMatcherType lookupMatcher(RampartString type, RampartHttpMatcherType[] valuesToLookInto) {
        for (RampartHttpMatcherType matcherType : valuesToLookInto) {
            if (matcherType.name != null && matcherType.name.asRampartString().equals(type)) {
                return matcherType;
            }
        }
        return null;
    }

    public static RampartHttpMatcherType fromConstant(RampartConstant entry) {
        if (entry == null) {
            return null;
        }
        RampartHttpMatcherType matcher;
        if ((matcher = lookupMatcher(entry.asRampartString(), RampartPatternMatcher.VALUES)) != null
                || (matcher = lookupMatcher(entry.asRampartString(), RampartHttpMethodMatcher.VALUES)) != null) {
            return matcher;
        }
        return null;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return String.valueOf(name.toString());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartHttpMatcherType)) {
            return false;
        }
        RampartHttpMatcherType otherMatcher = (RampartHttpMatcherType) other;
        return ObjectUtils.equals(name, otherMatcher.name);
    }

    /**
     * Identifies how matchers should be evaluated together if there are multiple enforcing matchers of this type.
     *
     * TODO: reconsider this design as this method was added because the agent HttpMethodMatchers and PatternMatchers
     * evaluate multiple matchers differently in the agent's implmentation. And this method makes it easier in the
     * agent to differentiate. However, it makes things a bit confusing from RAMPART language POV. Whether agent
     * implementation or language should changed in the future to make things consistent.
     *
     * @return the join type for this matcher type
     */
    public abstract RampartMatcherJoinType getMatcherJoinType();

    public static final class RampartMatcherJoinType implements RampartObject {
        public static final RampartMatcherJoinType ONE_OF = new RampartMatcherJoinType();
        public static final RampartMatcherJoinType ALL_OF = new RampartMatcherJoinType();
    }
}
