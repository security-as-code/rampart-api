package org.rampart.lang.impl.patch.validators.signatures;

import org.rampart.lang.impl.core.InvalidRampartRuleException;

/**
 * Class to validate a static constructor
 * e.g. com/foo/bar/Class.<clinit>()V
 */
class StaticConstructorSignatureValidator extends ConstructorSignatureValidator {
    private static final String CLINIT_STRING = "clinit";

    private static final String ERROR_MESSAGE_STATIC_CONSTRUCTOR_NAME_EXPECTED =
            "'" + CLINIT_STRING + "' expected";

    StaticConstructorSignatureValidator(String signature) {
        super(signature);
    }

    @Override
    protected void checkConstructorNameMatches(String constructorName) throws InvalidRampartRuleException {
        if (!CLINIT_STRING.startsWith(constructorName)) {
            throw new InvalidRampartRuleException(enhanceMessage(
                    ERROR_MESSAGE_STATIC_CONSTRUCTOR_NAME_EXPECTED));
        }
    }

    /**
     * Override validateMethodSignature to not accept parameters as clinit does not take any.
     * @throws InvalidRampartRuleException when anything but <clinit>()V is present.
     */
    @Override
    protected void validateMethodSignature() throws InvalidRampartRuleException {
        if (charArray[index] != OPEN_PAREN) {
            throw new InvalidRampartRuleException(ERROR_MESSAGE_OPEN_PAREN_EXPECTED);
        }
        nextChar();
        if (charArray[index] != CLOSE_PAREN) {
            throw new InvalidRampartRuleException("clinit cannot have any parameters");
        }
        nextChar();
        validateReturnType();
    }
}
