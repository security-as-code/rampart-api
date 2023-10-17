package org.rampart.lang.impl.checksum;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Class to represent a java bytecode instruction and its (optional) parameters
 */
class ChecksumInstruction implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int opcode;
    private final String[] parameters;

    ChecksumInstruction(int opcode, String[] parameters) {
        this.opcode = opcode;
        this.parameters = parameters;
    }

    ChecksumInstruction(int opcode) {
        this(opcode, null);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("\n\t\t\topcode=" + opcode);
        if (parameters != null) {
            b.append(" parameters=").append(Arrays.toString(parameters));
        }
        return b.toString();
    }
}
