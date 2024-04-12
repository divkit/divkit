package com.yandex.div.internal.util;

import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.yandex.div.core.annotations.InternalApi;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * Defines what clock we use, since Android has couple of them. Additionally it's a unit-test assemble point.
 */
@InternalApi
public class Clock {
    @NonNull
    private static Clock sDefault = new Clock();

    @Inject
    public Clock() {
    }

    @NonNull
    public static Clock get() {
        return sDefault;
    }

    @VisibleForTesting
    public static void setForTests(@Nullable Clock clock) {
        sDefault = clock != null ? clock : new Clock();
    }

    /**
     * @return amount of seconds passed by since January 1, 1970 UTC.
     */
    public long getCurrentUnixTimestamp() {
        return TimeUnit.MILLISECONDS.toSeconds(getCurrentTimeMs());
    }

    public long getCurrentTimeMs() {
        return System.currentTimeMillis();
    }

    public long getUptimeMillis() {
        return SystemClock.uptimeMillis();
    }

    public long getElapsedRealtimeMs() {
        return SystemClock.elapsedRealtime();
    }
}
