package com.artemistechnica.commons.errors;

import java.util.Optional;

public class Error {
    public final int                    code;
    public final String                 error;
    public final Optional<Exception>    exception;

    private Error(int       code,
                  String    error,
                  Exception exception) {
        this.code       = code;
        this.error      = error;
        this.exception  = Optional.ofNullable(exception);
    }

    public static Error create(int code, String error) {
        return new Error(code, error, null);
    }

    public static Error create(int code, String error, Exception e) {
        return new Error(code, error, e);
    }
}
