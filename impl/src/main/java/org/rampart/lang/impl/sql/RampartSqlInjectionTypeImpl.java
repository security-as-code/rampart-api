package org.rampart.lang.impl.sql;

import static org.rampart.lang.api.constants.RampartSqlConstants.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartNamedValue;
import org.rampart.lang.api.core.RampartNamedValueIterator;
import org.rampart.lang.api.sql.RampartSqlInjectionType;
import org.rampart.lang.impl.utils.ObjectUtils;

public class RampartSqlInjectionTypeImpl implements RampartSqlInjectionType {

    private final List<RampartNamedValue> configurations;
    private RampartBoolean onSuccessfulAttempt = RampartBoolean.FALSE;
    private RampartBoolean onFailedAttempt = RampartBoolean.FALSE;
    private final String toStringValue;
    private final int hashCode;

    /**
     * Constructor for the RampartSqlInjectionType impl object
     *
     * @param configurations List of configurations to go along with injection types which are
     *        common across all injection types, e.g. permit: query-provided
     *        Should never be null.
     * @param injectionTypes modes for the SQLi protection feature, could be successful or failed
     *        Should never be null.
     */
    public RampartSqlInjectionTypeImpl(Set<Type> injectionTypes, List<RampartNamedValue> configurations) {
        if (injectionTypes == null) {
            throw new NullPointerException("RAMPART SQL injectionTypes should never be null");
        }
        if (configurations == null) {
            throw new NullPointerException("RAMPART SQL configurations should never be null");
        }
        if (injectionTypes.contains(Type.SUCCESSFUL_ATTEMPT)) {
            onSuccessfulAttempt = RampartBoolean.TRUE;
        }
        if (injectionTypes.contains(Type.FAILED_ATTEMPT)) {
            onFailedAttempt = RampartBoolean.TRUE;
        }
        this.configurations = configurations;
        this.toStringValue = createStringRepresentation();
        this.hashCode = ObjectUtils.hash(onSuccessfulAttempt, onFailedAttempt, configurations);
    }

    // @Override
    public RampartBoolean onSuccessfulAttempt() {
        return onSuccessfulAttempt;
    }

    // @Override
    public RampartBoolean onFailedAttempt() {
        return onFailedAttempt;
    }

    public enum Type {
        SUCCESSFUL_ATTEMPT,
        FAILED_ATTEMPT
    }

    /**
     * Deprecated since library version 4.0.0, use getters returing RampartBoolean instead
     */
    @Deprecated
    // @Override
    public RampartNamedValueIterator getConfigurationIterator() {
        return new RampartNamedValueIterator() {
            private int index = 0;

            // @Override
            public RampartBoolean hasNext() {
                return index < configurations.size() ? RampartBoolean.TRUE : RampartBoolean.FALSE;
            }

            // @Override
            public RampartNamedValue next() {
                if (index >= configurations.size()) {
                    throw new NoSuchElementException();
                }
                return configurations.get(index++);
            }
        };
    }

    // @Override
    public RampartBoolean shouldPermitQueryProvided() {
        for (RampartNamedValue param : configurations) {
            if (PERMIT_KEY.equals(param.getName())) {
                return QUERY_PROVIDED_KEY.equals(param.getRampartObject()) ? RampartBoolean.TRUE : RampartBoolean.FALSE;
            }
        }
        return RampartBoolean.FALSE;
    }

    @Override
    public String toString() {
        return toStringValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartSqlInjectionTypeImpl)) {
            return false;
        }
        RampartSqlInjectionTypeImpl otherType = (RampartSqlInjectionTypeImpl) other;
        return ObjectUtils.equals(onSuccessfulAttempt, otherType.onSuccessfulAttempt)
                && ObjectUtils.equals(onFailedAttempt, otherType.onFailedAttempt)
                && ObjectUtils.equals(configurations, otherType.configurations);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    private String createStringRepresentation() {
        StringBuilder builder = new StringBuilder("injection(")
                .append(onSuccessfulAttempt == RampartBoolean.TRUE ? SUCCESSFUL_ATTEMPT_KEY : "")
                .append(onSuccessfulAttempt == RampartBoolean.TRUE && onFailedAttempt == RampartBoolean.TRUE ? ", " : "")
                .append(onFailedAttempt == RampartBoolean.TRUE ? FAILED_ATTEMPT_KEY : "");
        if (!configurations.isEmpty()) {
            String separator = (onSuccessfulAttempt == RampartBoolean.TRUE || onFailedAttempt == RampartBoolean.TRUE)
                    ? ", " : "";
            for (RampartNamedValue config : configurations) {
                builder.append(separator).append(config);
                separator = ", ";
            }
        }
        return builder.append(')').toString();
    }
}
