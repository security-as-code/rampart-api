package org.rampart.lang.impl.library;

import static org.rampart.lang.api.constants.RampartLibraryConstants.*;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.api.library.RampartLibrary;
import org.rampart.lang.impl.core.RampartActionableRuleBase;
import org.rampart.lang.impl.utils.ObjectUtils;

/**
 * Class to model an Rampart library rule
 * Eg.
 *  library("native library loading alert rule"):
 *      load("some.so")
 *      action(detect: "an attempt was made to load some.so", 10)
 *  endlibrary
 * @since 1.5
 */
public class RampartLibraryImpl extends RampartActionableRuleBase implements RampartLibrary {
    private final RampartList libraryList;
    private final String toStringValue;
    private final int hashCode;

    public RampartLibraryImpl(RampartString appName, RampartString ruleName, RampartList libraryList, RampartAction ruleAction,
                              RampartList targetOSList, RampartMetadata metadata) {
        super(appName, ruleName, ruleAction, targetOSList, metadata);
        this.libraryList = libraryList;
        this.toStringValue = super.toString();
        this.hashCode = ObjectUtils.hash(libraryList, super.hashCode());
    }

    // @Override
    public RampartList getLibraryList() {
        return libraryList;
    }

    // @Override
    public RampartRuleType getRuleType() {
        return RampartRuleType.LIBRARY;
    }

    @Override
    public String toString() {
        return toStringValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartLibraryImpl)) {
            return false;
        }
        RampartLibraryImpl otherLibrary = (RampartLibraryImpl) other;
        return ObjectUtils.equals(libraryList, otherLibrary.libraryList)
                && super.equals(otherLibrary);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    protected void appendRuleBody(StringBuilder builder) {
        builder.append('\t').append(LOAD_KEY).append('(').append(pathListToString()).append(')').append(LINE_SEPARATOR);
        super.appendRuleBody(builder);
    }

    /**
     * Creates String representation of the path list
     * Format: "path1", "path2", "path3"
     * @return String - representation of the path list
     */
    private String pathListToString() {
        StringBuilder builder = new StringBuilder();
        String delim = "";
        RampartObjectIterator it = libraryList.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject path = it.next();
            builder.append(delim).append(path instanceof RampartString ?
                    ((RampartString) path).formatted()
                    : path);
            delim = ", ";
        }
        return builder.toString();
    }
}
