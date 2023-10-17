package org.rampart.lang.impl.http;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.impl.core.validators.v2.ConfigValueValidator;

import java.util.Arrays;
import java.util.List;

import static org.rampart.lang.api.constants.RampartHttpConstants.EXCLUDE_KEY;
import static org.rampart.lang.api.constants.RampartHttpConstants.POLICY_KEY;

public class RampartXssOptions2_5 extends RampartXssOptions2_0 {
    private static final List<RampartConstant> HTML_OPTIONS = Arrays.asList(POLICY_KEY, EXCLUDE_KEY);

    // @Override
    public ConfigValueValidator getOptionValidator(RampartConstant config) {
        ConfigValueValidator superConfigValueValidator = super.getOptionValidator(config);
        if (superConfigValueValidator != null) {
            return superConfigValueValidator;
        }
        if (EXCLUDE_KEY.equals(config)) {
            return ConfigValueValidator.SINGLE_OR_LIST_OF_NOT_EMPTY_URIS_VALIDATOR;
        }
        return null;
    }

    // @Override
    public List<RampartConstant> getAllConfigsForTarget(RampartConstant target) {
        return HTML_OPTIONS;
    }
}
