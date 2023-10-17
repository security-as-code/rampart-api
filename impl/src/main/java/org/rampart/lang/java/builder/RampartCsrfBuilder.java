package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartNamedValue;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.http.RampartCsrf;
import org.rampart.lang.impl.core.RampartOptions;
import org.rampart.lang.impl.http.RampartCsrfImpl;
import org.rampart.lang.impl.http.RampartCsrfOptions2_6;

import java.util.ArrayList;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.rampart.lang.java.RampartPrimitives.newRampartNamedValue;

public class RampartCsrfBuilder implements RampartObjectBuilder<RampartCsrf> {
    private RampartConstant csrfAlgorithm;
    private ArrayList<RampartNamedValue> options = new ArrayList<RampartNamedValue>();

    public RampartCsrf createRampartObject() {
        RampartOptions featureOptions = new RampartCsrfOptions2_6();
        for (RampartConstant config : featureOptions.getAllConfigsForTarget(csrfAlgorithm)) {
            if (RampartBuilderUtils.findRampartNamedValueByKey(options, config) == null
                && featureOptions.getDefaults(config) != null) {
                options.add(newRampartNamedValue(config, featureOptions.getDefaults(config)));
            }
        }
        return new RampartCsrfImpl(csrfAlgorithm, newRampartList(options.toArray(new RampartNamedValue[options.size()])));
    }

    public RampartCsrfBuilder addCsrfAlgorithm(RampartConstant csrfAlgorithm) {
        this.csrfAlgorithm = csrfAlgorithm;
        return this;
    }

    /**
     * @param excludedUris non empty RampartList of RampartStrings
     * @return
     */
    public RampartCsrfBuilder addExcludeOption(RampartList excludedUris) {
        this.options.add(newRampartNamedValue(EXCLUDE_KEY, excludedUris));
        return this;
    }

    /**
     * @param methods non empty RampartList of RampartConstants
     * @return
     */
    public RampartCsrfBuilder addMethodOption(RampartList methods) {
        this.options.add(newRampartNamedValue(METHOD_KEY, methods));
        return this;
    }

    public RampartCsrfBuilder addTokenTypeOption(RampartConstant tokenType) {
        this.options.add(newRampartNamedValue(TOKEN_TYPE_KEY, tokenType));
        return this;
    }

    public RampartCsrfBuilder addTokenNameOption(RampartString tokenName) {
        this.options.add(newRampartNamedValue(TOKEN_NAME_KEY, tokenName));
        return this;
    }

    public RampartCsrfBuilder withoutAjaxOption() {
        this.options.add(newRampartNamedValue(AJAX_KEY, NO_VALIDATE_KEY));
        return this;
    }

    public RampartCsrfBuilder withAjaxOption() {
        this.options.add(newRampartNamedValue(AJAX_KEY, VALIDATE_KEY));
        return this;
    }

    /**
     * @param hosts non empty RampartList of RampartStrings
     * @return
     */
    public RampartCsrfBuilder addHostsOption(RampartList hosts) {
        this.options.add(newRampartNamedValue(HOSTS_KEY, hosts));
        return this;
    }
}
