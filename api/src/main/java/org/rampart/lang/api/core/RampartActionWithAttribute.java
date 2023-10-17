package org.rampart.lang.api.core;

import org.rampart.lang.api.RampartList;

public interface RampartActionWithAttribute extends RampartAction {
    RampartActionTarget getTarget();

    RampartActionAttribute getAttribute();

    /**
     * Returns an RampartList but this list is actually a mapping. All elements contained within are of
     * type RampartNamedValue. This config map should be interpreted in the context of
     * {@link RampartActionTarget} and {@link RampartActionAttribute}, also accessible through {@link RampartActionWithAttribute}
     *
     * @return an RampartList comprised of RampartNamedValues, elements of this mapping
     */
    RampartList getConfigMap();
}
