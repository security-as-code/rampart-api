package org.rampart.lang.java.builder;

import static org.rampart.lang.api.constants.RampartSqlConstants.PERMIT_KEY;
import static org.rampart.lang.api.constants.RampartSqlConstants.QUERY_PROVIDED_KEY;
import static org.rampart.lang.impl.sql.RampartSqlInjectionTypeImpl.Type.FAILED_ATTEMPT;
import static org.rampart.lang.impl.sql.RampartSqlInjectionTypeImpl.Type.SUCCESSFUL_ATTEMPT;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

import org.rampart.lang.api.RampartNamedValue;
import org.rampart.lang.api.sql.RampartSqlInjectionType;
import org.rampart.lang.impl.sql.RampartSqlInjectionTypeImpl;
import org.rampart.lang.impl.sql.RampartSqlInjectionTypeImpl.Type;
import org.rampart.lang.java.RampartPrimitives;

public class RampartSqlInjectionTypeBuilder implements RampartObjectBuilder<RampartSqlInjectionType> {
    private Set<Type> injectionTypes = EnumSet.noneOf(Type.class);
    private ArrayList<RampartNamedValue> configurations = new ArrayList<RampartNamedValue>();

    public RampartSqlInjectionType createRampartObject() {
        return new RampartSqlInjectionTypeImpl(injectionTypes, configurations);
    }

    public RampartSqlInjectionTypeBuilder setSuccessfulAttemptInjections() {
        this.injectionTypes.add(SUCCESSFUL_ATTEMPT);
        return this;
    }

    public RampartSqlInjectionTypeBuilder setFailedAttemptInjections() {
        this.injectionTypes.add(FAILED_ATTEMPT);
        return this;
    }

    /**
     * Deprecated since library version 4.0.0, use setPermitQueryProvided instead
     */
    @Deprecated
    public RampartSqlInjectionTypeBuilder addConfiguration(RampartNamedValue configuration) {
        this.configurations.add(configuration);
        return this;
    }

    public RampartSqlInjectionTypeBuilder setPermitQueryProvided() {
        return addConfiguration(RampartPrimitives.newRampartNamedValue(PERMIT_KEY, QUERY_PROVIDED_KEY));
    }
}
