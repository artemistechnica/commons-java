package com.artemistechnica.commons.errors;

import com.artemistechnica.commons.datatypes.EitherE;

import java.util.function.Supplier;

public interface Retry extends Try {

    /**
     *
     * @param times
     * @param fn
     * @return
     * @param <A>
     */
    default <A> EitherE<A> retry(int times, Supplier<A> fn) {
        EitherE<A> result = EitherE.failure(SimpleError.create("Function did not execute"));
        while (times > 0) {
            --times;
            result = tryFunc(fn);
            if (result.isRight()) break;
        }
        return result;
    }
}
