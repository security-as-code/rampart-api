package org.rampart.lang.impl.core.validators.v2;

import static org.rampart.lang.java.RampartPrimitives.newRampartList;

import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.impl.http.validators.v2.utils.RampartHttpValidatorUtils;


public interface ConfigValueValidator {
    ConfigValueValidator SINGLE_OR_LIST_OF_NOT_EMPTY_HOSTS_VALIDATOR = new ConfigValueValidator() {
        public RampartObject test(RampartObject obj) {
            if (obj instanceof RampartString) {
                obj = newRampartList(obj);
            }
            if (!RampartHttpValidatorUtils.isValidListOfNonEmptyHosts(obj)) {
                return null;
            }
            return obj;
        }
    };

    ConfigValueValidator SINGLE_OR_LIST_OF_NOT_EMPTY_URIS_VALIDATOR = new ConfigValueValidator() {
        public RampartObject test(RampartObject obj) {
            if (obj instanceof RampartString) {
                obj = newRampartList(obj);
            }
            if (!RampartHttpValidatorUtils.isValidListOfNonEmptyUris(obj)) {
                return null;
            }
            return obj;
        }
    };

    RampartObject test(RampartObject obj);

}