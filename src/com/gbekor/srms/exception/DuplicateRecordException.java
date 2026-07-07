package com.gbekor.srms.exception;

/** Thrown when attempting to add a record whose ID already exists. */
public class DuplicateRecordException extends Exception {
    public DuplicateRecordException(String message) {
        super(message);
    }
}
