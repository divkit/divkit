package com.yandex.div.internal.util;

import android.os.Build;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.yandex.div.core.annotations.InternalApi;

/**
 * Defines what clock we use, since Android has couple of them. Additionally it's a unit-test assemble point.
 */
@InternalApi
public class Clock {
    @NonNull
    private static Clock sDefault = new Clock();

    @NonNull
    public static Clock get() {
        return sDefault;
    }

    @VisibleForTesting
    public static void setForTests(@Nullable Clock clock) {
        sDefault = clock != null ? clock : new Clock();
    }

    public long getCurrentTimeMs() {
        return System.currentTimeMillis();
    }

    /**
     * @return current uptime in microseconds.
     */
    public long getUptimeMicros() {
        long nanoTime;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            nanoTime = System.nanoTime();
        } else {
            nanoTime = SystemClock.uptimeNanos();
        }
        return nanoTime / 1000L;
    }
}
