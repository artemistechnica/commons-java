package com.artemistechnica.commons.datatypes;

import com.artemistechnica.commons.errors.SimpleError;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class EitherE<A> extends Either<SimpleError, A> {

    private EitherE(SimpleError left, A right) {
        super(left, right);
    }

    @Override
    public <C> EitherE<C> map(Function<A, C> fn) {
        return left.map(EitherE::<C>failure).orElseGet(() -> right.map(right -> tryFunc(() -> fn.apply(right))).get());
    }

    public <C> CompletableFutureE<C> mapAsyncE(Function<A, C> fn) {
        return CompletableFutureE.create(CompletableFuture.supplyAsync(
                () -> left.map(EitherE::<C>failure).orElseGet(() -> right.map(right -> tryFunc(() -> fn.apply(right))).get()),
                _asyncService
        ));
    }

    public <B> EitherE<B> biMapE(Function<SimpleError, B> errFn, Function<A, B> fn) {
        return this.left.map(err -> EitherE.success(errFn.apply(err)))
                .orElseGet(() -> right.map(right -> tryFunc(() -> fn.apply(right))).get());
    }

    public <B> EitherE<B> flatMapE(Function<A, EitherE<B>> fn) {
        return this.left.map(EitherE::<B>failure)
                .orElseGet(() -> right.map(right -> tryEitherEFunc(() -> fn.apply(right))).get());
    }

    public <B> EitherE<B> biFlatMapE(Function<SimpleError, EitherE<B>> errFn, Function<A, EitherE<B>> fn) {
        return this.left.map(errFn)
                .orElseGet(() -> right.map(right -> tryEitherEFunc(() -> fn.apply(right))).get());
    }

    public static <A> EitherE<A> failure(SimpleError error) {
        return new EitherE<>(error, null);
    }

    public static <A> EitherE<A> success(A right) {
        return new EitherE<>(null, right);
    }
}
