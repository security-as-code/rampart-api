package org.rampart.lang.impl.core.validators.v2;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartCode;
import org.rampart.lang.impl.core.RampartCodeImpl;
import org.rampart.lang.impl.core.RampartVersionImpl;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.v1.RampartCodeValidatorUpTo1_5;

import static org.rampart.lang.java.RampartPrimitives.newRampartConstant;

public class RampartCodeValidator2_0 extends RampartCodeValidatorUpTo1_5 {

    public RampartCodeValidator2_0(RampartList codeValues, RampartList sourceCode) {
        super(codeValues, sourceCode);
    }

    @Override
    protected RampartString getSourceCodeLanguage(RampartObject language) throws InvalidRampartRuleException {
        if (!(language instanceof RampartConstant)) {
            throw new InvalidRampartRuleException("language definition must be a constant");
        }
        return ((RampartConstant) language).asRampartString();
    }

    @Override
    public RampartCode validateCodeBlock() throws InvalidRampartRuleException {
        return new RampartCodeImpl(newRampartConstant(validateSourceLanguage().toString()), RampartVersionImpl.v2_0,
                validateSourceCode(), validateImports());
    }
}
