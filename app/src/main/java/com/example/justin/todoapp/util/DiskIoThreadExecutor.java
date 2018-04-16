package com.example.justin.todoapp.util;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DiskIoThreadExecutor implements Executor {

    private Executor mDiskIo;

    public DiskIoThreadExecutor() {
        mDiskIo = Executors.newSingleThreadExecutor();
    }

    @Override
    public void execute(@NonNull Runnable command) {
        mDiskIo.execute(command);
    }
}
