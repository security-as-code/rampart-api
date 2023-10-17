package org.rampart.lang.api.http;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartObject;

public enum RampartHttpFeaturePattern implements RampartObject {
    INPUT_VALIDATION("validate"),
    XSS("xss"),
    CSRF("csrf"),
    SET_HEADERS,
    SESSION_FIXATION("authenticate"),
    RESPONSE_HEADER_INJECTION("injection"),
    OPEN_REDIRECT("open-redirect");

    private final RampartConstant declarationTerm;

    RampartHttpFeaturePattern(final String declarationTerm) {
        this.declarationTerm = new RampartConstant() {
            @Override
            public String toString() {
                return declarationTerm;
            }
        };
    }

    RampartHttpFeaturePattern() {
        this.declarationTerm = null;
    }

    public RampartConstant getDeclarationTerm() {
        return declarationTerm;
    }
}
