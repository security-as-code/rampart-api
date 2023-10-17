package org.rampart.lang.impl.http;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.impl.core.RampartOptions;
import org.rampart.lang.impl.core.validators.v2.ConfigValueValidator;

import java.util.Collections;
import java.util.List;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;

public class RampartXssOptions2_0 implements RampartOptions {
    private static final List<RampartConstant> HTML_OPTIONS = Collections.singletonList(POLICY_KEY);

    // @Override
    public RampartObject getDefaults(RampartConstant config) {
        if (POLICY_KEY.equals(config)) {
            return LOOSE_KEY;
        }
        return null;
    }

    // @Override
    public ConfigValueValidator getOptionValidator(RampartConstant config) {
        if (POLICY_KEY.equals(config)) {
            return new ConfigValueValidator() {
                // @Override
                public RampartObject test(RampartObject obj) {
                    return LOOSE_KEY.equals(obj) || STRICT_KEY.equals(obj) ? obj : null;
                }
            };
        }
        return null;
    }

    // @Override
    public List<RampartConstant> getAllConfigsForTarget(RampartConstant target) {
        return HTML_OPTIONS;
    }
}
