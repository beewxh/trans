package com.hsbc.common.errorhandler.exception;

/**
 * Framework Exception
 * Used to represent framework-level exceptions, such as configuration errors, initialization failures, etc.
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
public class FrameworkException extends RuntimeException {

    /**
     * Constructor with error message
     *
     * @param message Error message
     */
    public FrameworkException(String message) {
        super(message);
    }

    /**
     * Constructor with error message and cause
     *
     * @param message Error message
     * @param cause Exception cause
     */
    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Default constructor
     */
    public FrameworkException() {
        super();
    }

    /**
     * Constructor with cause
     *
     * @param cause Exception cause
     */
    public FrameworkException(Throwable cause) {
        super(cause);
    }
}
