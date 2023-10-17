package org.rampart.lang.impl.patch.validators.signatures;

import org.rampart.lang.impl.core.InvalidRampartRuleException;

/**
 * Class to validate java object constructor signatures
 * e.g. com/foo/bar/Class.<init>()V
 * @since 1.4
 */
class ConstructorSignatureValidator extends MethodSignatureValidator {
    private static final String INIT_STRING = "init";

    private static final String ERROR_MESSAGE_OPEN_ANGLE_BRACKET_EXPECTED =
            "'" + OPEN_ANGLE_BRACKET + "' expected";

    private static final String ERROR_MESSAGE_CLOSE_ANGLE_BRACKET_EXPECTED =
            "'" + CLOSE_ANGLE_BRACKET + "' expected";

    private static final String ERROR_MESSAGE_CONSTRUCTOR_NAME_EXPECTED =
            "'" + INIT_STRING + "' expected";

    private static final String ERROR_MESSAGE_VOID_TYPE_EXPECTED =
            "'" + VOID_TYPE + "' expected. All constructors must specify a void return type";

    ConstructorSignatureValidator(String signature) {
        super(signature);
    }

    @Override
    protected void validateMemberName() throws InvalidRampartRuleException {
        if (charArray[index] != OPEN_ANGLE_BRACKET) {
            throw new InvalidRampartRuleException(
                    enhanceMessage(ERROR_MESSAGE_OPEN_ANGLE_BRACKET_EXPECTED));
        }
        nextChar();
        int startChar = index;

        validateConstructorName();
        String memberName = signature.substring(startChar, index);

        checkConstructorNameMatches(memberName);

        if (charArray[index] != CLOSE_ANGLE_BRACKET) {
            throw new InvalidRampartRuleException(
                    enhanceMessage(ERROR_MESSAGE_CLOSE_ANGLE_BRACKET_EXPECTED));
        }
        nextChar();

        if (index == charArray.length) {
            throw new InvalidRampartRuleException(enhanceMessage(ERROR_MESSAGE_INCOMPLETE_SIGNATURE));
        }
    }

    /**
     * Validate constructor name.
     *
     * When an unconventional, but legal character, as denoted in JVMS(S4.2.2), is
     * encountered, it will be allowed.
     * @throws InvalidRampartRuleException if an illegal character is encountered.
     */
    private void validateConstructorName() throws InvalidRampartRuleException {
        validateJavaIdentifier();
        while (index < charArray.length) {
            switch (charArray[index]) {
                case SLASH:
                case DOT:
                case OPEN_SQUARE_BRACKET:
                case SEMI_COLON:
                case OPEN_ANGLE_BRACKET: // the expected one has already been validated
                    throw new InvalidRampartRuleException(
                            enhanceMessage(ERROR_MESSAGE_UNEXPECTED_CHARACTER));
                case CLOSE_ANGLE_BRACKET: // for a constructor, the next char can be >
                    return;
            }
            index++;
            validateJavaIdentifierEnd();
        }
    }

    /**
     * All constructors have a return type of void
     * @throws InvalidRampartRuleException when return type is not void (V)
     */
    @Override
    protected void validateReturnType() throws InvalidRampartRuleException {
        if (charArray[index] != VOID_TYPE) {
            throw new InvalidRampartRuleException(enhanceMessage(ERROR_MESSAGE_VOID_TYPE_EXPECTED));
        }
        index++;
    }

    protected void checkConstructorNameMatches(String constructorName) throws InvalidRampartRuleException {
        if (!INIT_STRING.startsWith(constructorName)) {
            throw new InvalidRampartRuleException(enhanceMessage(ERROR_MESSAGE_CONSTRUCTOR_NAME_EXPECTED));
        }
    }
}
