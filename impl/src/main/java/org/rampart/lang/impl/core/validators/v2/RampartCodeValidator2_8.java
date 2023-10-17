package org.rampart.lang.impl.core.validators.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;

import org.rampart.lang.api.RampartList;

public class RampartCodeValidator2_8 extends RampartCodeValidator2_0 {

    private static final RampartList SUPPORTED_CODE_BLOCK_LANGUAGES =
        newRampartList(JAVA_KEY, JAVASCRIPT_KEY, CSHARP_KEY);


    public RampartCodeValidator2_8(RampartList codeValues, RampartList sourceCode) {
        super(codeValues, sourceCode);
    }

    @Override
    protected RampartList supportedCodeBlockLanguages() {
        return SUPPORTED_CODE_BLOCK_LANGUAGES;
    }
}
