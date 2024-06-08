package com.artemistechnica.commons.datatypes;

import com.artemistechnica.commons.errors.SimpleError;
import com.artemistechnica.commons.utils.Threads;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;

public class EitherTests {

    @Test
    public void testSimpleEitherMapAsync() {
        CompletableFutureE<Integer> result2 = EitherE.success(42)
                .mapAsyncE(Object::toString)
                .mapAsyncE(Integer::parseInt);
        EitherE<Integer> finalResult = result2.materialize();

        assert(finalResult.isRight());
    }

    @Test
    public void testSimpleEitherMapAsyncFailure() {
        CompletableFutureE<Integer> result2 = EitherE.success(42)
                .mapAsyncE(Object::toString)
                .mapAsyncE(Integer::parseInt);
        EitherE<Integer> finalResult = result2.materialize(0, TimeUnit.NANOSECONDS);

        assert(finalResult.isLeft());
    }

    @Test
    public void testSimpleEitherMapAsyncLongRunning() {
        AtomicReference<Integer> objReference = new AtomicReference<>(-1);
        CompletableFutureE<Integer> result0 = EitherE.success(42)
                .mapAsyncE(Object::toString)
                .mapAsyncE(str -> {
                    // Sleep the 'long running' thread for 5 seconds and then update the atomic reference.
                    Threads.sleep(5000);
                    objReference.set(1);
                    return Integer.parseInt(str);
                });

        CompletableFutureE<Integer> result1 = EitherE.success(84)
                .mapAsyncE(i -> {
                    // Sanity check the 'delayed' virtual thread has not updated the atomic reference from
                    // its initial value.
                    assert(objReference.get() == -1);
                    return i.toString();
                })
                .mapAsyncE(Integer::parseInt);

        EitherE<Integer> finalResult1 = result1.materialize();
        EitherE<Integer> finalResult0 = result0.materialize();

        assert(objReference.get() == 1);
        assert(finalResult0.isRight());
        assert(finalResult1.isRight());
    }

    @Disabled
    @Test
    public void testSimpleEitherMapAsyncManyLongRunning() {

        BiFunction<Long, String, Function<String, Integer>> mkLongRunningIntegerFn = (sleepLength, msg) -> str -> {
            System.out.println("Start: " + msg);
            // Sleep the 'long running' thread for 5 seconds and then update the atomic reference.
            Threads.sleep(sleepLength);
            System.out.println("Finish: " + msg);
            return Integer.parseInt(str);
        };

        BiFunction<Long, String, Function<Integer, String>> mkLongRunningStringFn = (sleepLength, msg) -> num -> {
            System.out.println("Start: " + msg);
            // Sleep the 'long running' thread for 5 seconds and then update the atomic reference.
            Threads.sleep(sleepLength);
            System.out.println("Finish: " + msg);
            return num.toString();
        };


        AtomicReference<Integer> objReference = new AtomicReference<>(-1);
        CompletableFutureE<Integer> result0 = EitherE.success(42)
                .mapAsyncE(Object::toString)
                .mapAsyncE(mkLongRunningIntegerFn.apply(5000L, "1"))
                .mapAsyncE(mkLongRunningStringFn.apply(500L, "2"))
                .mapAsyncE(mkLongRunningIntegerFn.apply(5000L, "3"))
                .mapAsyncE(mkLongRunningStringFn.apply(3000L, "4"))
                .mapAsyncE(mkLongRunningIntegerFn.apply(50L, "5"))
                .mapAsyncE(i -> {
                    System.out.println("This should run last!");
                    return i;
                });

        CompletableFutureE<Integer> result1 = EitherE.success(84)
                .mapAsyncE(i -> {
                    // Sanity check the 'delayed' virtual thread has not updated the atomic reference from
                    // its initial value.
                    assert(objReference.get() == -1);
                    System.out.println("This should print first!");
                    return i.toString();
                })
                .mapAsyncE(Integer::parseInt);

        EitherE<Integer> finalResult1 = result1.materialize();
        EitherE<Integer> finalResult0 = result0.materialize();

        assert(finalResult0.isRight());
        assert(finalResult1.isRight());
    }

    @Test
    public void testSimpleEitherMapAsyncWithRetrySuccess() {
        AtomicReference<HashSet<Long>> threadId = new AtomicReference<>(new HashSet<>());
        AtomicReference<Integer> retry = new AtomicReference<>(3);
        EitherE<String> result = EitherE.success(42).mapAsyncE(retry.get(), i -> {
            // Add thread ID to set
            threadId.updateAndGet(s -> { s.add(Thread.currentThread().threadId()); return s; });
            Integer currentRetry = retry.getAndUpdate(r -> r - 1);
            // Purposeful division by 0 to generate exception
            return Integer.toString((currentRetry != 1) ? (i / 0) : currentRetry);
        }).materialize();

        assert(result.isRight());
        assert(result.right.get().equals("1"));
        assert(threadId.get().size() == 1);
    }

    @Test
    public void testSimpleEitherMapAsyncWithRetryFailure() {
        AtomicReference<HashSet<Long>> threadId = new AtomicReference<>(new HashSet<>());
        AtomicReference<Integer> retry = new AtomicReference<>(3);
        EitherE<String> result = EitherE.success(42).mapAsyncE(retry.get(), i -> {
            // Add thread ID to set
            threadId.updateAndGet(s -> { s.add(Thread.currentThread().threadId()); return s; });
            Integer currentRetry = retry.getAndUpdate(r -> r - 1);
            // Purposeful division by 0 to generate exception
            return Integer.toString((i / 0));
        }).materialize();

        assert(result.isLeft());
        assert(threadId.get().size() == 1);
    }

    @Test
    public void testSimpleEitherMaterializerRight() {
        Either<String, Integer> e0  = Either.right(42);
        String result               = e0.materialize(str -> str, Object::toString);
        assert(result.equals("42"));
    }

    @Test
    public void testSimpleEitherMaterializerLeft() {
        Either<Boolean, Integer> e0 = Either.left(false);
        String result               = e0.materialize(Object::toString, Object::toString);
        assert(result.equals("false"));
    }

    @Test
    public void testSimpleEitherEMaterializerRight() {
        EitherE<Integer> e0 = EitherE.success(42);
        String result       = e0.materialize(err -> err.error, Object::toString);
        assert(result.equals("42"));
    }

    @Test
    public void testSimpleEitherEMaterializerLeft() {
        EitherE<Integer> e0 = EitherE.failure(SimpleError.create("An error was raised!"));
        String result       = e0.materialize(err -> err.error, Object::toString);
        assert (result.equals("An error was raised!"));
    }

    @Test
    public void testSimpleEitherEMaterializerLeftOptional() {
        EitherE<Integer> e0 = EitherE.failure(SimpleError.create("An error was raised!"));
        Optional<String> result = e0.materializeOpt(Object::toString);
        assert (result.isEmpty());
    }

    @Test
    public void testSimpleEitherEMaterializerRightOptional() {
        EitherE<Integer> e0 = EitherE.success(42);
        Optional<String> result = e0.materializeOpt(Object::toString);
        assert(result.isPresent());
        assert(result.get().equals("42"));
    }

    @Test
    public void testSimpleEitherESuccess() {
        EitherE<String> initE   = EitherE.success("World");
        EitherE<String> resultE = initE.map(name -> String.format("Hello, %s!", name));
        assert(resultE.isRight());
    }

    @Test
    public void testSimpleEitherEFailure() {
        EitherE<String> initE   = EitherE.failure(SimpleError.create("Exception raised"));
        EitherE<String> resultE = initE.map(name -> String.format("Hello, %s!", name));
        assert(resultE.isLeft());
    }
}
