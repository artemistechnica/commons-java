package com.artemistechnica.commons;

public class Main {
    public static void main(String[] args) {
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
    }
}