package org.rampart.lang.impl.apiprotect.validators;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.http.RampartHttpIOType;

/**
 * Basic DTO that encodes where to actually intercept requests.
 */
final class RampartApiInterceptionPoint {
    final RampartList uriPatterns;
    final RampartHttpIOType requestProcessingStage;

    RampartApiInterceptionPoint(RampartList uriPatterns, RampartHttpIOType requestProcessingStage) {
        this.uriPatterns = uriPatterns;
        this.requestProcessingStage = requestProcessingStage;
    }
}
