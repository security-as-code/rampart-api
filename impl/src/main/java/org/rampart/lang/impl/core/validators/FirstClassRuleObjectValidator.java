package org.rampart.lang.impl.core.validators;

import java.util.List;

import org.rampart.lang.api.RampartConstant;

public interface FirstClassRuleObjectValidator {
    List<RampartConstant> allowedKeys();
}
