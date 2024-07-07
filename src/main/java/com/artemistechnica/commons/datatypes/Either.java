package com.artemistechnica.commons.datatypes;

import com.artemistechnica.commons.errors.Retry;
import com.artemistechnica.commons.utils.Threads;

import java.util.Optional;
import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * Either
 * @param <A>
 * @param <B>
 */
public class Either<A, B> implements BiSum<A, B>, Retry {

    public final Optional<A> left;
    public final Optional<B> right;

    protected Either(A left, B right) {
        this.left   = Optional.ofNullable(left);
        this.right  = Optional.ofNullable(right);
        if (this.left.isEmpty() && this.right.isEmpty())
            throw new RuntimeException("Either must have exactly one defined member. Found both members as null");
        if (this.left.isPresent() && this.right.isPresent())
            throw new RuntimeException("Either must have exactly one defined member. Found both members as defined");
    }

//    /**
//     * Map
//     * @param fn
//     * @return
//     * @param <C>
//     */
//    public <C> Either<A, C> map(Function<B, C> fn) {
//        return left.map(l -> Either.<A, C>left(l)).orElseGet(() -> right.map(right -> Either.<A, C>right(fn.apply(right))).get());
//    }

//    /**
//     *
//     * @param fn
//     * @return
//     * @param <C>
//     */
//    public <C> Future<Either<A, C>> mapAsync(Function<B, C> fn) {
//        return Threads.executorService().submit(
//                // Callable<Either<A,C>>
//                () -> left.map(l -> Either.<A, C>left(l)).orElseGet(() -> right.map(right -> Either.<A, C>right(fn.apply(right))).get())
//        );
//    }

//    /**
//     * Flatmap
//     * @param fn
//     * @return
//     * @param <C>
//     */
//    public <C, D extends Either<A, C>> Either<A, C> flatMap(Function<B, D> fn) {
//        return left.map(l -> Either.<A, C>left(l)).orElseGet(() -> right.map(fn).get());
//    }

    /**
     * Constructor
     * @param left
     * @return
     * @param <A>
     * @param <B>
     */
    public static <A, B> Either<A, B> left(A left) {
        return new Either<>(left, null);
    }

    /**
     * Constructor
     * @param right
     * @return
     * @param <A>
     * @param <B>
     */
    public static <A, B> Either<A, B> right(B right) {
        return new Either<>(null, right);
    }

    /**
     *
     * @return
     */
    public boolean isRight() {
        return right.isPresent();
    }

    /**
     *
     * @return
     */
    public boolean isLeft() {
        return left.isPresent();
    }

    /**
     *
     * @param errFn
     * @param fn
     * @return
     * @param <C>
     */
    public <C> C resolve(Function<A, C> errFn, Function<B, C> fn) {
        return this.left.map(errFn).orElseGet(() -> right.flatMap(v -> tryFuncOpt(() -> fn.apply(v))).get());
    }

    /**
     *
     * @param fn
     * @return
     * @param <C>
     */
    public <C> Optional<C> resolveOpt(Function<B, C> fn) {
        Function<B, Optional<C>> func = (B v) -> right.flatMap(prevValue -> tryFuncOpt(() -> fn.apply(prevValue)));
        return this.left.map(err -> Optional.<C>empty()).orElseGet(() -> right.flatMap(func));
    }

    @Override
    public Optional<A> left() {
        return left;
    }

    @Override
    public Optional<B> right() {
        return right;
    }

    @Override
    public <C, F extends BiSum<A, C>> F pureLeft(A l) {
        return (F) Either.left(l);
    }

    @Override
    public <C, F extends BiSum<A, C>> F pureRight(C l) {
        return (F) Either.right(l);
    }


//    @Override
//    public <C> Either pureLeft(A l) {
//        return null;
//    }
//
//    @Override
//    public <C, F extends BiSum<A, C, F>> F pureRight(C l) {
//        return null;
//    }
}
