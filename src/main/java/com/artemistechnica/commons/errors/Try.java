package com.artemistechnica.commons.errors;

import com.artemistechnica.commons.datatypes.EitherE;

import java.util.Optional;
import java.util.function.Supplier;

public interface Try {

    /**
     *
     * @param fn
     * @return
     * @param <A>
     */
    default <A> EitherE<A> tryFunc(Supplier<A> fn) {
        try { return EitherE.success(fn.get()); } catch (Exception e) {
            System.out.printf("CAUGHT EXCEPTION %s\n", e.getMessage());
            return EitherE.failure(SimpleError.create(e));
        }
    }

    /**
     *
     * @param fn
     * @return
     * @param <A>
     */
    default <A> EitherE<A> tryEitherEFunc(Supplier<EitherE<A>> fn) {
        try { return fn.get(); } catch (Exception e) {
            System.out.printf("CAUGHT EXCEPTION %s\n", e.getMessage());
            return EitherE.failure(SimpleError.create(e));
        }
    }

    /**
     *
     * @param fn
     * @return
     * @param <A>
     */
    default <A> Optional<A> tryFuncOpt(Supplier<A> fn) {
        try { return Optional.ofNullable(fn.get()); } catch (Exception e) {
            System.out.printf("CAUGHT EXCEPTION %s\n", e.getMessage());
            return Optional.empty();
        }
    }
}
