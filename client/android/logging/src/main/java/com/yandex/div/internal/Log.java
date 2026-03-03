package com.yandex.div.internal;

import androidx.annotation.NonNull;
import com.yandex.div.logging.BuildConfig;
import com.yandex.div.logging.Severity;

public class Log {

    private static volatile boolean sEnabled = false;
    private static volatile Severity sSeverity = Severity.VERBOSE;

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

    public static Severity getSeverity() {
        return sSeverity;
    }

    public static void setSeverity(@NonNull Severity severity) {
        sSeverity = severity;
    }

    public static void v(@NonNull String tag, @NonNull String message) {
        if (isAtLeast(Severity.VERBOSE)) {
            android.util.Log.v(tag, message);
        }
    }

    public static void v(@NonNull String tag, @NonNull String message, @NonNull Throwable th) {
        if (isAtLeast(Severity.VERBOSE)) {
            android.util.Log.v(tag, message, th);
        }
    }

    public static void d(@NonNull String tag, @NonNull String message) {
        if (isAtLeast(Severity.DEBUG)) {
            android.util.Log.d(tag, message);
        }
    }

    public static void d(@NonNull String tag, @NonNull String message, @NonNull Throwable th) {
        if (isAtLeast(Severity.DEBUG)) {
            android.util.Log.d(tag, message, th);
        }
    }

    public static void w(@NonNull String tag, @NonNull String message) {
        if (isAtLeast(Severity.WARNING)) {
            android.util.Log.w(tag, message);
        }
    }

    public static void w(@NonNull String tag, @NonNull Throwable th) {
        if (isAtLeast(Severity.WARNING)) {
            android.util.Log.w(tag, th);
        }
    }

    public static void w(@NonNull String tag, @NonNull String message, @NonNull Throwable th) {
        if (isAtLeast(Severity.WARNING)) {
            android.util.Log.w(tag, message, th);
        }
    }

    public static void i(@NonNull String tag, @NonNull String message) {
        if (isAtLeast(Severity.INFO)) {
            android.util.Log.i(tag, message);
        }
    }

    public static void i(@NonNull String tag, @NonNull String message, @NonNull Throwable th) {
        if (isAtLeast(Severity.INFO)) {
            android.util.Log.i(tag, message, th);
        }
    }

    public static void e(@NonNull String tag, @NonNull String message) {
        if (isAtLeast(Severity.ERROR)) {
            android.util.Log.e(tag, message);
        }
    }

    public static void e(@NonNull String tag, @NonNull String message, @NonNull Throwable th) {
        if (isAtLeast(Severity.ERROR)) {
            android.util.Log.e(tag, message, th);
        }
    }

    static boolean isAtLeast(Severity minLevel) {
        if (!isEnabled()) {
            return false;
        }

        return sSeverity.isAtLeast(minLevel);
    }
}
