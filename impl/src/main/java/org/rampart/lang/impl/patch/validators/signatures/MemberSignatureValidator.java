package org.rampart.lang.impl.patch.validators.signatures;

import org.rampart.lang.impl.core.InvalidRampartRuleException;

abstract class MemberSignatureValidator extends SignatureValidator {
    private static final char OBJECT_START_CHAR = 'L';
    private static final String ERROR_MESSAGE_DOT_EXPECTED = "'" + DOT + "' expected";
    private static final String ERROR_MESSAGE_OBJECT_START_CHAR_EXPECTED =
            "'" + OBJECT_START_CHAR + "' expected";
    private static final String ERROR_MESSAGE_INVALID_TYPE = "invalid type detected";
    private static final String ERROR_MESSAGE_SEMI_COLON_EXPECTED = "'" + SEMI_COLON + "' expected";

    static final char OPEN_ANGLE_BRACKET = '<';
    static final char CLOSE_ANGLE_BRACKET = '>';
    static final char VOID_TYPE = 'V';

    MemberSignatureValidator(String signature) {
        super(signature);
    }

    @Override
    public void validate() throws InvalidRampartRuleException {
        // 1. validate the class part, relevant for all rule types
        validateFullyQualifiedClassName(DOT);
        checkForValidParsingSeparator();
        validateMemberSignature();
    }

    private void validateFullyQualifiedClassName(char endDelimiter) throws InvalidRampartRuleException {
        // The following call will throw if the first character is illegal.
        // For the remaining characters, it will check if they are valid
        // JavaIdentifierPart characters. If an invalid, but internally legal,
        // character is encountered, it will be vetted by the loop below.
        // In this case, a FQCN with '/' separators is expected.
        // When a '/' is encountered, it is skipped and validateJavaIdentifier()
        // is called again because the next identifier of the FQCN is expected.
        // If the FQCN is part of an object type of the form "Laaa/bbb/Ccc;",
        // the end delimiter will be ';'
        // If it is not part of an object type, the end delimiter will be '.'
        validateJavaIdentifier();
        while (index < charArray.length) {
            switch (charArray[index]) {
                case DOT: // separates class and member name
                case SEMI_COLON: // delimits ObjectType
                    if (charArray[index] == endDelimiter) {
                        return;
                    }
                case OPEN_SQUARE_BRACKET:
                    throw new InvalidRampartRuleException(
                            enhanceMessage(ERROR_MESSAGE_UNEXPECTED_CHARACTER));
                case SLASH: // package-class separator
                    nextChar();
                    validateJavaIdentifier();
                    continue;
            }
            index++;
            validateJavaIdentifierEnd();
        }
        throw new InvalidRampartRuleException(enhanceMessage(ERROR_MESSAGE_INCOMPLETE_SIGNATURE));
    }

    protected void checkForValidParsingSeparator() throws InvalidRampartRuleException {
        if (charArray[index] != DOT) {
            throw new InvalidRampartRuleException(enhanceMessage(ERROR_MESSAGE_DOT_EXPECTED));
        }
        nextChar();
    }

    private void validateArrayElements() throws InvalidRampartRuleException {
        while (charArray[index] == OPEN_SQUARE_BRACKET) {
            nextChar();
        }
    }

    private void validatePrimitiveType() throws InvalidRampartRuleException {
        switch (charArray[index]) {
            case 'B':
            case 'C':
            case 'D':
            case 'F':
            case 'I':
            case 'J':
            case 'S':
            case 'Z':
                index++;
                return;
        }
        throw new InvalidRampartRuleException(enhanceMessage(ERROR_MESSAGE_INVALID_TYPE));
    }

    private void validateObjectType() throws InvalidRampartRuleException {
        if (charArray[index] != OBJECT_START_CHAR) {
            // This shouldn't happen as this method is only called if the above
            // condition is true. However, as a stand-alone method, it's better
            // to check the character anyway.
            throw new InvalidRampartRuleException(
                    enhanceMessage(ERROR_MESSAGE_OBJECT_START_CHAR_EXPECTED));
        }
        nextChar();
        validateFullyQualifiedClassName(SEMI_COLON);
        if (index == charArray.length) {
            throw new InvalidRampartRuleException(enhanceMessage(ERROR_MESSAGE_INCOMPLETE_SIGNATURE));
        }
        if (charArray[index] != SEMI_COLON) {
            throw new InvalidRampartRuleException(enhanceMessage(ERROR_MESSAGE_SEMI_COLON_EXPECTED));
        }
        index++;
    }

    void validateType() throws InvalidRampartRuleException {
        // skip over '[' elements
        validateArrayElements();
        if (charArray[index] != OBJECT_START_CHAR) {
            validatePrimitiveType();
            return;
        }
        validateObjectType();
    }

    /**
     * Validate member name.
     *
     * When an unconventional, but legal character, as denoted in JVMS(S4.2.2), is
     * encountered, it will be allowed with a warning.
     *
     * @throws InvalidRampartRuleException if an illegal character is encountered.
     */
    protected void validateMemberName() throws InvalidRampartRuleException {
        validateJavaIdentifier();
        while (index < charArray.length) {
            switch (charArray[index]) {
                case SLASH:
                case SEMI_COLON:
                case OPEN_SQUARE_BRACKET:
                case OPEN_ANGLE_BRACKET: // can't appear in method name, only constructor name
                case CLOSE_ANGLE_BRACKET: // can't appear in method name, only constructor name
                    throw new InvalidRampartRuleException(
                            enhanceMessage(ERROR_MESSAGE_UNEXPECTED_CHARACTER));
                case DOT: // separates method name from method signature
                    return;
            }
            index++;
            validateJavaIdentifierEnd();
        }
    }

    protected abstract void validateMemberSignature() throws InvalidRampartRuleException;
}

