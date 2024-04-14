package com.artemistechnica.commons;

import com.artemistechnica.commons.utils.EitherE;
import com.artemistechnica.commons.utils.Retry;
import com.artemistechnica.commons.utils.Try;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Main implements Try {
    public static void main(String[] args) {
        App app = new App();

        app.doThing();
    }

    public static class App implements Retry {

        public void doThing() {
            Either<String, Integer> e0 = Either.right(1);
            Either<String, Integer> e1 = e0.map(i -> i + 1);
            Either<String, String> e2 = e0.flatMap(i -> Either.right(Integer.toString(i + 42)));

            Either<String, Integer> e3 = Either.<String, Integer>left("ERROR");
            Either<String, Integer> e4 = e3.map(i -> i + 100);

            try {
                Either<String, Integer> e5 = Either.<String, Integer>right(null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            EitherE<Integer> e6 = tryFn(() -> {
                throw new RuntimeException("Something bad happened");
            });

            AtomicReference<Integer> count = new AtomicReference<>(0);
            EitherE<Integer> res0 = retry(3, () -> {
                int c = count.get();
                count.set(c + 1);
                return 1 / c;
            });
        }
    }
}