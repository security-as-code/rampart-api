package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartNamedValue;
import org.rampart.lang.api.http.RampartXss;
import org.rampart.lang.impl.core.RampartOptions;
import org.rampart.lang.impl.http.RampartXssImpl;
import org.rampart.lang.impl.http.RampartXssOptions2_5;

import java.util.ArrayList;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.rampart.lang.java.RampartPrimitives.newRampartNamedValue;

public class RampartXssBuilder implements RampartObjectBuilder<RampartXss> {
    private ArrayList<RampartNamedValue> options = new ArrayList<RampartNamedValue>();

    public RampartXss createRampartObject() {
        RampartOptions featureOptions = new RampartXssOptions2_5();
        for (RampartConstant config : featureOptions.getAllConfigsForTarget(HTML_KEY)) {
            if (RampartBuilderUtils.findRampartNamedValueByKey(options, config) == null
                && featureOptions.getDefaults(config) != null) {
                options.add(newRampartNamedValue(config, featureOptions.getDefaults(config)));
            }
        }
        return new RampartXssImpl(newRampartList(options.toArray(new RampartNamedValue[options.size()])));
    }

    public RampartXssBuilder addPolicyOption(RampartConstant policy) {
        this.options.add(newRampartNamedValue(POLICY_KEY, policy));
        return this;
    }

    public RampartXssBuilder addExcludeOption(RampartList excludedUris) {
        this.options.add(newRampartNamedValue(EXCLUDE_KEY, excludedUris));
        return this;
    }

}
