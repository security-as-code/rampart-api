package org.rampart.lang.impl.core;

/**
 * This exception is to be used when the context of the validation is only known at runtime (e.g. RampartApp or RampartRule?).
 * With this exception it's possible to make validators universal across apps and rules, where the functionality is
 * shared between them. As an example this was required for the `metadata` declaration which can be declared within
 * apps and rules.
 */
public class ValidationError extends Exception {
    public ValidationError(String cause) {
        super(cause);
    }
}
