package org.rampart.lang.impl.checksum;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;

public class ChecksumUtilsTest {

    @Test
    public void generateChecksumForClassBytesValid() throws Exception {
        ChecksumUtils.generateChecksumForClassBytes(new ClassReader("java/lang/String").b);
    }

    @Test
    public void generateChecksumForMethodValid() throws Exception {
        ChecksumUtils.generateChecksumForMethodSignature(
                new ClassReader("java/lang/String").b, "indexOf(Ljava/lang/String;)I");
    }

    @Test
    public void generateChecksumForClassBytes_randomBytesForClass() {
        byte [] invalidClassBytes = new byte[10000];
        Arrays.fill(invalidClassBytes, (byte)1);

        assertThrows(IllegalArgumentException.class,
                () -> ChecksumUtils.generateChecksumForClassBytes(invalidClassBytes));
    }

    @Test
    public void generateChecksumForMethod_methodNotInClass() {
        String invalidMethodSignature = "foo(Ljava/lang/String;)I";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> ChecksumUtils.generateChecksumForMethodSignature(
                new ClassReader("java/lang/String").b, invalidMethodSignature));

        assertThat(thrown.getMessage(),
                equalTo("method: \"" + invalidMethodSignature + "\" was not found in the given class"));
    }

    @Test
    public void generateChecksumForMethod_passedNullForMethodSignature() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> ChecksumUtils.generateChecksumForMethodSignature(
                new ClassReader("java/lang/String").b, null));

        assertThat(thrown.getMessage(), equalTo("method signature missing"));
    }

    @Test
    public void generateChecksumForMethod_passedEmptyStringForMethodSignature() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> ChecksumUtils.generateChecksumForMethodSignature(new ClassReader("java/lang/String").b, ""));

        assertThat(thrown.getMessage(), equalTo("method signature missing"));
    }

    @Test
    public void validateMessageDigestForNoBytes() {
        assertThat(ChecksumUtils.generateChecksumFromByteArray(new byte[]{}),
                equalTo("da39a3ee5e6b4b0d3255bfef95601890afd80709"));
    }

    @Test
    public void validateMessageDigestForCafe() {
        assertThat(ChecksumUtils.generateChecksumFromByteArray(new byte[]{13,10,15,14}),
                equalTo("0de16ace8c90a5e2bde4cb49441e63232e35e074"));
    }

    @Test
    public void validateMessageDigestForCafeBabe() {
        assertThat(ChecksumUtils.generateChecksumFromByteArray(new byte[]{13,10,15,14, 12,10,12,14}),
                equalTo("2a4caa984dc6697fbde6dfebb4f2e79fc602b9b9"));
    }

}
