package org.rampart.lang.impl.checksum;

import static org.objectweb.asm.Opcodes.ASM7;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * Class to walk the bytecode of a given class
 */
class ChecksumClassVisitor extends ClassVisitor {
    // mask out any undocumented access modifier bits
    private static final int ACCESS_MODIFIER_MASK = 0x3FFFF;
    private ChecksumClassFile checksumClassFile;

    ChecksumClassVisitor(int api) {
        super(api);
    }

    @Override
    public void visit(int version, int accessModifier, String name, String signature, String superName,
            String[] interfaces) {
        checksumClassFile = new ChecksumClassFile(name, superName, interfaces, accessModifier & ACCESS_MODIFIER_MASK);
        super.visit(version, accessModifier, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int accessModifier, String name, String desc, String signature, Object value) {
        checksumClassFile.add(new ChecksumField(name, desc, accessModifier & ACCESS_MODIFIER_MASK));
        return super.visitField(accessModifier, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int accessModifier, String name, String desc, String signature, String[] exceptions) {
        return new ChecksumMethodVisitor(
                ASM7,
                super.visitMethod(accessModifier, name, desc, signature, exceptions),
                checksumClassFile,
                new ChecksumMethod(name, desc, exceptions, accessModifier & ACCESS_MODIFIER_MASK));
    }

    @Override
    public void visitEnd() {
        checksumClassFile.sortFields();
        super.visitEnd();
    }

    ChecksumClassFile getChecksumClassFile() {
        return checksumClassFile;
    }
}

