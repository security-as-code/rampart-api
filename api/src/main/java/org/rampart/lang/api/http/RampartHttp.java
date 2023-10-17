package org.rampart.lang.api.http;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartActionableRule;
import org.rampart.lang.api.core.RampartDataInputsRule;

public interface RampartHttp extends RampartActionableRule, RampartDataInputsRule {
    RampartHttpFeaturePattern getSecurityFeature();

    RampartList getUriValues();

    RampartHttpIOType getHttpIOType();

    // used in the old Http Input Validation API, replaced by:
    // RampartHttpInputValidation.getInputValidation();
    @Deprecated()
    RampartHttpValidationType getHttpValidationType();

    // used in the old Http Input Validation API, replaced by:
    // RampartHttpInputValidation.getInputValidation();
    @Deprecated
    RampartList getValidationMap();

    @Deprecated
    RampartBoolean isOpenRedirect();

    RampartHttpInputValidation getInputValidation();

    RampartOpenRedirect getOpenRedirectConfiguration();

    RampartBoolean isAuthenticate();

    RampartCsrf getCsrfConfiguration();

    RampartXss getXssConfiguration();

    RampartHttpInjectionType getInjectionType();
}
