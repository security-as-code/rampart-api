package org.rampart.lang.impl.core;

/**
 * Interface all Rampart Validators must implement
 */

public interface Validatable<T, E extends Throwable> {
    T validate() throws E;
}
