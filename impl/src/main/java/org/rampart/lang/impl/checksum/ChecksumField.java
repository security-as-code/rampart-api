package org.rampart.lang.impl.checksum;

import java.io.Serializable;
/**
 * Class to represent a field of a class file
 */
class ChecksumField implements Serializable, Comparable<ChecksumField> {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String desc;
    private final int accessModifier;

    ChecksumField(String name, String desc, int accessModifier) {
        this.name = name;
        this.desc= desc;
        this.accessModifier = accessModifier;
    }

    public int compareTo(ChecksumField o) {
        return name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return "\n\t\tname=" + name +
                " desc=" + desc +
                " accessModifiers=" + accessModifier;
    }

}
