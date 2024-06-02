package com.artemistechnica.commons.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Threads {

    private static ExecutorService asyncService;

    public static ExecutorService executorService() {
        if (asyncService == null) asyncService = Executors.newVirtualThreadPerTaskExecutor();
        return asyncService;
    }

    public static void shutdownExecutorService() {
        if (asyncService != null) asyncService.shutdown();
    }

    public static void sleep(long length) {
        try { Thread.sleep(length); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}
