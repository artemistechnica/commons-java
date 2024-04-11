package com.artemistechnica.commons;

import com.artemistechnica.commons.utils.EitherE;
import com.artemistechnica.commons.utils.Try;

public class Main implements Try {
    public static void main(String[] args) {
        App app = new App();

        app.doThing();
    }

    public static class App implements Try {

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
        }
    }
}