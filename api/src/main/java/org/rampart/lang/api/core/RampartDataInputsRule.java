package org.rampart.lang.api.core;

import org.rampart.lang.api.RampartList;

/**
 * A rule implementing this type will be able to work with tainted data inputs, e.g., http,
 * database, deserialization. This is the class used for any sort of configuration for any tainting
 * engine implemented on any agent.
 */
public interface RampartDataInputsRule {
    /**
     * @return an RampartList of the RampartInput type or an empty RampartList if none were specified
     */
    RampartList getDataInputs();

}
