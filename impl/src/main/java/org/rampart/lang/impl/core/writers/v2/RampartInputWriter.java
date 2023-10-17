package org.rampart.lang.impl.core.writers.v2;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.impl.core.parsers.v2.RampartInputParser;

/**
 * Writer of RAMPART input (tainting) clauses like
 * <code>
 *   input(http)
 * </code>
 *
 * This implementation is compatible with the formats recognized by the {@link RampartInputParser}.
 */
public final class RampartInputWriter {
    private RampartInputWriter() {
        throw new UnsupportedOperationException();
    }


    /**
     * Appends the input list to the builders with the customizable key used for the input list.
     * @param builder builder to append data to.
     * @param fieldName name of the field to use for the inputs.
     * @param inputs input specifications that should be output. Could be <code>null</code>, in this
     *   case the output clause is not created.
     */
    private static void appendTo(StringBuilder builder, RampartConstant fieldName, RampartList inputs) {
        if (inputs == null || inputs.isEmpty() == RampartBoolean.TRUE) {
            return;
        }
        builder.append('\t').append(fieldName).append('(');
        String delim = "";
        RampartObjectIterator it = inputs.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            builder.append(delim).append(it.next());
            delim = ", ";
        }
        builder.append(')').append(RampartObject.LINE_SEPARATOR);
    }



    /**
     * Appends the input list to the builders with the default key used for the input list.
     * @param builder builder to append data to.
     * @param inputs input specifications that should be output. Could be <code>null</code>, in this
     *   case the output clause is not created.
     */
    public static void appendTo(StringBuilder builder, RampartList inputs) {
        appendTo(builder, RampartInputParser.INPUT_KEY, inputs);
    }
}
