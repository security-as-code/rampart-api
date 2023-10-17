package org.rampart.lang.impl.apiprotect.writers;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.apiprotect.RampartApiFilter;
import org.rampart.lang.api.constants.RampartApiProtectConstants;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.impl.apiprotect.parsers.RampartApiFilterParser;

/**
 * Writer for RAMPART API rules.
 */
public class RampartApiFilterWriter {
    private RampartApiFilterWriter() {
        throw new UnsupportedOperationException();
    }

    /**
     * Appends the RAMPART API filter to the builders with the default key used for the input list.
     * @param builder builder to append data to.
     * @param apiFilter filter specification to append. Could be <code>null</code>, in this
     *   case the output clause is not created.
     */
    public static void appendTo(StringBuilder builder, RampartApiFilter apiFilter) {
        appendTo(builder, RampartApiFilterParser.API_FILTER_KEY, apiFilter);
    }


    private static void appendTo(StringBuilder builder, RampartConstant fieldName, RampartApiFilter apiFilter) {
        if (apiFilter == null) {
            return;
        }

        builder.append('\t').append(fieldName).append('(');

        if (apiFilter.onAllTargets() == RampartBoolean.TRUE) {
            builder.append(RampartApiProtectConstants.ANY_KEY);
        } else {
            String delim = "";
            RampartObjectIterator it = apiFilter.getUrlPatterns().getObjectIterator();
            while (it.hasNext() == RampartBoolean.TRUE) {
                builder.append(delim).append(((RampartString) it.next()).formatted());
                delim = ", ";
            }
        }

        builder.append(')').append(RampartObject.LINE_SEPARATOR);
    }
}
