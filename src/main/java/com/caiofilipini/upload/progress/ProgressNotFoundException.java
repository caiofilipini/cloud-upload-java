package com.caiofilipini.upload.progress;

public class ProgressNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ProgressNotFoundException(String message) {
        super(message);
    }

}
