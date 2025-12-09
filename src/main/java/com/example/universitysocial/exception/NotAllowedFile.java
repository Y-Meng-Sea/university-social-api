package com.example.universitysocial.exception;

public class NotAllowedFile extends RuntimeException {
    public NotAllowedFile(String message) {
        super(message);
    }
}
