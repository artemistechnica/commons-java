package com.artemistechnica.commons.errors;

import java.util.Optional;

public class SimpleError {
    public final int                    code;
    public final String                 error;
    public final Optional<Throwable>    exception;

    private SimpleError(int       code,
                        String    error,
                        Throwable exception) {
        this.code       = code;
        this.error      = error;
        this.exception  = Optional.ofNullable(exception);
    }

    public static SimpleError create(int code, String error) {
        return new SimpleError(code, error, null);
    }

    public static SimpleError create(int code, String error, Throwable e) {
        return new SimpleError(code, error, e);
    }

    public static SimpleError create(Throwable err) {
        return create(500, err.getMessage(), err);
    }
}
