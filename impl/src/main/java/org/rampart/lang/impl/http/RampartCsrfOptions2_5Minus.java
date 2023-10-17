package org.rampart.lang.impl.http;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;

import java.util.Arrays;
import java.util.List;

import org.rampart.lang.api.*;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.impl.core.RampartOptions;
import org.rampart.lang.impl.core.validators.v2.ConfigValueValidator;

public class RampartCsrfOptions2_5Minus implements RampartOptions {

    private static final List<RampartConstant> SYNCHRONIZED_TOKENS_OPTIONS =
            Arrays.asList(EXCLUDE_KEY, METHOD_KEY, TOKEN_NAME_KEY, TOKEN_TYPE_KEY, AJAX_KEY);
    private static final List<RampartConstant> SAME_ORIGIN_OPTIONS2_5 = Arrays.asList(HOSTS_KEY);

    protected List<RampartConstant> sameOriginOptions() {
        return SAME_ORIGIN_OPTIONS2_5;
    }

    // @Override
    public RampartObject getDefaults(RampartConstant config) {
        if (METHOD_KEY.equals(config)) {
            return newRampartList(POST_KEY);
        } else if (TOKEN_TYPE_KEY.equals(config)) {
            return SHARED_KEY;
        } else if (TOKEN_NAME_KEY.equals(config)) {
            return newRampartString("_X-CSRF-TOKEN");
        } else if (AJAX_KEY.equals(config)) {
            return VALIDATE_KEY;
        }
        return null;
    }

    // @Override
    public ConfigValueValidator getOptionValidator(RampartConstant config) {
        if (EXCLUDE_KEY.equals(config)) {
            return ConfigValueValidator.SINGLE_OR_LIST_OF_NOT_EMPTY_URIS_VALIDATOR;
        } else if (METHOD_KEY.equals(config)) {
            return new ConfigValueValidator() {
                private boolean isSupportedHttpMethod(RampartObject method) {
                    return method instanceof RampartConstant && (GET_KEY.equals(method) || POST_KEY.equals(method));
                }

                // @Override
                public RampartObject test(RampartObject obj) {
                    if(obj instanceof RampartConstant) {
                        obj = newRampartList(obj);
                    }
                    if (obj instanceof RampartList) {
                        RampartObjectIterator it = ((RampartList) obj).getObjectIterator();
                        while (it.hasNext() == RampartBoolean.TRUE) {
                            if (!isSupportedHttpMethod(it.next())) {
                                return null;
                            }
                        }
                        return obj;
                    }
                    return null;
                }
            };
        } else if (TOKEN_TYPE_KEY.equals(config)) {
            return new ConfigValueValidator() {
                // @Override
                public RampartObject test(RampartObject obj) {
                    return SHARED_KEY.equals(obj) || UNIQUE_KEY.equals(obj) ? obj : null;
                }
            };
        } else if (TOKEN_NAME_KEY.equals(config)) {
            return new ConfigValueValidator() {
                // @Override
                public RampartObject test(RampartObject obj) {
                    return obj instanceof RampartString ? obj : null;
                }
            };
        } else if (AJAX_KEY.equals(config)) {
            return new ConfigValueValidator() {
                // @Override
                public RampartObject test(RampartObject obj) {
                    return VALIDATE_KEY.equals(obj) || NO_VALIDATE_KEY.equals(obj) ? obj : null;
                }
            };
        } else if (HOSTS_KEY.equals(config)) {
            return ConfigValueValidator.SINGLE_OR_LIST_OF_NOT_EMPTY_HOSTS_VALIDATOR;
        }
        return null;
    }

    // @Override
    public List<RampartConstant> getAllConfigsForTarget(RampartConstant target) {
        if (SYNCHRONIZED_TOKENS_KEY.equals(target)) {
            return SYNCHRONIZED_TOKENS_OPTIONS;
        } else if (SAME_ORIGIN_KEY.equals(target)) {
            return sameOriginOptions();
        }
        return null;
    }
}
