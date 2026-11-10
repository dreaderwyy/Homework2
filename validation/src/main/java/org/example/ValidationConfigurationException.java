package org.example;

public class ValidationConfigurationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ValidationConfigurationException(String message) {
        super(message);
    }

    public ValidationConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
