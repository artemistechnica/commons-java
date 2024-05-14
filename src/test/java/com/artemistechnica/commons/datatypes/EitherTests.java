package com.artemistechnica.commons.datatypes;

import com.artemistechnica.commons.errors.SimpleError;
import org.junit.jupiter.api.Test;

public class EitherTests {

    @Test
    public void testSimpleEitherMaterializerRight(){
        Either<String, Integer> e0  = Either.right(42);
        String result               = e0.materialize(str -> str, Object::toString);
        assert(result.equals("42"));
    }

    @Test
    public void testSimpleEitherMaterializerLeft(){
        Either<Boolean, Integer> e0 = Either.left(false);
        String result               = e0.materialize(Object::toString, Object::toString);
        assert(result.equals("false"));
    }

    @Test
    public void testSimpleEitherEMaterializerRight(){
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
}
