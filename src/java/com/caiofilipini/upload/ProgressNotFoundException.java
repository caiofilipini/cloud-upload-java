package com.caiofilipini.upload;

public class ProgressNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ProgressNotFoundException(String message) {
        super(message);
    }

}
