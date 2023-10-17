package org.rampart.lang.impl.core;

/**
 * Exception to be thrown when an Rampart App is not well formed.
 */
public class InvalidRampartRuleException extends Exception {
    public InvalidRampartRuleException(String message) {
        super(message);
    }

    public InvalidRampartRuleException(String message, Throwable t) {
        super(message, t);
    }
}
