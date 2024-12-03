package com.matezalantoth.codeconverse.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String item) {
        super(item + " could not be found");
    }
}
