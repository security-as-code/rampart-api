package org.rampart.lang.impl.patch.validators.signatures;

import org.rampart.lang.impl.core.InvalidRampartRuleException;

/**
 * Class to parse a class field signature
 * e.g. "com/foo/bar/Class.field"
 * @since 1.4
 */
class FieldSignatureValidator extends MemberSignatureValidator {

    FieldSignatureValidator(String parameter) {
        super(parameter);
    }

    /**
     * Parse the field type and ensure there is nothing more to consume after we are finished.
     * @throws InvalidRampartRuleException if there are more characters present to consume
     *  after we identify the field type.
     */
    @Override
    protected void validateMemberSignature() throws InvalidRampartRuleException {
        validateMemberName();
        if (index != charArray.length) {
            throw new InvalidRampartRuleException(enhanceMessage(ERROR_MESSAGE_UNEXPECTED_CHARACTER_AT_END_OF_SIGNATURE));
        }
    }
}
