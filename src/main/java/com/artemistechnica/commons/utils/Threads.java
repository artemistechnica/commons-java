package com.artemistechnica.commons.utils;

public class Threads {

    public static void sleep(long length) {
        try { Thread.sleep(length); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}
