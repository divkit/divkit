package com.yandex.div.internal.util;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.yandex.div.core.annotations.InternalApi;

@InternalApi
public abstract class NamedRunnable implements Runnable {
    @NonNull
    private final String mThreadSuffix;

    public NamedRunnable(@NonNull String threadSuffix) {
        mThreadSuffix = threadSuffix;
    }

    @Override
    public final void run() {
        String oldName = Thread.currentThread().getName();
        Thread.currentThread().setName(oldName + "-" + mThreadSuffix);
        try {
            execute();
        } finally {
            Thread.currentThread().setName(oldName);
        }
    }

    @WorkerThread
    public abstract void execute();
}
