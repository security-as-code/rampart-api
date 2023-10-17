package org.rampart.lang.impl.patch.validators.v2;

import static org.rampart.lang.api.constants.RampartPatchConstants.*;

import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.rampart.lang.api.RampartList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.rampart.lang.api.patch.RampartPatchType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

public class RampartLocationValidator2_1Test {

    private static final String[] VALID_CLASS_SIGNATURES = {
            "c",
            "com",
            "com/blah",
            "com/blah/SomeClass",
            "com/blah/SomeClass$InnerClass",
            "com/blah/SomeClass$1",
            "com/blah/SomeClass$",
            "SomeClass",
            "SomeClass$InnerClass"};

    private static final String[] INVALID_CLASS_SIGNATURES = {
            "",
            "com/",
            "com/blah/",
            "com.blah.SomeClass"};

    private static final String[] VALID_FIELD_SIGNATURES = {
            "com/blah/SomeClass.field",
            "com/blah/SomeClass.f",
            "com/blah/SomeClass.fie_ld",
            "com/blah/SomeClass._field",
            "com/blah/SomeClass$OtherClass.field"};

    private static final String[] INVALID_FIELD_SIGNATURES = {
            "com.blah.SomeClass.field",
            "com/blah/SomeClass.field.",
            "com/blah/SomeClass.fi*d.I",
            "com/blah/SomeClass.field(J",
            "com/blah/SomeClass<field.Ljava/lang/String;",
            "com/blah/SomeClass.field.Ljava/lang/String",
            "com/blah/SomeClass.field.java/lang/String;",
            "com/blah/SomeClass.field.x",
            "com/blah/SomeClass.field.Ljava/lang/String;x",
            "com/blah/SomeClass.field.[[[[[I;"};


    private static final String[] VALID_METHOD_SIGNATURES = {
            "com/foo/bar/Class.main([Ljava/lang/String;)V",
            "com/foo/bar/SomeClass.method()V",
            "com/foo/bar/SomeClass.method(IJB)V",
            "com/foo/bar/SomeClass.__method()V",
            "com/foo/bar/SomeClass.method(ILjava/lang/Class;)V",
            "com/foo/bar/SomeClass.method()Ljava/io/File;",
            "com/foo/bar/SomeClass.method(Ljava/lang/String;ILjava/lang/Class;[[B)Ljava/io/File;",
            "Class.main([Ljava/lang/String;)V"};

    private static final String[] INVALID_METHOD_SIGNATURES = {
            "com.foo.bar.Class.main([Ljava/lang/String;)V",
            "com/foo/bar/Class.main(A)V",
            "com/foo/bar/Class.main([Ljava/lang/String;)",
            "com/blah/SomeClass.method.",
            "com/blah/SomeClass.me*d.()V",
            "com/blah/SomeClass.method.()V",
            "com/blah/SomeClass<method()V",
            "com/blah/SomeClass.method(Ljava/lang/String)V",
            "com/blah/SomeClass.method(java/lang/String;)V",
            "com/blah/SomeClass.methodLjava/lang/String;)V",
            "com/blah/SomeClass.method(Ljava/lang/String;X)V",
            "com/blah/SomeClass.method(Ljava/lang/String;)Vx",
            "com/blah/SomeClass.method([[[[[Ijava/lang/String;)Ljava/lang/String;",
            "com/blah/SomeClass.method([[[[[ILjava/lang/String;)java/lang/String;"};


    private static final String[] VALID_CONSTRUCTOR_SIGNATURES = {
            "com/foo/bar/Class.<init>([Ljava/lang/String;)V",
            "com/foo/bar/Class.<clinit>()V",
            "com/foo/bar/Class.init([Ljava/lang/String;)V",
            "com/blah/SomeClass.<init>()V"};

    private static final String[] INVALID_CONSTRUCTOR_SIGNATURES = {
            "com/blah/SomeClass.<init>",
            "com/blah/SomeClass.<*>()V",
            "com/blah/SomeClass.<init>.()V",
            "com/blah/SomeClass.<init()V",
            "com/blah/SomeClass.<jnit()V",
            "com/blah/SomeClass.<init>()I",
            "com/blah/SomeClass.<jnit>()V",
            "com/blah/SomeClass<init>()V",
            "com/blah/SomeClass.<init>(Ljava/lang/String)V",
            "com/blah/SomeClass.<init>(java/lang/String;)V",
            "com/blah/SomeClass.<init>Ljava/lang/String;)V",
            "com/blah/SomeClass.<init>(Ljava/lang/String;X)V",
            "com/blah/SomeClass.<init>(Ljava/lang/String;)Vx"};

    private Map<String, RampartList> visitorSymbolTable;
    RampartLocationValidator2_1 validator;

    @BeforeEach
    public void setUp() {
        visitorSymbolTable = new HashMap<>();
        validator = new RampartLocationValidator2_1(visitorSymbolTable);
    }

    @Test
    public void callLocationWithValidMethodSignatures() {
        assertAll(Arrays.stream(VALID_METHOD_SIGNATURES)
                .map(validMethodSignature ->
                () -> assertAll("method signature [" + validMethodSignature + "] must not throw exceptions",
                        () -> {
                    visitorSymbolTable.put(CALL_KEY.toString(), newRampartList(
                            newRampartString(validMethodSignature)));
                    validator.validateLocationSpecifier(RampartPatchType.CALL, JAVA_KEY);
                })));
    }

    @Test
    public void callLocationWithInvalidMethodSignatures() {
        assertAll(Arrays.stream(INVALID_METHOD_SIGNATURES)
                .map(invalidMethodSignature->
                () -> assertThrows(InvalidRampartRuleException.class,
                    () -> {
                        visitorSymbolTable.put(CALL_KEY.toString(), newRampartList(
                                newRampartString(invalidMethodSignature)));
                        validator.validateLocationSpecifier(RampartPatchType.CALL, JAVA_KEY);
                    },
                    "assert for invalid method signature [" + invalidMethodSignature + "] failed")));
    }

    @Test
    public void callLocationWithValidConstructorSignatures() {
        assertAll(Arrays.stream(VALID_METHOD_SIGNATURES)
                .map(validConstructorSignature ->
                () -> assertAll("constructor signature [" + validConstructorSignature + "] must not throw exceptions",
                        () -> {
                    visitorSymbolTable.put(CALL_KEY.toString(), newRampartList(
                            newRampartString(validConstructorSignature)));
                    validator.validateLocationSpecifier(RampartPatchType.CALL, JAVA_KEY);
                })));
    }

    @Test
    public void callLocationWithInvalidConstructorSignatures() {
        assertAll(Arrays.stream(INVALID_CONSTRUCTOR_SIGNATURES)
                .map(invalidConstructorSignature ->
                () -> assertThrows(InvalidRampartRuleException.class,
                    () -> {
                        visitorSymbolTable.put(CALL_KEY.toString(), newRampartList(
                                newRampartString(invalidConstructorSignature)));
                        validator.validateLocationSpecifier(RampartPatchType.CALL, JAVA_KEY);
                    },
                    "assert for invalid constructor signature [" + invalidConstructorSignature + "] failed")));
    }

    @Test
    public void callLocationWithValidFieldSignatures() {
        assertAll(Arrays.stream(VALID_FIELD_SIGNATURES)
                .map(validFieldSignature ->
                () -> assertThrows(InvalidRampartRuleException.class,
                    () -> {
                        visitorSymbolTable.put(CALL_KEY.toString(), newRampartList(
                                newRampartString(validFieldSignature)));
                        validator.validateLocationSpecifier(RampartPatchType.CALL, JAVA_KEY);
                    },
                    "assert for field signature [" + validFieldSignature + "] failed")));
    }

    @Test
    public void callLocationWithValidClassSignatures() {
        assertAll(Arrays.stream(VALID_CLASS_SIGNATURES)
                .map(validClassSignature ->
                () -> assertThrows(InvalidRampartRuleException.class,
                    () -> {
                        visitorSymbolTable.put(CALL_KEY.toString(), newRampartList(
                                newRampartString(validClassSignature)));
                        validator.validateLocationSpecifier(RampartPatchType.CALL, JAVA_KEY);
                    },
                    "assert for class signature [" + validClassSignature + "] failed")));
    }

    @Test
    public void readLocationWithValidFieldSignatures() {
        assertAll(Arrays.stream(VALID_FIELD_SIGNATURES)
                .map(validFieldSignature ->
                () -> assertAll("field signature [" + validFieldSignature + "] must not throw exceptions",
                        () -> {
                    visitorSymbolTable.put(READ_KEY.toString(), newRampartList(
                            newRampartString(validFieldSignature)));
                    validator.validateLocationSpecifier(RampartPatchType.READ, JAVA_KEY);
                })));
    }

    @Test
    public void readLocationWithInvalidFieldSignatures() {
        assertAll(Arrays.stream(INVALID_FIELD_SIGNATURES)
                .map(invalidFieldSignature ->
                () -> assertThrows(InvalidRampartRuleException.class,
                    () -> {
                        visitorSymbolTable.put(READ_KEY.toString(), newRampartList(
                                newRampartString(invalidFieldSignature)));
                        validator.validateLocationSpecifier(RampartPatchType.READ, JAVA_KEY);
                    },
                    "assert for invalid field signature [" + invalidFieldSignature + "] failed")));
    }

    @Test
    public void readLocationWithValidMethodSignatures() {
        assertAll(Arrays.stream(VALID_METHOD_SIGNATURES)
                .map(validMethodSignature ->
                () -> assertThrows(InvalidRampartRuleException.class,
                    () -> {
                        visitorSymbolTable.put(READ_KEY.toString(), newRampartList(
                                newRampartString(validMethodSignature)));
                        validator.validateLocationSpecifier(RampartPatchType.READ, JAVA_KEY);
                    },
                    "assert for method signature [" + validMethodSignature + "] failed")));
    }

    @Test
    public void readLocationWithValidConstructorSignatures() {
        assertAll(Arrays.stream(VALID_CONSTRUCTOR_SIGNATURES)
                .map(validConstructorSignature ->
                () -> assertThrows(InvalidRampartRuleException.class,
                    () -> {
                        visitorSymbolTable.put(READ_KEY.toString(), newRampartList(
                                newRampartString(validConstructorSignature)));
                        validator.validateLocationSpecifier(RampartPatchType.READ, JAVA_KEY);
                    },
                    "assert for constructor signature [" + validConstructorSignature + "] failed")));
    }

    @Test
    public void readLocationWithValidClassSignatures() {
        assertAll(Arrays.stream(VALID_CLASS_SIGNATURES)
                .map(validClassSignature ->
                () -> assertThrows(InvalidRampartRuleException.class,
                    () -> {
                        visitorSymbolTable.put(READ_KEY.toString(), newRampartList(
                                newRampartString(validClassSignature)));
                        validator.validateLocationSpecifier(RampartPatchType.READ, JAVA_KEY);
                    },
                    "assert for class signature [" + validClassSignature + "] failed")));
    }

    @Test
    public void writeLocationValidFieldSignatures() {
        assertAll(Arrays.stream(VALID_FIELD_SIGNATURES)
                .map(validFieldSignature ->
                () -> assertAll("field signature [" + validFieldSignature + "] must not throw exceptions",
                        () -> {
                    visitorSymbolTable.put(WRITE_KEY.toString(), newRampartList(
                            newRampartString(validFieldSignature)));
                    validator.validateLocationSpecifier(RampartPatchType.WRITE, JAVA_KEY);
                })));
    }

    @Test
    public void writeLocationInvalidFieldSignatures() {
        assertAll(Arrays.stream(INVALID_FIELD_SIGNATURES)
                .map(invalidFieldSignature ->
                () -> assertThrows(InvalidRampartRuleException.class,
                    () -> {
                        visitorSymbolTable.put(WRITE_KEY.toString(), newRampartList(
                                newRampartString(invalidFieldSignature)));
                        validator.validateLocationSpecifier(RampartPatchType.WRITE, JAVA_KEY);
                    },
                    "assert for invalid field signature [" + invalidFieldSignature + "] failed")));
    }

    @Test
    public void writeLocationWithValidMethodSignatures() {
        assertAll(Arrays.stream(VALID_METHOD_SIGNATURES)
                .map(validMethodSignature ->
                () -> assertThrows(InvalidRampartRuleException.class,
                    () -> {
                        visitorSymbolTable.put(WRITE_KEY.toString(), newRampartList(
                                newRampartString(validMethodSignature)));
                        validator.validateLocationSpecifier(RampartPatchType.WRITE, JAVA_KEY);
                    },
                    "assert for method signature [" + validMethodSignature + "] failed")));
    }

    @Test
    public void writeLocationWithValidConstructorSignatures() {
        assertAll(Arrays.stream(VALID_CONSTRUCTOR_SIGNATURES)
                .map(validConstructorSignature ->
                () -> assertThrows(InvalidRampartRuleException.class,
                    () -> {
                        visitorSymbolTable.put(WRITE_KEY.toString(), newRampartList(
                                newRampartString(validConstructorSignature)));
                        validator.validateLocationSpecifier(RampartPatchType.WRITE, JAVA_KEY);
                    },
                    "assert for constructor signature [" + validConstructorSignature + "] failed")));
    }

    @Test
    public void writeLocationWithValidClassSignatures() {
        assertAll(Arrays.stream(VALID_CLASS_SIGNATURES)
                .map(validClassSignature ->
                () -> assertThrows(InvalidRampartRuleException.class,
                    () -> {
                        visitorSymbolTable.put(WRITE_KEY.toString(), newRampartList(
                                newRampartString(validClassSignature)));
                        validator.validateLocationSpecifier(RampartPatchType.WRITE, JAVA_KEY);
                    },
                    "assert for class signature [" + validClassSignature + "] failed")));
    }

    @Test
    public void errorLocationWithValidClassSignatures() {
        assertAll(Arrays.stream(VALID_CLASS_SIGNATURES)
                .map(validClassSignature ->
                () -> assertAll("class signature [" + validClassSignature + "] must not throw exceptions",
                        () -> {
                    visitorSymbolTable.put(ERROR_KEY.toString(), newRampartList(
                            newRampartString(validClassSignature)));
                    validator.validateLocationSpecifier(RampartPatchType.ERROR, JAVA_KEY);
                })));
    }

    @Test
    public void errorLocationWithInvalidClassSignatures() {
        assertAll(Arrays.stream(INVALID_CLASS_SIGNATURES)
                .map(invalidClassSignature ->
                () -> assertThrows(InvalidRampartRuleException.class,
                    () -> {
                        visitorSymbolTable.put(ERROR_KEY.toString(), newRampartList(
                                newRampartString(invalidClassSignature)));
                        validator.validateLocationSpecifier(RampartPatchType.ERROR, JAVA_KEY);
                    },
                    "assert for invalid class signature [" + invalidClassSignature + "] failed")));
    }

    @Test
    public void errorLocationWithValidMethodSignatures() {
        assertAll(Arrays.stream(VALID_METHOD_SIGNATURES)
                .map(validMethodSignature ->
                () -> assertThrows(InvalidRampartRuleException.class,
                    () -> {
                        visitorSymbolTable.put(ERROR_KEY.toString(), newRampartList(
                                newRampartString(validMethodSignature)));
                        validator.validateLocationSpecifier(RampartPatchType.ERROR, JAVA_KEY);
                    },
                    "assert for method signature [" + validMethodSignature + "] failed")));
    }

    @Test
    public void errorLocationWithValidConstructorSignatures() {
        assertAll(Arrays.stream(VALID_CONSTRUCTOR_SIGNATURES)
                .map(validConstructorSignature ->
                () -> assertThrows(InvalidRampartRuleException.class,
                    () -> {
                        visitorSymbolTable.put(ERROR_KEY.toString(), newRampartList(
                                newRampartString(validConstructorSignature)));
                        validator.validateLocationSpecifier(RampartPatchType.ERROR, JAVA_KEY);
                    },
                    "assert for constructor signature [" + validConstructorSignature + "] failed")));
    }

    @Test
    public void errorLocationWithValidFieldSignatures() {
        assertAll(Arrays.stream(VALID_FIELD_SIGNATURES)
                .map(validFieldSignature ->
                () -> assertThrows(InvalidRampartRuleException.class,
                    () -> {
                        visitorSymbolTable.put(ERROR_KEY.toString(), newRampartList(
                                newRampartString(validFieldSignature)));
                        validator.validateLocationSpecifier(RampartPatchType.ERROR, JAVA_KEY);
                    },
                    "assert for field signature [" + validFieldSignature + "] failed")));
    }

    @Test
    public void callLocationWithValidMethodSignaturesInCsharp() {
        String validMethodSignature =
                "instance void [System.Web]System.Web.UI.Page::ProcessRequest(class [System.Web]System.Web.HttpContext)";
        assertAll("method signature [" + validMethodSignature + "] must not throw exceptions", () -> {
            visitorSymbolTable.put(CALL_KEY.toString(), newRampartList(newRampartString(validMethodSignature)));
            validator.validateLocationSpecifier(RampartPatchType.CALL, CSHARP_KEY);
        });
    }
}
