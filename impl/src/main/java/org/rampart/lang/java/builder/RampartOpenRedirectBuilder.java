package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartNamedValue;
import org.rampart.lang.api.http.RampartOpenRedirect;
import org.rampart.lang.impl.http.RampartOpenRedirectImpl2_7;

import java.util.ArrayList;

import static org.rampart.lang.api.constants.RampartHttpConstants.EXCLUDE_KEY;
import static org.rampart.lang.api.constants.RampartHttpConstants.SUBDOMAINS_KEY;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.rampart.lang.java.RampartPrimitives.newRampartNamedValue;

public class RampartOpenRedirectBuilder implements RampartObjectBuilder<RampartOpenRedirect> {
    private ArrayList<RampartNamedValue> options = new ArrayList<RampartNamedValue>();
    private RampartList hostsList = RampartList.EMPTY;

    public RampartOpenRedirect createRampartObject() {
        return new RampartOpenRedirectImpl2_7(
                newRampartList(options.toArray(new RampartNamedValue[options.size()])),
                hostsList);
    }

    /**
     * Deprecated since library version 4.0.0, use addHosts(RampartList) and setExcludeSubdomains() directly
     */
    @Deprecated
    public RampartOpenRedirectBuilder addExcludeOption(RampartConstant option) {
        this.options.add(newRampartNamedValue(EXCLUDE_KEY, option));
        return this;
    }

    /**
     * Defines the list of hosts that this rule is for.
     * Host are matched exactly, wildcards are not supported.
     *
     * @param hosts non empty RampartList of RampartStrings
     * @return this builder
     */
    public RampartOpenRedirectBuilder addHosts(RampartList hosts) {
        if (hosts != null) {
            this.hostsList = hosts;
        }
        return this;
    }

    public RampartOpenRedirectBuilder setExcludeSubdomains() {
        return addExcludeOption(SUBDOMAINS_KEY);
    }
}
