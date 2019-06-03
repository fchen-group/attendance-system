package com.uzpeng.sign.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    private static final int DEFAULT_THREAD_COUNT = 3;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_COUNT);

    public static void run(Runnable task){
        executorService.submit(task);
    }
}
