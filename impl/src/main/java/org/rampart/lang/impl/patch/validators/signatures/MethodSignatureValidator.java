package org.rampart.lang.impl.patch.validators.signatures;

import org.rampart.lang.impl.core.InvalidRampartRuleException;

class MethodSignatureValidator extends MemberSignatureValidator {

    static final char OPEN_PAREN = '(';
    static final char CLOSE_PAREN = ')';

    static final String ERROR_MESSAGE_OPEN_PAREN_EXPECTED =
            "'" + OPEN_PAREN + "' expected";

    MethodSignatureValidator(String signature) {
        super(signature);
    }

    protected void validateMethodSignature() throws InvalidRampartRuleException {
        if (charArray[index] != OPEN_PAREN) {
            throw new InvalidRampartRuleException(ERROR_MESSAGE_OPEN_PAREN_EXPECTED);
        }
        nextChar();
        if (charArray[index] != CLOSE_PAREN) {
            do {
                validateType();
            } while (index < charArray.length && charArray[index] != CLOSE_PAREN);
        }
        if (index == charArray.length) {
            throw new InvalidRampartRuleException(ERROR_MESSAGE_INCOMPLETE_SIGNATURE);
        }
        nextChar();
        validateReturnType();
    }

    @Override
    protected void validateMemberSignature() throws InvalidRampartRuleException {
        validateMemberName();
        checkForValidParsingSeparator();
        validateMethodSignature();
        // Completed parsing successfully, if there are any characters left over, we throw
        if (index != charArray.length ) {
            throw new InvalidRampartRuleException(
                    enhanceMessage(ERROR_MESSAGE_UNEXPECTED_CHARACTER_AT_END_OF_SIGNATURE));
        }
    }

    void validateReturnType() throws InvalidRampartRuleException {
        if (charArray[index] != VOID_TYPE) {
            validateType();
            return;
        }
        index++;
    }
}
