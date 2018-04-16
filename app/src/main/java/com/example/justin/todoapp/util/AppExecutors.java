package com.example.justin.todoapp.util;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {
    private static final String TAG = "test";

    private static int Thread_Count = 3;

    private Executor diskIo;

    private Executor networkIo;

    private Executor mainThread;

    /**
     * 表示该类，方法或字段的可见性放宽，使得它比其他方法更可视化，以使代码可测试。
     */
    @VisibleForTesting
    public AppExecutors(Executor diskIo, Executor networkIo, Executor mainThread) {
        this.diskIo = diskIo;
        this.networkIo = networkIo;
        this.mainThread = mainThread;
    }

    public Executor getDiskIo() {
        return diskIo;
    }

    public Executor getMainThread() {
        return mainThread;
    }

    public Executor getNetworkIo() {
        return networkIo;
    }

    public AppExecutors() {
        this(new DiskIoThreadExecutor(),
                Executors.newFixedThreadPool(Thread_Count),
                new MainThreadExecutor());
    }

    private static class MainThreadExecutor implements Executor {

        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            Log.i(TAG, "MainThreadExecutor execute: ");
            mainThreadHandler.post(command);
        }
    }
}
