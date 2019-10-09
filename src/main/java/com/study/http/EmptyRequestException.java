package com.study.http;

public class EmptyRequestException extends Exception {
    public EmptyRequestException() {
        super();
    }

    public EmptyRequestException(String message) {
        super(message);
    }

    public EmptyRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyRequestException(Throwable cause) {
        super(cause);
    }

    protected EmptyRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

