package com.artemistechnica.commons.processing;


import com.artemistechnica.commons.datatypes.CompletableFutureE;
import com.artemistechnica.commons.datatypes.EitherE;
import com.artemistechnica.commons.errors.Retry;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface Pipeline {

    default <A> Function<A, EitherE<PipelineResult.Materializer<A>>> pipeline(Function<A, EitherE<A>>... stages) {
        return (A ctx) -> {
            EitherE<A> resultE = Arrays.stream(stages)
                    .map(this::step)
                    .reduce(
                            EitherE.success(ctx),
                            (acc, step) -> acc.flatMapE(step),
                            (acc0, acc1) -> acc1
                    );
            return resultE.map(PipelineResult::construct);
        };
    }

    default <A> Function<A, CompletableFutureE<PipelineResult.Materializer<A>>> pipelineAsync(Function<A, EitherE<A>>... stages) {
        return (A ctx) -> {
            CompletableFutureE<A> resultE = Arrays.stream(stages)
                    .map(this::step)
                    .reduce(
                            CompletableFutureE.create(CompletableFuture.completedFuture(EitherE.success(ctx))),
                            (acc, step) -> acc.flatMapAsyncE(step),
                            (acc0, acc1) -> acc1
                    );
            return resultE.mapAsyncE(PipelineResult::construct);
        };
    }

    default <A> Function<A, CompletableFutureE<PipelineResult.Materializer<A>>> pipelineAsyncParallel(Function<A, EitherE<A>>... stages) {
        return (A ctx) -> {
            CompletableFutureE<A> resultE = Arrays.stream(stages)
                    .map(this::step)
                    .parallel()
                    .reduce(
                            CompletableFutureE.create(CompletableFuture.completedFuture(EitherE.success(ctx))),
                            (acc, step) -> acc.flatMapAsyncE(step),
                            (acc0, acc1) -> acc1
                    );
            return resultE.mapAsyncE(PipelineResult::construct);
        };
    }

    private <A> Function<A, EitherE<A>> step(Function<A, EitherE<A>> fn) {
        // TODO metrics
//        Function<Metrics.Context, EitherE<Metrics.Context>> m = new Metrics() {}.metrics(new Metrics.Context("PIPELINE STEP"));
        return (A ctx) -> fn.apply(ctx);//m.apply(new Metrics.Context()).flatMapE(c -> fn.apply(ctx));
    }

    interface PipelineResult {

        class Materializer<A> implements Retry {
            private final A result;

            private Materializer(A result) { this.result = result; }

            public <B> EitherE<B> materialize(Function<A, B> matFn) {
                return retry(3, () -> matFn.apply(result));
            }
        }

        static <A> Materializer<A> construct(A result) {
            return new Materializer<>(result);
        }
    }
}
