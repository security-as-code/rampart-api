package org.rampart.lang.impl.checksum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class to represent the contents of a given class file
 */
class ChecksumClassFile implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String superclass;
    private final List<String> interfaces;
    private final int accessModifier;
    private final List<ChecksumField> classFields;
    private final List<ChecksumMethod> classMethods;

    ChecksumClassFile(String name, String superclass, String[] interfaces, int accessModifier) {
        this.name = name;
        this.superclass = superclass;
        this.interfaces = (interfaces == null)
                ? Collections.<String>emptyList()
                : Arrays.asList(interfaces);
        this.accessModifier = accessModifier;
        this.classFields = new ArrayList<ChecksumField>();
        this.classMethods = new ArrayList<ChecksumMethod>();
    }

    void add(ChecksumField field) {
        classFields.add(field);
    }

    void add(ChecksumMethod method) {
        classMethods.add(method);
    }

    void sortFields() {
        Collections.sort(classFields);
        Collections.sort(classMethods);
        Collections.sort(interfaces);
    }

    List<ChecksumMethod> getClassMethods() {
        return classMethods;
    }

    @Override
    public String toString() {
        return "\tclassName=" + name +
                "\n\tsuperClassName=" + superclass +
                "\n\tinterfaces=" + interfaces +
                "\n\taccessModifiers=" + accessModifier +
                "\n\tclassFields=" + classFields +
                "\n\tclassMethods=" + classMethods ;
    }
}
