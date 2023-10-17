package org.rampart.lang.impl.patch.validators.v2;

import static org.rampart.lang.api.constants.RampartPatchConstants.*;

import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

public class RampartFunctionValidator2_1Test {
    @Test
    public void validFunctionSignatureIsValidatedSuccessfully() throws InvalidRampartRuleException {
        new RampartFunctionValidator2_1(newRampartList(newRampartString("com/foo/bar/Class.main([Ljava/lang/String;)V")))
                .validateFunctionString(JAVA_KEY);
    }

    @Test
    public void validConstructorSignatureIsValidatedSuccessfully() throws InvalidRampartRuleException {
        new RampartFunctionValidator2_1(newRampartList(newRampartString("com/foo/bar/Class.<init>([Ljava/lang/String;)V")))
                .validateFunctionString(JAVA_KEY);
    }

    @Test
    public void validStaticConstructorSignatureIsValidatedSuccessfully() throws InvalidRampartRuleException {
        new RampartFunctionValidator2_1(newRampartList(newRampartString("com/foo/bar/Class.<clinit>()V")))
                .validateFunctionString(JAVA_KEY);
    }

    @Test
    public void classWithoutPackageIsValidatedSuccessfully() throws InvalidRampartRuleException {
        new RampartFunctionValidator2_1(newRampartList(newRampartString("Class.main([Ljava/lang/String;)V")))
                .validateFunctionString(JAVA_KEY);
    }

    @Test
    public void normalMethodNamedInitIsValidatedSuccessfully() throws InvalidRampartRuleException {
        new RampartFunctionValidator2_1(newRampartList(newRampartString("com/foo/bar/Class.init([Ljava/lang/String;)V")))
                .validateFunctionString(JAVA_KEY);
    }

    @Test
    public void constructorSignatureWithInvalidReturnTypeThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartFunctionValidator2_1(newRampartList(
                        newRampartString("com/foo/bar/Class.<init>(Ljava/lang/String;II)I")))
                    .validateFunctionString(JAVA_KEY));

        assertThat(thrown.getMessage(), startsWith(
                "'V' expected. All constructors must specify a void return type\ncom/foo/bar/Class.<init>.(Ljava/lang/String;II)I\n"));
    }

    @Test
    public void staticConstructorSignatureWithInvalidReturnTypeThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartFunctionValidator2_1(newRampartList(
                        newRampartString("com/foo/bar/Class.<clinit>()I")))
                    .validateFunctionString(JAVA_KEY));

        assertThat(thrown.getMessage(), startsWith(
                "'V' expected. All constructors must specify a void return type\ncom/foo/bar/Class.<clinit>.()I"));
    }

    @Test
    public void staticConstructorSignatureWithParametersThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartFunctionValidator2_1(newRampartList(
                        newRampartString("com/foo/bar/Class.<clinit>(Ljava/lang/String;II)V")))
                  .validateFunctionString(JAVA_KEY));

        assertThat(thrown.getMessage(), equalTo("clinit cannot have any parameters"));
    }

    @Test
    public void objectParameterWithLPrefixMissingThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartFunctionValidator2_1(newRampartList(
                        newRampartString("com/foo/bar/Class.main(java/lang/String;)V")))
                    .validateFunctionString(JAVA_KEY));

        assertThat(thrown.getMessage(), startsWith("invalid type detected"));
    }

    @Test
    public void invalidPrimitiveTypeParameterThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () ->
            // A is not a valid java primitive
            new RampartFunctionValidator2_1(newRampartList(newRampartString("com/foo/bar/Class.main(A)V")))
                    .validateFunctionString(JAVA_KEY)
        );

        assertThat(thrown.getMessage(), startsWith("invalid type detected"));
    }

    @Test
    public void objectParameterWithMissingSemiColonThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartFunctionValidator2_1(newRampartList(
                        newRampartString("com/foo/bar/Class.main(Ljava/lang/String)V")))
                    .validateFunctionString(JAVA_KEY));

        assertThat(thrown.getMessage(), startsWith("incomplete signature detected"));
    }

    @Test
    public void signatureWithMissingReturnTypeThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartFunctionValidator2_1(newRampartList(
                        newRampartString("com/foo/bar/Class.main([Ljava/lang/String;)")))
                    .validateFunctionString(JAVA_KEY));

        assertThat(thrown.getMessage(), startsWith("incomplete signature detected"));
    }

    @Test
    public void emptyStringThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> new RampartFunctionValidator2_1(newRampartList(newRampartString(""))).validateFunctionString(JAVA_KEY));

        assertThat(thrown.getMessage(), equalTo("\"" + FUNCTION_KEY + "\" declaration is missing"));
    }

    @Test
    public void nullStringThrowsException() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class, () -> new RampartFunctionValidator2_1(RampartList.EMPTY).validateFunctionString(JAVA_KEY));

        assertThat(thrown.getMessage(), equalTo("\"" + FUNCTION_KEY + "\" declaration is missing"));
    }

    @Test
    public void csharpSignatureIsValid() throws InvalidRampartRuleException {
        new RampartFunctionValidator2_1(newRampartList(newRampartString(
                "instance void [System.Web]System.Web.UI.Page::ProcessRequest(class [System.Web]System.Web.HttpContext)")))
                .validateFunctionString(CSHARP_KEY);
    }
}
