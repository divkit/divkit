package com.yandex.div.core.log

import com.yandex.div.core.annotations.InternalApi

@Suppress("TooManyFunctions")
@InternalApi
public object Log {

    @JvmStatic
    @field:Volatile
    public var isEnabled: Boolean = false
        private set

    @field:Volatile
    public var severity: Severity = Severity.VERBOSE

    @JvmStatic
    public fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
    }

    @JvmStatic
    public fun v(tag: String, message: String) {
        if (isEnabledAt(Severity.VERBOSE)) {
            PlatformLog.v(tag, message)
        }
    }

    @JvmStatic
    public fun v(tag: String, message: String, throwable: Throwable) {
        if (isEnabledAt(Severity.VERBOSE)) {
            PlatformLog.v(tag, message, throwable)
        }
    }

    public inline fun v(tag: String, message: () -> String) {
        if (isEnabledAt(Severity.VERBOSE)) {
            PlatformLog.v(tag, message())
        }
    }

    public inline fun v(tag: String, throwable: Throwable, message: () -> String) {
        if (isEnabledAt(Severity.VERBOSE)) {
            PlatformLog.v(tag, message(), throwable)
        }
    }

    @JvmStatic
    public fun d(tag: String, message: String) {
        if (isEnabledAt(Severity.DEBUG)) {
            PlatformLog.d(tag, message)
        }
    }

    @JvmStatic
    public fun d(tag: String, message: String, throwable: Throwable) {
        if (isEnabledAt(Severity.DEBUG)) {
            PlatformLog.d(tag, message, throwable)
        }
    }

    public inline fun d(tag: String, message: () -> String) {
        if (isEnabledAt(Severity.DEBUG)) {
            PlatformLog.d(tag, message())
        }
    }

    public inline fun d(tag: String, throwable: Throwable, message: () -> String) {
        if (isEnabledAt(Severity.DEBUG)) {
            PlatformLog.d(tag, message(), throwable)
        }
    }

    @JvmStatic
    public fun i(tag: String, message: String) {
        if (isEnabledAt(Severity.INFO)) {
            PlatformLog.i(tag, message)
        }
    }

    @JvmStatic
    public fun i(tag: String, message: String, throwable: Throwable) {
        if (isEnabledAt(Severity.INFO)) {
            PlatformLog.i(tag, message, throwable)
        }
    }

    public inline fun i(tag: String, message: () -> String) {
        if (isEnabledAt(Severity.INFO)) {
            PlatformLog.i(tag, message())
        }
    }

    public inline fun i(tag: String, throwable: Throwable, message: () -> String) {
        if (isEnabledAt(Severity.INFO)) {
            PlatformLog.i(tag, message(), throwable)
        }
    }

    @JvmStatic
    public fun w(tag: String, message: String) {
        if (isEnabledAt(Severity.WARNING)) {
            PlatformLog.w(tag, message)
        }
    }

    @JvmStatic
    public fun w(tag: String, message: String, throwable: Throwable) {
        if (isEnabledAt(Severity.WARNING)) {
            PlatformLog.w(tag, message, throwable)
        }
    }

    public inline fun w(tag: String, message: () -> String) {
        if (isEnabledAt(Severity.WARNING)) {
            PlatformLog.w(tag, message())
        }
    }

    public inline fun w(tag: String, throwable: Throwable, message: () -> String) {
        if (isEnabledAt(Severity.WARNING)) {
            PlatformLog.w(tag, message(), throwable)
        }
    }

    @JvmStatic
    public fun e(tag: String, message: String) {
        if (isEnabledAt(Severity.ERROR)) {
            PlatformLog.e(tag, message)
        }
    }

    @JvmStatic
    public fun e(tag: String, message: String, throwable: Throwable) {
        if (isEnabledAt(Severity.ERROR)) {
            PlatformLog.e(tag, message, throwable)
        }
    }

    public inline fun e(tag: String, message: () -> String) {
        if (isEnabledAt(Severity.ERROR)) {
            PlatformLog.e(tag, message())
        }
    }

    public inline fun e(tag: String, throwable: Throwable, message: () -> String) {
        if (isEnabledAt(Severity.ERROR)) {
            PlatformLog.e(tag, message(), throwable)
        }
    }

    @JvmStatic
    public fun print(severity: Severity, tag: String, message: String) {
        when (severity) {
            Severity.VERBOSE -> v(tag, message)
            Severity.DEBUG -> d(tag, message)
            Severity.INFO -> i(tag, message)
            Severity.WARNING -> w(tag, message)
            Severity.ERROR -> e(tag, message)
        }
    }

    @JvmStatic
    public fun print(severity: Severity, tag: String, message: String, throwable: Throwable) {
        when (severity) {
            Severity.VERBOSE -> v(tag, message, throwable)
            Severity.DEBUG -> d(tag, message, throwable)
            Severity.INFO -> i(tag, message, throwable)
            Severity.WARNING -> w(tag, message, throwable)
            Severity.ERROR -> e(tag, message, throwable)
        }
    }

    public inline fun print(severity: Severity, tag: String, message: () -> String) {
        if (isEnabledAt(severity)) {
            print(severity, tag, message())
        }
    }

    public inline fun print(severity: Severity, tag: String, throwable: Throwable, message: () -> String) {
        if (isEnabledAt(severity)) {
            print(severity, tag, message(), throwable)
        }
    }

    @PublishedApi
    internal fun isEnabledAt(minLevel: Severity): Boolean {
        return isEnabled && severity.isAtLeast(minLevel)
    }
}
