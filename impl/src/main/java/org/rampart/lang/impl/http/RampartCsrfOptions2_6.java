package org.rampart.lang.impl.http;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;

import java.util.Arrays;
import java.util.List;

import org.rampart.lang.api.RampartConstant;

public class RampartCsrfOptions2_6 extends RampartCsrfOptions2_5Minus {

    private static final List<RampartConstant> SAME_ORIGIN_OPTIONS2_6 = Arrays.asList(EXCLUDE_KEY, HOSTS_KEY);

    @Override
    protected List<RampartConstant> sameOriginOptions() {
        return SAME_ORIGIN_OPTIONS2_6;
    }

}
