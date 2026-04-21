package com.giitotech.product_management.exception;

import org.springframework.validation.FieldError;

import java.util.List;

public class ValidationException extends RuntimeException{
    private final List<FieldError> fieldErrors;

    public ValidationException(List<FieldError> fieldErrors) {
        super("Validation failed with " + fieldErrors.size() + " error(s)");
        this.fieldErrors = fieldErrors;
    }

    public List<FieldError> getFieldErrors () {
        return fieldErrors;
    }
}
