package org.rampart.lang.java;

public class InvalidRampartSyntaxError extends RuntimeException {

    private final String filePath;

    public InvalidRampartSyntaxError(String message, String filePath) {
        super(message);
        this.filePath = filePath;
    }

    public InvalidRampartSyntaxError(String message) {
        super(message);
        this.filePath = null;
    }

    public String getFilePath() {
        return filePath;
    }
}
