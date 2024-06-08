package com.artemistechnica.commons.utils;

import java.util.Optional;

public interface HelperFunctions {

    static <A> A identity(A a) { return a; }

    static <A> A getIfPresent(Optional<A> opt) {
        if (opt.isPresent()) return opt.get(); else throw new RuntimeException("java.util.Optional is empty");
    }
}
