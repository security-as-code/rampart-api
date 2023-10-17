package org.rampart.lang.api.http;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;

public interface RampartCsrf extends RampartObject {

    RampartConstant getAlgorithm();

    /**
     * Gets the options for the selected algorithm.
     * @return an RampartList filled with RampartNamedValue instances
     */
    RampartList getConfigMap();
}
