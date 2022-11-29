package com.yandex.div.internal;

import androidx.annotation.NonNull;
import com.yandex.div.logging.BuildConfig;

public class Log {

    private static volatile boolean sEnabled = false;

    private Log() {
    }

    public static boolean isEnabled() {
        if (BuildConfig.DISABLE_LOGS) {
            return false;
        }
        return sEnabled;
    }

    public static void setEnabled(Boolean enabled) {
        sEnabled = enabled;
    }

    public static void v(@NonNull String tag, @NonNull String message) {
        if (isEnabled()) {
            android.util.Log.v(tag, message);
        }
    }

    public static void v(@NonNull String tag, @NonNull String message, @NonNull Throwable th) {
        if (isEnabled()) {
            android.util.Log.v(tag, message, th);
        }
    }

    public static void d(@NonNull String tag, @NonNull String message) {
        if (isEnabled()) {
            android.util.Log.d(tag, message);
        }
    }

    public static void d(@NonNull String tag, @NonNull String message, @NonNull Throwable th) {
        if (isEnabled()) {
            android.util.Log.d(tag, message, th);
        }
    }

    public static void w(@NonNull String tag, @NonNull String message) {
        if (isEnabled()) {
            android.util.Log.w(tag, message);
        }
    }

    public static void w(@NonNull String tag, @NonNull Throwable th) {
        if (isEnabled()) {
            android.util.Log.w(tag, th);
        }
    }

    public static void w(@NonNull String tag, @NonNull String message, @NonNull Throwable th) {
        if (isEnabled()) {
            android.util.Log.w(tag, message, th);
        }
    }

    public static void i(@NonNull String tag, @NonNull String message) {
        if (isEnabled()) {
            android.util.Log.i(tag, message);
        }
    }

    public static void i(@NonNull String tag, @NonNull String message, @NonNull Throwable th) {
        if (isEnabled()) {
            android.util.Log.i(tag, message, th);
        }
    }

    public static void e(@NonNull String tag, @NonNull String message) {
        if (isEnabled()) {
            android.util.Log.e(tag, message);
        }
    }

    public static void e(@NonNull String tag, @NonNull String message, @NonNull Throwable th) {
        if (isEnabled()) {
            android.util.Log.e(tag, message, th);
        }
    }
}
