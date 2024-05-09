package com.artemistechnica.commons.errors;

import com.artemistechnica.commons.datatypes.EitherE;

import java.util.function.Supplier;

public interface Try {

    default <A> EitherE<A> tryFn(Supplier<A> fn) {
        try { return EitherE.success(fn.get()); } catch (Exception e) {
            System.out.printf("CAUGHT EXCEPTION %s\n", e.getMessage());
            return EitherE.failure(SimpleError.create(e));
        }
    }
}