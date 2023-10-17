package org.rampart.lang.impl.core.validators.v2;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartVersion;
import org.rampart.lang.java.InvalidRampartAppException;
import org.rampart.lang.impl.core.validators.RampartAppValidatorBase;

import java.util.Map;

/**
 * Class to validate the app declaration and its requires block
 * Eg.
 *  app("appName"):
 *      requires(version : "RAMPART/1.1")
 */

public class RampartAppValidatorUpTo2_3 extends RampartAppValidatorBase {
    public RampartAppValidatorUpTo2_3(RampartList appValues, RampartVersion requiredVersion, Map<String, RampartList> appDeclarations) {
        super(appValues, requiredVersion, appDeclarations);
    }

    @Override
    protected void validateAppDeclarations(Map<String, RampartList> appDeclarations) {
        if (!appDeclarations.isEmpty()) {
            throw new InvalidRampartAppException(
                    "RAMPART language version does not support \"" + appDeclarations.keySet().iterator().next()
                            + "\" declaration");
        }
    }

    @Override
    protected void addToBuilder(String key, RampartList parameters) {
        throw new IllegalStateException("unsupported operation for this validator");
    }
}
