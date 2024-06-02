package com.artemistechnica.commons.datatypes;

import com.artemistechnica.commons.errors.Retry;
import com.artemistechnica.commons.errors.SimpleError;

import java.util.concurrent.*;
import java.util.function.Function;

public class CompletableFutureE<A> implements Retry {

        private final ExecutorService               _asyncService = Executors.newVirtualThreadPerTaskExecutor();
        private final CompletableFuture<EitherE<A>> _future;

        private CompletableFutureE(CompletableFuture<EitherE<A>> result) {
            this._future = result;
        }

        public <C> CompletableFutureE<C> mapAsyncE(Function<A, C> fn) {
            return create(
                    _future.thenApplyAsync(
                            r -> r.left.map(EitherE::<C>failure).orElseGet(() -> r.right.map(right -> tryFunc(() -> fn.apply(right))).get()),
                            _asyncService
                    )
            );
        }

        public EitherE<A> materialize() {
            try {
                return _future.get();
            } catch (InterruptedException | ExecutionException e) {
                return EitherE.failure(SimpleError.create(e));
            }
        }

        public EitherE<A> materialize(long timeout, TimeUnit timeUnit) {
            try {
                return _future.get(timeout, timeUnit);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                return EitherE.failure(SimpleError.create(e));
            }
        }

        public static <A> CompletableFutureE<A> create(CompletableFuture<EitherE<A>> result) {
            return new CompletableFutureE<>(result);
        }
    }