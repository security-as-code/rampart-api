package org.rampart.lang.impl.process;

import static org.rampart.lang.api.constants.RampartProcessConstants.*;
import static org.rampart.lang.api.core.RampartRuleType.PROCESS;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.apiprotect.RampartApiFilter;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.api.process.RampartProcess;
import org.rampart.lang.impl.apiprotect.writers.RampartApiFilterWriter;
import org.rampart.lang.impl.core.RampartActionableRuleBase;
import org.rampart.lang.impl.core.writers.v2.RampartInputWriter;
import org.rampart.lang.impl.utils.ObjectUtils;

/**
 * Class to model an Rampart process rule
 * Eg.
 *  process("deny netcat execution process rule"):
 *      execute("/usr/bin/nc")
 *      action(protect: "an attempt to execute netcat was made", 10)
 *  endprocess
 * @since 1.5
 */
public class RampartProcessImpl extends RampartActionableRuleBase implements RampartProcess {
    private final RampartList processList;
    private final String toStringValue;
    private final int hashCode;
    private final RampartList dataInputsList;
    private final RampartApiFilter apiFilter;

    public RampartProcessImpl(RampartString appName, RampartString ruleName, RampartList processList, RampartAction ruleAction,
                              RampartList targetOSList, RampartMetadata metadata, RampartList dataInputsList, RampartApiFilter apiFilter) {
        super(appName, ruleName, ruleAction, targetOSList, metadata);
        this.processList = processList;
        this.dataInputsList = dataInputsList;
        this.apiFilter = apiFilter;
        this.toStringValue = super.toString();
        this.hashCode = ObjectUtils.hash(processList, dataInputsList, apiFilter, super.hashCode());
    }

    // @Override
    public RampartList getProcessList() {
        return processList;
    }

    // @Override
    public RampartList getDataInputs() {
        return dataInputsList;
    }

    // @Override
    public RampartRuleType getRuleType() {
        return PROCESS;
    }


    //@Override
    public RampartApiFilter getApiFilter() {
        return apiFilter;
    }

    @Override
    public String toString() {
        return toStringValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartProcessImpl)) {
            return false;
        }
        RampartProcessImpl otherProcess = (RampartProcessImpl) other;
        return ObjectUtils.equals(processList, otherProcess.processList)
                && ObjectUtils.equals(dataInputsList, otherProcess.dataInputsList)
                && ObjectUtils.equals(apiFilter, otherProcess.apiFilter)
                && super.equals(otherProcess);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    protected void appendRuleBody(StringBuilder builder){
        builder.append('\t').append(EXECUTE_KEY).append('(').append(pathListToString()).append(')').append(LINE_SEPARATOR);
        RampartApiFilterWriter.appendTo(builder, apiFilter);
        RampartInputWriter.appendTo(builder, dataInputsList);
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
        RampartObjectIterator it = processList.getObjectIterator();
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
