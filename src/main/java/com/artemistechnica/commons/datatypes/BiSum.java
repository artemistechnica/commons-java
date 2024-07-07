package com.artemistechnica.commons.datatypes;

import com.artemistechnica.commons.utils.Threads;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Function;

import static com.artemistechnica.commons.utils.HelperFunctions.getIfPresent;

public interface BiSum<A, B> {

    Optional<A> left();
    Optional<B> right();

    <C, F extends BiSum<A, C>> F pureLeft(A l);
    <C, F extends BiSum<A, C>> F pureRight(C l);

    default <C, F extends BiSum<A, C>> F map(Function<B, C> fn) {
        return left().map(this::<C, F>pureLeft).orElseGet(() -> getIfPresent(right().map(right -> this.pureRight(fn.apply(right)))));
    }

    default <C, F extends BiSum<A, C>> F flatMap(Function<B, F> fn) {
        return left().map(this::<C, F>pureLeft).orElseGet(() -> getIfPresent(right().map(fn)));
    }

    default <C, F extends BiSum<A, C>> Future<F> mapAsync(Function<B, C> fn) {
        return Threads.executorService().submit(() -> map(fn));
    }

//    default <C, F extends BiSum<A, C>> CompletableFutureE<C> mapAsyncE(int retry, Function<A, C> fn) {
//        return CompletableFutureE.create(
//                CompletableFuture.supplyAsync(
//                        () -> left().map(this::<C, F>pureLeft).orElseGet(() -> getIfPresent(right().map(right -> retry(retry, () -> fn.apply(right))))),
//                        Threads.executorService()
//                )
//        );
//    }
}
