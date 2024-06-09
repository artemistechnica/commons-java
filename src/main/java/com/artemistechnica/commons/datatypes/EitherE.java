package com.artemistechnica.commons.datatypes;

import com.artemistechnica.commons.errors.SimpleError;
import com.artemistechnica.commons.utils.Threads;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static com.artemistechnica.commons.utils.HelperFunctions.getIfPresent;

/**
 * {@link EitherE} is a specialized {@link Either} and represents 'either' a {@link SimpleError} or some value <i><strong>A</strong></i>. It is right-biased in that when applying a function
 * via some map function, the function will not be invoked unless the {@link EitherE} is in a successful state; its <i><strong>left</strong></i> viable is
 * an empty {@link java.util.Optional}.
 * <pre>
 *     // Success
 *     EitherE<String> initE   = EitherE.success("World");
 *     EitherE<String> resultE = initE.map(name -> String.format("Hello, %s!", name));
 *     resultE.isRight();   // true
 *     resultE.isLeft();    // false
 *
 *     // Failure
 *     EitherE<String> initE   = EitherE.failure(SimpleError.create("Exception raised"));
 *     EitherE<String> resultE = initE.map(name -> String.format("Hello, %s!", name));
 *     resultE.isRight();   // false
 *     resultE.isLeft();    // true
 * </pre>
 *
 * @see com.artemistechnica.commons.datatypes.Either
 * @see java.util.Optional
 *
 * @param <A>
 */
public class EitherE<A> extends Either<SimpleError, A> {

    private EitherE(SimpleError left, A right) {
        super(left, right);
    }

    /**
     * Maps a function <i><strong>fn</strong></i> on the value <i><strong>A</strong></i> of an {@link EitherE} that is in a successful state returning
     * some value <i><strong>C</strong></i>.
     *
     * @param fn
     * @return
     * @param <C>
     */
    @Override
    public <C> EitherE<C> map(Function<A, C> fn) {
        return left.map(EitherE::<C>failure).orElseGet(() -> getIfPresent(right.map(right -> tryFunc(() -> fn.apply(right)))));
    }

    /**
     * Maps a function <i><strong>fn</strong></i> on the value <i><strong>A</strong></i> of an {@link EitherE} that is in a successful state, but does
     * so asynchronously in a new virtual thread, returning a <i><strong>C</strong></i> parameterized {@link CompletableFutureE<C>}.
     *
     * @param fn
     * @return
     * @param <C>
     */
    public <C> CompletableFutureE<C> mapAsyncE(Function<A, C> fn) {
        return CompletableFutureE.create(
                CompletableFuture.supplyAsync(
                        () -> left.map(EitherE::<C>failure).orElseGet(() -> getIfPresent(right.map(right -> tryFunc(() -> fn.apply(right))))),
                        Threads.executorService()
                )
        );
    }

    /**
     * Maps a function <i><strong>fn</strong></i> on the value <i><strong>A</strong></i> of an {@link EitherE} that is in a successful state, but does
     * so asynchronously in a new virtual thread, returning a <i><strong>C</strong></i> parameterized {@link CompletableFutureE<C>}. This method has
     * the additional effect of retrying a function <i><strong>fn</strong></i> at most <i><strong>retry</strong></i> number of times.
     *
     * @param retry The number of times to retry a function <i><strong>fn</strong></i>
     * @param fn    The function to apply
     * @return
     * @param <C>
     */
    public <C> CompletableFutureE<C> mapAsyncE(int retry, Function<A, C> fn) {
        return CompletableFutureE.create(
                CompletableFuture.supplyAsync(
                        () -> left.map(EitherE::<C>failure).orElseGet(() -> getIfPresent(right.map(right -> retry(retry, () -> fn.apply(right))))),
                        Threads.executorService()
                )
        );
    }

    /**
     * Maps either an <i><strong>errFn</strong></i> error handling function <i>or</i> a success function <i><strong>fn</strong></i>
     * on some value <i><strong>A</strong></i> depending on if the {@link EitherE} is in a <i>failure</i> state or a <i>success</i> state respectively.
     * @param errFn
     * @param fn
     * @return
     * @param <B>
     */
    public <B> EitherE<B> biMapE(Function<SimpleError, B> errFn, Function<A, B> fn) {
        return this.left.map(err -> EitherE.success(errFn.apply(err)))
                .orElseGet(() -> right.map(right -> tryFunc(() -> fn.apply(right))).get());
    }

    /**
     *
     * @param fn
     * @return
     * @param <B>
     */
    public <B> EitherE<B> flatMapE(Function<A, EitherE<B>> fn) {
        return this.left.map(EitherE::<B>failure)
                .orElseGet(() -> right.map(right -> tryEitherEFunc(() -> fn.apply(right))).get());
    }

    /**
     *
     * @param fn
     * @return
     * @param <C>
     */
    public <C> CompletableFutureE<EitherE<C>> flatMapAsyncE(Function<A, EitherE<C>> fn) {
        return CompletableFutureE.createEitherE(
                CompletableFuture.supplyAsync(
                        () -> this.left.map(EitherE::<C>failure)
                                .orElseGet(() -> right.map(right -> tryEitherEFunc(() -> fn.apply(right))).get()),
                        Threads.executorService()
                )
        );
    }

    /**
     *
     * @param errFn
     * @param fn
     * @return
     * @param <B>
     */
    public <B> EitherE<B> biFlatMapE(Function<SimpleError, EitherE<B>> errFn, Function<A, EitherE<B>> fn) {
        return this.left.map(errFn)
                .orElseGet(() -> right.map(right -> tryEitherEFunc(() -> fn.apply(right))).get());
    }

    /**
     * Static method for creating failed {@link EitherE}.
     * @param error
     * @return
     * @param <A>
     */
    public static <A> EitherE<A> failure(SimpleError error) {
        return new EitherE<>(error, null);
    }

    /**
     * Static method for creating successful {@link EitherE}.
     * @param right
     * @return
     * @param <A>
     */
    public static <A> EitherE<A> success(A right) {
        return new EitherE<>(null, right);
    }
}
