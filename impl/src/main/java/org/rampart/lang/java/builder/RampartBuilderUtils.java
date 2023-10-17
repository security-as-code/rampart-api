package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartNamedValue;

import java.util.Collection;

public class RampartBuilderUtils {

    public static RampartNamedValue findRampartNamedValueByKey(Collection<RampartNamedValue> collection, RampartConstant key) {
        if (key != null) {
            for (RampartNamedValue element : collection) {
                if (key.equals(element.getName())) {
                    return element;
                }
            }
        }
        return null;
    }
}
