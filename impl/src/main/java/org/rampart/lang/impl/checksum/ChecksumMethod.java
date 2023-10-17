package org.rampart.lang.impl.checksum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class to represent a Method of a class file
 */
class ChecksumMethod implements Serializable, Comparable<ChecksumMethod> {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String descriptor;
    private final List<String> exceptions;
    private final int accessModifier;
    private final List<ChecksumInstruction> instructions;

    ChecksumMethod(String name, String descriptor, String[] exceptions, int accessModifier) {
        this.name = name;
        this.descriptor = descriptor;
        this.exceptions = (exceptions == null)
                ? Collections.<String>emptyList()
                : Arrays.asList(exceptions);
        this.accessModifier = accessModifier;
        this.instructions = new ArrayList<ChecksumInstruction>();
    }

    void addAll(List<ChecksumInstruction> instructionList) {
        instructions.addAll(instructionList);
    }

    public int compareTo(ChecksumMethod o) {
        return this.name.compareTo(o.name);
    }

    boolean matchesSignature(String signature) {
        return (this.name + this.descriptor).equals(signature);
    }

    @Override
    public String toString() {
        return "\n\t\tname=" + name +
                " descriptor=" + descriptor +
                " exceptions=" + exceptions +
                " accessModifiers=" + accessModifier +
                " instructions=" + instructions;
    }
}
