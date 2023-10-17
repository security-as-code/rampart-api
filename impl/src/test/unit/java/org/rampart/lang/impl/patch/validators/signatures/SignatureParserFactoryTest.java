package org.rampart.lang.impl.patch.validators.signatures;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import org.junit.jupiter.api.Test;

import org.rampart.lang.api.patch.RampartPatchType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.patch.validators.signatures.*;

public class SignatureParserFactoryTest {

    private SignatureValidator parser;

    @Test
    public void factoryGivesTypeMethodSignatureValidatorForCall() throws InvalidRampartRuleException {
        parser = SignatureValidatorFactory.createSignatureValidator("com/foo/bar/Class.main(Ljava/lang/String;)V", RampartPatchType.CALL);
        assertThat(parser, instanceOf(MethodSignatureValidator.class));
    }

    @Test
    public void factoryGivesTypeStaticConstructorSignatureValidatorForCall() throws InvalidRampartRuleException {
        parser = SignatureValidatorFactory.createSignatureValidator("com/foo/bar/Class.<clinit>()V", RampartPatchType.CALL);
        assertThat(parser, instanceOf(StaticConstructorSignatureValidator.class));
    }

    @Test
    public void factoryGivesTypeConstructorSignatureValidatorForCall() throws InvalidRampartRuleException {
        parser = SignatureValidatorFactory.createSignatureValidator("com/foo/bar/Class.<init>()V", RampartPatchType.CALL);
        assertThat(parser, instanceOf(ConstructorSignatureValidator.class));
    }

    @Test
    public void factoryGivesTypeClassSignatureValidatorForError() throws InvalidRampartRuleException {
        parser = SignatureValidatorFactory.createSignatureValidator("com/foo/bar/Class", RampartPatchType.ERROR);
        assertThat(parser, instanceOf(ClassSignatureValidator.class));
    }

    @Test
    public void factoryGivesTypeFieldSignatureValidatorForRead() throws InvalidRampartRuleException {
        parser = SignatureValidatorFactory.createSignatureValidator("com/foo/bar/Class.field", RampartPatchType.READ);
        assertThat(parser, instanceOf(FieldSignatureValidator.class));
    }

    @Test
    public void factoryGivesTypeFieldSignatureValidatorForWrite() throws InvalidRampartRuleException {
        parser = SignatureValidatorFactory.createSignatureValidator("com/foo/bar/Class.field", RampartPatchType.WRITE);
        assertThat(parser, instanceOf(FieldSignatureValidator.class));
    }

}
