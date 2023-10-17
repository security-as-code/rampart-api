package org.rampart.lang.impl.patch.validators.signatures;

import org.rampart.lang.impl.core.InvalidRampartRuleException;

public abstract class SignatureValidator {
    private static final char SPACE = ' ';
    private static final char HAT = '^';

    static final char SEMI_COLON = ';';
    static final char DOT = '.';
    static final char SLASH = '/';
    static final char OPEN_SQUARE_BRACKET = '[';

    static final String ERROR_MESSAGE_INCOMPLETE_SIGNATURE = "incomplete signature detected";
    static final String ERROR_MESSAGE_UNEXPECTED_CHARACTER = "unexpected signature character detected";
    static final String ERROR_MESSAGE_UNEXPECTED_CHARACTER_AT_END_OF_SIGNATURE = "unexpected character at end of signature";

    final String signature;
    final char[] charArray;
    int index = 0;

    SignatureValidator(String signature) {
        this.signature = signature;
        this.charArray = signature.toCharArray();
    }

    public abstract void validate() throws InvalidRampartRuleException;

    String enhanceMessage(String errorMessage) {
        String enhancedErrorMessage = errorMessage + "\n" + signature + "\n";
        StringBuilder sb = new StringBuilder();
        // extra char to accommodate end of string position
        for (int i = 0; i <= signature.length(); i++) {
            sb.append(SPACE);
        }
        sb.setCharAt(index, HAT);
        enhancedErrorMessage += sb.toString();
        return enhancedErrorMessage;
    }

    void nextChar() throws InvalidRampartRuleException {
        if (++index == charArray.length) {
            throw new InvalidRampartRuleException(enhanceMessage(ERROR_MESSAGE_INCOMPLETE_SIGNATURE));
        }
    }

    void validateJavaIdentifier() throws InvalidRampartRuleException {
        validateJavaIdentifierStart();
        validateJavaIdentifierEnd();
    }

    /**
     * Check the first character of a java identifier.
     * When an unconventional,but legal character, as denoted in JVMS(S4.2.2), is encountered, it
     * will be allowed with a warning.
     */
    private void validateJavaIdentifierStart() throws InvalidRampartRuleException {
        if (!Character.isJavaIdentifierStart(charArray[index])) {
            throw new InvalidRampartRuleException(enhanceMessage(ERROR_MESSAGE_UNEXPECTED_CHARACTER));
        }
        // NOTE: nextChar() is used when there MUST be a character at the next index.
        index++;
    }

    /**
     * Check the characters of the identifier, excluding the first character.
     * If a character is not deemed a JavaIdentifierPart, the caller of this method
     * will determine whether or not the character is valid. The character's validity
     * depends on whether the identifier is part of a FQCN, member name, signature
     * type, or field type.
     */
    void validateJavaIdentifierEnd() throws InvalidRampartRuleException {
        while (index < charArray.length) {
            if (!Character.isJavaIdentifierPart(charArray[index])) {
                break;
            }
            index++;
        }
    }
}
