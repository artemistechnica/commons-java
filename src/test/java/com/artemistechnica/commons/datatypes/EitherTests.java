package com.artemistechnica.commons.datatypes;

import com.artemistechnica.commons.errors.SimpleError;
import com.artemistechnica.commons.utils.Threads;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

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
}
