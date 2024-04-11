package com.artemistechnica.commons.utils;

import com.artemistechnica.commons.Either;
import com.artemistechnica.commons.errors.SimpleError;

public class EitherE<A> extends Either<SimpleError, A> {

    public EitherE(SimpleError left, A right) {
        super(left, right);
    }

    public static <A> EitherE<A> failure(SimpleError error) {
        return new EitherE<>(error, null);
    }

    public static <A> EitherE<A> success(A right) {
        return new EitherE<>(null, right);
    }
}
