package org.rampart.lang.impl.checksum;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Class to walk the bytecode of a given method
 */
class ChecksumMethodVisitor extends MethodVisitor {
    private static final String[] EMPTY_STRING = new String[0];

    private final ChecksumClassFile checksumClassFile;
    private final ChecksumMethod checksumMethod;
    private final List<ChecksumInstruction> instructions;

    ChecksumMethodVisitor(int api, MethodVisitor mv, ChecksumClassFile checksumClassFile, ChecksumMethod checksumMethod) {
        super(api, mv);
        this.checksumClassFile = checksumClassFile;
        this.checksumMethod = checksumMethod;
        this.instructions = new ArrayList<ChecksumInstruction>();
    }

    /**
     * Visits a zero operand instruction.
     *
     * @param opcode the opcode of the instruction to be visited. This opcode is either NOP,
     * ACONST_NULL, ICONST_M1, ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5, LCONST_0,
     * LCONST_1, FCONST_0, FCONST_1, FCONST_2, DCONST_0, DCONST_1, IALOAD, LALOAD, FALOAD, DALOAD,
     * AALOAD, BALOAD, CALOAD, SALOAD, IASTORE, LASTORE, FASTORE, DASTORE, AASTORE, BASTORE,
     * CASTORE, SASTORE, POP, POP2, DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1, DUP2_X2, SWAP, IADD, LADD,
     * FADD, DADD, ISUB, LSUB, FSUB, DSUB, IMUL, LMUL, FMUL, DMUL, IDIV, LDIV, FDIV, DDIV, IREM,
     * LREM, FREM, DREM, INEG, LNEG, FNEG, DNEG, ISHL, LSHL, ISHR, LSHR, IUSHR, LUSHR, IAND, LAND,
     * IOR, LOR, IXOR, LXOR, I2L, I2F, I2D, L2I, L2F, L2D, F2I, F2L, F2D, D2I, D2L, D2F, I2B, I2C,
     * I2S, LCMP, FCMPL, FCMPG, DCMPL, DCMPG, IRETURN, LRETURN, FRETURN, DRETURN, ARETURN, RETURN,
     * ARRAYLENGTH, ATHROW, MONITORENTER, or MONITOREXIT.
     */

    @Override
    public void visitInsn(int opcode) {
        instructions.add(new ChecksumInstruction(opcode));
        super.visitInsn(opcode);
    }

    /**
     * Visits an instruction with a single int operand.
     *
     * @param opcode the opcode of the instruction to be visited. This opcode is either BIPUSH,
     * SIPUSH or NEWARRAY.
     * @param operand the operand of the instruction to be visited.<br> When opcode is BIPUSH,
     * operand value should be between Byte.MIN_VALUE and Byte.MAX_VALUE.<br> When opcode is SIPUSH,
     * operand value should be between Short.MIN_VALUE and Short.MAX_VALUE.<br> When opcode is
     * NEWARRAY, operand value should be one of {@link Opcodes#T_BOOLEAN}, {@link Opcodes#T_CHAR},
     * {@link Opcodes#T_FLOAT}, {@link Opcodes#T_DOUBLE}, {@link Opcodes#T_BYTE}, {@link
     * Opcodes#T_SHORT}, {@link Opcodes#T_INT} or {@link Opcodes#T_LONG}.
     */

    @Override
    public void visitIntInsn(int opcode, int operand) {
        instructions.add(new ChecksumInstruction(opcode, new String[] {Integer.toString(operand)}));
        super.visitIntInsn(opcode, operand);
    }

    /**
     * Visits a local variable instruction. A local variable instruction is an instruction that
     * loads or stores the value of a local variable.
     *
     * @param opcode the opcode of the local variable instruction to be visited. This opcode is
     * either ILOAD, LLOAD, FLOAD, DLOAD, ALOAD, ISTORE, LSTORE, FSTORE, DSTORE, ASTORE or RET.
     * @param var the operand of the instruction to be visited. This operand is the index of a local
     * variable.
     */

    @Override
    public void visitVarInsn(int opcode, int var) {
        instructions.add(new ChecksumInstruction(opcode, new String[] {Integer.toString(var)}));
        super.visitVarInsn(opcode, var);
    }

    /**
     * Visits a type instruction. A type instruction is an instruction that takes the internal name
     * of a class as parameter.
     *
     * @param opcode the opcode of the type instruction to be visited. This opcode is either NEW,
     * ANEWARRAY, CHECKCAST or INSTANCEOF.
     * @param type the operand of the instruction to be visited. This operand must be the internal
     * name of an object or array class (see {@link Type#getInternalName() getInternalName}).
     */

    @Override
    public void visitTypeInsn(int opcode, String type) {
        instructions.add(new ChecksumInstruction(opcode, new String[] {type}));
        super.visitTypeInsn(opcode, type);
    }

    /**
     * Visits a field instruction. A field instruction is an instruction that loads or stores the
     * value of a field of an object.
     *
     * @param opcode the opcode of the type instruction to be visited. This opcode is either
     * GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD.
     * @param owner the internal name of the field's owner class (see {@link Type#getInternalName()
     * getInternalName}).
     * @param name the field's name.
     * @param desc the field's descriptor (see {@link Type Type}).
     */

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        instructions.add(new ChecksumInstruction(opcode, new String[] {owner, name, desc}));
        super.visitFieldInsn(opcode, owner, name, desc);
    }

    /**
     * Visits a method instruction. A method instruction is an instruction that invokes a method.
     *
     * @param opcode the opcode of the type instruction to be visited. This opcode is either
     * INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or INVOKEINTERFACE.
     * @param owner the internal name of the method's owner class (see {@link Type#getInternalName()
     * getInternalName}).
     * @param name the method's name.
     * @param desc the method's descriptor (see {@link Type Type}).
     * @param itf if the method's owner class is an interface.
     */

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc,
            boolean itf) {
        instructions.add(new ChecksumInstruction(opcode, new String[] {owner, name, desc}));
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    /**
     * Visits an invokedynamic instruction.
     *
     * @param name the method's name.
     * @param desc the method's descriptor (see {@link Type Type}).
     * @param bsm the bootstrap method.
     * @param bsmArgs the bootstrap method constant arguments. Each argument must be an {@link
     * Integer}, {@link Float}, {@link Long}, {@link Double}, {@link String}, {@link Type} or {@link
     * Handle} value. This method is allowed to modify the content of the array so a caller should
     * expect that this array may change.
     */

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        StringBuilder builder = new StringBuilder(bsm.toString());
        for (Object arg : bsmArgs) {
            String argToString = createStringRepOfLDCParam(arg);
            if(argToString != null) {
                builder.append(argToString);
            }
        }
        instructions.add(new ChecksumInstruction(Opcodes.INVOKEDYNAMIC, new String[] {name, desc, builder.toString()}));
        super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }

    /**
     * Visits a jump instruction. A jump instruction is an instruction that may jump to another
     * instruction.
     *
     * @param opcode the opcode of the type instruction to be visited. This opcode is either IFEQ,
     * IFNE, IFLT, IFGE, IFGT, IFLE, IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT,
     * IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE, GOTO, JSR, IFNULL or IFNONNULL.
     * @param label the operand of the instruction to be visited. This operand is a label that
     * designates the instruction to which the jump instruction may jump.
     */

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        // Offset (Label) is not required
        instructions.add(new ChecksumInstruction(opcode));
        super.visitJumpInsn(opcode, label);
    }

    /**
     * Visits a LDC instruction. Note that new constant types may be added in future versions of the
     * Java Virtual Machine. To easily detect new constant types, implementations of this method
     * should check for unexpected constant types, like this:
     *
     * <pre>
     * if (cst instanceof Integer) {
     *     // ...
     * } else if (cst instanceof Float) {
     *     // ...
     * } else if (cst instanceof Long) {
     *     // ...
     * } else if (cst instanceof Double) {
     *     // ...
     * } else if (cst instanceof String) {
     *     // ...
     * } else if (cst instanceof Type) {
     *     int sort = ((Type) cst).getSort();
     *     if (sort == Type.OBJECT) {
     *         // ...
     *     } else if (sort == Type.ARRAY) {
     *         // ...
     *     } else if (sort == Type.METHOD) {
     *         // ...
     *     } else {
     *         // throw an exception
     *     }
     * } else if (cst instanceof Handle) {
     *     // ...
     * } else {
     *     // throw an exception
     * }
     * </pre>
     *
     * @param cst the constant to be loaded on the stack. This parameter must be a non null {@link
     * Integer}, a {@link Float}, a {@link Long}, a {@link Double}, a {@link String}, a {@link Type}
     * of OBJECT or ARRAY sort for <tt>.class</tt> constants, for classes whose version is 49.0, a
     * {@link Type} of METHOD sort or a {@link Handle} for MethodType and MethodHandle constants,
     * for classes whose version is 51.0.
     */

    @Override
    public void visitLdcInsn(Object cst) {
        String constantToString = createStringRepOfLDCParam(cst);
        instructions.add(
                new ChecksumInstruction(
                        Opcodes.LDC,
                        (constantToString == null) ? EMPTY_STRING : new String[] {constantToString}));
        super.visitLdcInsn(cst);
    }

    /**
     * Visits an IINC instruction.
     *
     * @param var index of the local variable to be incremented.
     * @param increment amount to increment the local variable by.
     */

    @Override
    public void visitIincInsn(int var, int increment) {

        instructions.add(new ChecksumInstruction(Opcodes.IINC,
                new String[] {Integer.toString(var), Integer.toString(increment)}));
        super.visitIincInsn(var, increment);
    }

    /**
     * Visits a MULTIANEWARRAY instruction.
     *
     * @param desc an array type descriptor (see {@link Type Type}).
     * @param dims number of dimensions of the array to allocate.
     */

    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
        // Number of array dimensions can be safely ignored
        // as this information is conveyed in the descriptor

        instructions.add(new ChecksumInstruction(Opcodes.MULTIANEWARRAY, new String[] {desc}));
        super.visitMultiANewArrayInsn(desc, dims);
    }

    @Override
    public void visitEnd() {
        checksumMethod.addAll(instructions);
        checksumClassFile.add(checksumMethod);
        super.visitEnd();
    }

    private String createStringRepOfLDCParam(Object cst) {
        if(cst == null) {
            return null;
        }
        if (cst instanceof Type) {
            return ((Type) cst).getInternalName();
        }
        if (cst instanceof Handle) {
            return cst.toString(); // Handle toString() is in internal format
        }
        return String.valueOf(cst);
    }

}
