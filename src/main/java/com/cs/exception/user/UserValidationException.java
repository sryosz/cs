package com.cs.exception.user;

public class UserValidationException extends RuntimeException{
    public UserValidationException(String message) {
        super(message);
    }

}
