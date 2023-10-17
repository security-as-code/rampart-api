package org.rampart.lang.impl.core.validators;

import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.marshal.DeserialStrategy;

public interface RampartDeserialStrategyValidator extends FirstClassRuleObjectValidator {
    DeserialStrategy validateStrategy() throws InvalidRampartRuleException;
}
