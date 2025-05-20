package com.quicktvui.sdk.core.utils;

import android.os.Handler;

import com.sunrain.toolkit.utils.ThreadUtils;
import com.sunrain.toolkit.utils.thread.Executors;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <br>
 * 注释
 * <br>
 * <br>
 * Created by WeiPeng on 2023-10-25 18:29
 */
public class ESExecutors {

    public static final int MAX_POOL_SIZE;

    static {
        int cupCount = Runtime.getRuntime().availableProcessors();
        MAX_POOL_SIZE = cupCount * 2 + 1;
    }

    public static final ExecutorService CPU = ThreadUtils.getCpuPool();
    public static final ExecutorService IO = ThreadUtils.getIoPool();
    public static final Executor MAIN = new HandlerExecutorServiceImpl(ThreadUtils.getMainHandler());

    public static final ExecutorService UN_LIMIT = Executors.get();

    public static final ExecutorService START_THREAD = createExecutor("start", 1, 1);

    public static final ExecutorService RPK_THREAD = createExecutor("rpk", 1, MAX_POOL_SIZE);

    public static final ExecutorService SO_THREAD = createExecutor("so", 0, 1);

    public static final ExecutorService PLUGIN_THREAD = createExecutor("plugin", 0, 1);

    public static ExecutorService createExecutor(String name, int coreSize, int maxSize) {
        return new ThreadPoolExecutor(coreSize, maxSize, 10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), new ThreadFactory() {

            private final AtomicInteger mCount = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable, String.format("%s-%d", name, this.mCount.getAndIncrement()));
            }
        });
    }

    public static class HandlerExecutorServiceImpl implements Executor {

        private final Handler mHandler;

        public HandlerExecutorServiceImpl(Handler handler) {
            mHandler = handler;
        }

        @Override
        public void execute(Runnable command) {
            mHandler.post(command);
        }
    }

}
