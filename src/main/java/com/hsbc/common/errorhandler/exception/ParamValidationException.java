package com.hsbc.common.errorhandler.exception;

/**
 * Parameter Validation Exception
 * Used to represent parameter validation failures, extends from business exception
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
public class ParamValidationException extends BusinessException {

    /**
     * Constructor with error message
     *
     * @param message Error message
     */
    public ParamValidationException(String message) {
        super(message);
    }
}
