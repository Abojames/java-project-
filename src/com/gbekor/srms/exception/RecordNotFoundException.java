package com.gbekor.srms.exception;

/** Thrown when a search/update/delete targets an ID that does not exist. */
public class RecordNotFoundException extends Exception {
    public RecordNotFoundException(String message) {
        super(message);
    }
}
