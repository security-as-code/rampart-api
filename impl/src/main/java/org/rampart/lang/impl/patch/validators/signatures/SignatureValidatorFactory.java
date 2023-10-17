package org.rampart.lang.impl.patch.validators.signatures;

import org.rampart.lang.api.patch.RampartPatchType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

/**
 * Factory class to create validators for java internal signatures
 * @since 1.4
 */
public class SignatureValidatorFactory {
    private static final String CONSTRUCTOR_START = "<";
    private static final String OPENING_PAREN = "(";
    private static final String DOT = ".";
    private static final String CLINIT_STRING = "clinit";

    private SignatureValidatorFactory() {}

    private static boolean canDeclareFieldSignature(RampartPatchType locationType) {
        return locationType == RampartPatchType.READ
                || locationType == RampartPatchType.READSITE
                || locationType == RampartPatchType.READRETURN
                || locationType == RampartPatchType.WRITE
                || locationType == RampartPatchType.WRITESITE
                || locationType == RampartPatchType.WRITERETURN;
    }

    private static boolean canDeclareMethodSignature(RampartPatchType locationType) {
        return locationType == RampartPatchType.CALL
                || locationType == RampartPatchType.CALLSITE
                || locationType == RampartPatchType.CALLRETURN;
    }

    private static boolean canDeclareClassSignature(RampartPatchType locationType) {
        return locationType == RampartPatchType.ERROR;
    }

    public static SignatureValidator createSignatureValidator(String signature, RampartPatchType locationType)
            throws InvalidRampartRuleException {
        if(signature.length() == 0) {
            throw new InvalidRampartRuleException("signature \"" + signature + "\" cannot be empty");
        }
        if (isClassSignature(signature)) {
            if (!canDeclareClassSignature(locationType)) {
                throw new InvalidRampartRuleException(
                        "\"" + locationType + "\" location specifier cannot declare a class signature");
            }
            return new ClassSignatureValidator(signature);
        }
        if (isFieldSignature(signature)) {
            if (!canDeclareFieldSignature(locationType)) {
                throw new InvalidRampartRuleException(
                        "\"" + locationType + "\" location specifier cannot declare a field signature");
            }
            return new FieldSignatureValidator(signature);
        }
        if (!canDeclareMethodSignature(locationType)) {
            throw new InvalidRampartRuleException(
                    "\"" + locationType + "\" location specifier cannot declare a method signature");
        }
        String methodSignatureWithDot;
        try {
            methodSignatureWithDot = insertDotBeforeLastOpeningParenthesis(signature);
        } catch (IllegalArgumentException iae) {
            throw new InvalidRampartRuleException(
                    "method signature: \"" + signature + "\" does not contain a parenthesis");
        }

        if (signature.contains(CONSTRUCTOR_START)) {
            if (signature.contains(CLINIT_STRING)) {
                return new StaticConstructorSignatureValidator(methodSignatureWithDot);
            }
            return new ConstructorSignatureValidator(methodSignatureWithDot);
        }
        return new MethodSignatureValidator(methodSignatureWithDot);
    }

    private static boolean isClassSignature(String signature) {
        // A class signature is simply in the form of xxx/yyy/zzz/Class
        return !signature.contains(OPENING_PAREN) && !signature.contains(DOT);
    }

    private static boolean isFieldSignature(String signature) {
        // A field signature is in the form xxx/yyy/zzz/Class.field
        return !signature.contains(OPENING_PAREN);
    }

    /**
     * In order to support the parsing of dynamically generated classes (which don't have the same
     * restrictions placed upon their naming as classes which are compiled with javac) we must
     * manually designate the end of the method name with a dot.
     *
     * @return String signature with a dot placed before the last '(' character
     * @throws IllegalArgumentException if the string signature does not contain a '(' character
     */
    private static String insertDotBeforeLastOpeningParenthesis(String functionString) {
        if (!functionString.contains("(")) {
            throw new IllegalArgumentException();
        }
        StringBuilder builder = new StringBuilder(functionString);
        builder.insert(functionString.lastIndexOf('('), '.');
        return builder.toString();
    }
}
