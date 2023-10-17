package org.rampart.lang.java;

import org.rampart.lang.api.RampartString;
import org.rampart.lang.java.parser.RampartSingleAppVisitor;

/**
 * Note: This needs to be unchecked so we can throw it from the ANTLR visitor
 * @see RampartSingleAppVisitor
 */
public class InvalidRampartAppException extends RuntimeException {

    private final RampartString filePath;

    public InvalidRampartAppException(RampartString filePath, String message, Throwable t) {
        super(message, t);
        this.filePath = filePath;
    }

    public InvalidRampartAppException(RampartString filePath, String message) {
        super(message);
        this.filePath = filePath;
    }

    public InvalidRampartAppException(String message) {
        super(message);
        this.filePath = null;
    }

    public InvalidRampartAppException(String message, Throwable t) {
        super(message, t);
        this.filePath = null;
    }

    public RampartString getFilePath() {
        return filePath;
    }
}