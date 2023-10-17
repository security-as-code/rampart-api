package org.rampart.lang.impl.core.validators.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.parsers.v2.RampartInputParser;
import org.rampart.lang.impl.core.validators.RampartValidatorBase;
import org.rampart.lang.impl.core.validators.FirstClassRuleObjectValidator;

public class RampartInputValidator extends RampartValidatorBase implements FirstClassRuleObjectValidator {

    public RampartInputValidator(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable.get(INPUT_KEY.toString()));
    }

    public RampartList validateDataInputs() throws InvalidRampartRuleException {
        return RampartInputParser.parseDataInputs((RampartList) validatableObject, INPUT_KEY);
    }

    // @Override
    public List<RampartConstant> allowedKeys() {
        return Arrays.asList(INPUT_KEY);
    }
}
