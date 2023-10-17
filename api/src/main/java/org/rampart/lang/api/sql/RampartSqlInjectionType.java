package org.rampart.lang.api.sql;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartNamedValueIterator;

public interface RampartSqlInjectionType extends RampartObject {
    RampartBoolean onSuccessfulAttempt();

    RampartBoolean onFailedAttempt();

    /**
     * Deprecated since library version 4.0.0, use getters returning RampartBoolean instead
     */
    @Deprecated
    RampartNamedValueIterator getConfigurationIterator();

    RampartBoolean shouldPermitQueryProvided();
}
