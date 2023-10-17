package org.rampart.lang.impl.core.validators;

import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

/**
 * The common interface for all of the Action validators.
 * @since 2.3
 */
public interface RampartActionValidator extends FirstClassRuleObjectValidator {

    RampartAction validateRampartAction() throws InvalidRampartRuleException;

}
