package org.rampart.lang.impl.checksum;

import static org.objectweb.asm.Opcodes.ASM7;

import java.io.IOException;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.objectweb.asm.ClassReader;

/**
 * Utility class used to generate a Checksum value for a class or method
 */
public class ChecksumUtils {

    private ChecksumUtils() {}

    /**
     * Generate a checksum for the given class bytes. An optional writer could be passed in to debug
     * output from the Class file parsing.
     *
     * @param classBytes bytes to generate the checksum for
     * @param debug writer to print debug message to
     * @return String 40 character hex string representing the class
     */
    public static String generateChecksumForClassBytes(byte[] classBytes, Writer debug) {
        writeMessage(debug, "Parsing class bytes ...");
        String checksumClassFile = populateChecksumObjectGraph(classBytes).toString();
        writeMessage(debug, "ClassFile:\n" + checksumClassFile);
        String hash = generateChecksumFromByteArray(checksumClassFile.getBytes());
        writeMessage(debug, "Computed hash: " + hash);
        flushWriter(debug);
        return hash;
    }

    /**
     * Generate a checksum for the given class bytes.
     *
     * @param classBytes bytes to generate the checksum for
     * @return String 40 character hex string representing the class
     */
    public static String generateChecksumForClassBytes(byte[] classBytes) {
        return generateChecksumForClassBytes(classBytes, null);
    }

    /**
     * Generate a checksum for the given method signature.
     * @param classBytes bytes for the class containing the
     * @param methodSignature signature of the desired method (in internal format).
     * @return 40 character hex String representing the method or null if the method is not found
     */
    public static String generateChecksumForMethodSignature(byte[] classBytes, String methodSignature) {
        if (methodSignature == null || methodSignature.length() == 0) {
            throw new IllegalArgumentException("method signature missing");
        }
        ChecksumClassFile checksumClassFile = populateChecksumObjectGraph(classBytes);
        ChecksumMethod desiredMethod =
                getChecksumMethodFromClassMethods(checksumClassFile.getClassMethods(), methodSignature);
        if (desiredMethod == null) {
            throw new IllegalArgumentException("method: \"" + methodSignature + "\" was not found in the given class");
        }
        return generateChecksumFromByteArray(desiredMethod.toString().getBytes());
    }

    /**
     * Iterates through the given list of ChecksumMethod instances and returns
     * @param classMethods list of the ChecksumMethod instances of this class
     * @param signature method signature to search for.
     * @return ChecksumMethod instances which matches the given function signature
     * or null if no match is found.
     */
    private static ChecksumMethod getChecksumMethodFromClassMethods(List<ChecksumMethod> classMethods, String signature) {
        for (ChecksumMethod checksumMethod: classMethods) {
            if (checksumMethod.matchesSignature(signature)) {
                return checksumMethod;
            }
        }
        return null;
    }

    /**
     * Creates in-memory representation of a class by walking its byte code
     *
     * @param classBytes - byte array containing class bytecode
     * @return ChecksumClassFile - Object representation of a class
     */

    private static ChecksumClassFile populateChecksumObjectGraph(byte[] classBytes) {
        ChecksumClassVisitor classVisitor = new ChecksumClassVisitor(ASM7);
        new ClassReader(classBytes).accept(classVisitor, 0);
        return classVisitor.getChecksumClassFile();
    }

    /**
     * Creates a (160 bit) SHA-1 digest of the byte array
     * returns the result as a (40 character)hex string.
     *
     * @param classFileBytes byte array to create hex string from
     * @return HexString representation of the class
     */

    static String generateChecksumFromByteArray(byte[] classFileBytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(classFileBytes);
            byte[] res = md.digest();
            return byteArrayToHex(res);
        } catch (NoSuchAlgorithmException ex) {
            throw new AssertionError(ex);
        }
    }

    /**
     * Returns a hex string representation of a byte array
     *
     * @param bytes - byte array to be converted to a string
     * @return String representation of the byte array
     */

    private static String byteArrayToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2); // 1 byte = 2 hex chars
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    private static void writeMessage(Writer debug, String message) {
        if(debug != null) {
            try {
                debug.write("CHECKSUM - " + message + "\n");
            } catch (IOException e) {}
        }
    }

    private static void flushWriter(Writer debug) {
        if(debug != null) {
            try {
                debug.flush();
            } catch (IOException e) {}
        }
    }
}
