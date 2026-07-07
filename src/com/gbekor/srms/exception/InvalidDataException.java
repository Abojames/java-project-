package com.gbekor.srms.exception;

/** Thrown when user-supplied data fails a business/validation rule. */
public class InvalidDataException extends Exception {
    public InvalidDataException(String message) {
        super(message);
    }
}
