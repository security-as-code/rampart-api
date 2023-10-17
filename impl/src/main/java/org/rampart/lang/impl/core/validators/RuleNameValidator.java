package org.rampart.lang.impl.core.validators;

import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

/**
 * Class to parse (extract) a rule name. E.g., patch("patch rule name")
 */
public class RuleNameValidator extends RampartValidatorBase {
    public RuleNameValidator(RampartObject ruleNameObject) {
        super(ruleNameObject);
    }

    public RampartString validateRuleName() throws InvalidRampartRuleException {
        return validateIsNotEmptyString("Rule name");
    }
}
