package com.artemistechnica.commons.processing;

import com.artemistechnica.commons.datatypes.CompletableFutureE;
import com.artemistechnica.commons.datatypes.EitherE;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class PipelineTests implements Pipeline {

    @Test
    public void testSimplePipeline() {
        int count = 100;
        List<Function<Integer, EitherE<Integer>>> fns = IntStream.rangeClosed(1, count).boxed().toList()
                .stream().map(i -> (Function<Integer, EitherE<Integer>>) integer -> EitherE.success(integer + 1)).toList();

        Function<Integer, EitherE<Integer>>[] steps = fns.<Function<Integer, EitherE<Integer>>>toArray(new Function[0]);
        Function<Integer, EitherE<PipelineResult.Materializer<Integer>>> pipelineFn = this.pipeline(steps);
        EitherE<Integer> result = pipelineFn.apply(0).flatMap(mat -> mat.materialize(i -> i));
        assert(result.isRight());
        assert(result.right.isPresent());
        assert(result.right.get() == count);
    }

    @Test
    public void testSimpleAsyncPipeline() {
        int count = 100;
        List<Function<Integer, EitherE<Integer>>> fns = IntStream.rangeClosed(1, count).boxed().toList()
                .stream().map(i -> (Function<Integer, EitherE<Integer>>) integer -> EitherE.success(integer + 1)).toList();

        Function<Integer, EitherE<Integer>>[] steps = fns.<Function<Integer, EitherE<Integer>>>toArray(new Function[0]);
        Function<Integer, CompletableFutureE<PipelineResult.Materializer<Integer>>> pipelineFn = this.pipelineAsync(steps);
        EitherE<Integer> result = pipelineFn.apply(0).flatMapAsyncE(mat -> mat.materialize(i -> i)).materialize();
        assert(result.isRight());
        assert(result.right.isPresent());
        assert(result.right.get() == count);
    }

    @Test
    public void testSimplePipelineWithExtremeNumberOfSteps() {
        long count = 10000000L;
        List<Function<Long, EitherE<Long>>> fns = LongStream.rangeClosed(1, count).boxed().toList()
                .stream().map(i -> (Function<Long, EitherE<Long>>) n -> EitherE.success(n + 1)).toList();

        Function<Long, EitherE<Long>>[] steps = fns.<Function<Long, EitherE<Long>>>toArray(new Function[0]);
        Function<Long, EitherE<PipelineResult.Materializer<Long>>> pipelineFn = this.<Long>pipeline(steps);
        EitherE<Long> result = pipelineFn.apply(0L).flatMap(mat -> mat.materialize(i -> i));
        assert(result.isRight());
        assert(result.right.isPresent());
        assert(result.right.get() == count);
    }

    @Test
    public void testSimplePipelineAsFunctionArgument() {
        Function<String, EitherE<PipelineResult.Materializer<String>>> p0 = pipeline(
                (String str) -> EitherE.success(String.format("Hello, %s!", str))
        );

        BiFunction<String, Function<String, EitherE<PipelineResult.Materializer<String>>>, EitherE<String>> fn = (String name, Function<String, EitherE<PipelineResult.Materializer<String>>> pipe) -> {
            return pipe.apply(name).flatMap(mat -> mat.materialize(str -> str));
        };

        EitherE<String> result = fn.apply("Nick", p0);
        assert(result.isRight());
        assert(result.right.get().equals("Hello, Nick!"));
    }

    @Test
    public void testSimplePipelineMultipleExecution() {
        int count = 10;
        List<Function<Integer, EitherE<Integer>>> fns = IntStream.rangeClosed(1, count).boxed().toList()
                .stream().map(i -> (Function<Integer, EitherE<Integer>>) integer -> { System.out.println(UUID.randomUUID().toString()); return EitherE.success(integer + 1); }).toList();

        Function<Integer, EitherE<Integer>>[] steps = fns.<Function<Integer, EitherE<Integer>>>toArray(new Function[0]);
        Function<Integer, EitherE<PipelineResult.Materializer<Integer>>> pipelineFn = this.<Integer>pipeline(steps);

        EitherE<Integer> result = pipelineFn.apply(0).flatMap(mat -> mat.materialize(i -> i));
        assert(result.isRight());
        assert(result.right.isPresent());
        assert(result.right.get() == count);

        EitherE<Integer> result0 = pipelineFn.apply(0).flatMap(mat -> mat.materialize(i -> i));
        assert(result0.isRight());
        assert(result0.right.isPresent());
        assert(result0.right.get() == count);
    }
}
