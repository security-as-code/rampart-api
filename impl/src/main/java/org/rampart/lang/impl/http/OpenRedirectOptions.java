package org.rampart.lang.impl.http;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.impl.core.RampartOptions;
import org.rampart.lang.impl.core.validators.v2.ConfigValueValidator;

import java.util.Arrays;
import java.util.List;

import static org.rampart.lang.api.constants.RampartHttpConstants.EXCLUDE_KEY;
import static org.rampart.lang.api.constants.RampartHttpConstants.SUBDOMAINS_KEY;

public class OpenRedirectOptions implements RampartOptions {
    // @Override
    public RampartObject getDefaults(RampartConstant config) {
        return null;
    }

    // @Override
    public ConfigValueValidator getOptionValidator(RampartConstant config) {
        if (config.equals(EXCLUDE_KEY)) {
            return new ConfigValueValidator() {
                // @Override
                public RampartObject test(RampartObject obj) {
                    return !obj.equals(SUBDOMAINS_KEY) ? null : SUBDOMAINS_KEY;
                }
            };
        }
        return null;
    }

    // @Override
    public List<RampartConstant> getAllConfigsForTarget(RampartConstant target) {
        return Arrays.asList(EXCLUDE_KEY);
    }
}
