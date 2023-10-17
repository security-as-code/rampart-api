package org.rampart.lang.api.library;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartActionableRule;

public interface RampartLibrary extends RampartActionableRule {
    RampartList getLibraryList();
}
