package org.rampart.lang.impl.patch.validators.signatures;

import org.rampart.lang.impl.core.InvalidRampartRuleException;

public class ClassSignatureValidator extends SignatureValidator {

    ClassSignatureValidator(String signature) {
        super(signature);
    }

    @Override
    public void validate() throws InvalidRampartRuleException {
        parseFullyQualifiedClassName();
        // if there are more characters this is an invalid state because a class signature has
        // no member signature
        if (index != charArray.length) {
            throw new InvalidRampartRuleException(enhanceMessage(ERROR_MESSAGE_UNEXPECTED_CHARACTER));
        }
    }

    private void parseFullyQualifiedClassName() throws InvalidRampartRuleException {
        validateJavaIdentifier();
        while (index < charArray.length) {
            switch (charArray[index]) {
                case DOT:
                case OPEN_SQUARE_BRACKET:
                case SEMI_COLON:
                    throw new InvalidRampartRuleException(enhanceMessage(ERROR_MESSAGE_UNEXPECTED_CHARACTER));
                case SLASH:
                    nextChar();
                    validateJavaIdentifier();
                    continue;
            }
            index++;
            validateJavaIdentifierEnd();
        }
    }

}
