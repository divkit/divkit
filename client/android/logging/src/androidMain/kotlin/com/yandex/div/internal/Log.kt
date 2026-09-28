package com.yandex.div.internal;

import com.yandex.div.logging.BuildKonfig
import com.yandex.div.logging.Severity

public object Log {

    @Volatile
    private var _isEnabled = false

    @Volatile
    private var _severity = Severity.VERBOSE

    @JvmStatic
    public val isEnabled: Boolean
        get() {
            if (BuildKonfig.DISABLE_LOGS) {
                return false
            }
            return _isEnabled
        }

    public var severity: Severity
        get() = _severity
        set(value) {
            _severity = value
        }

    @JvmStatic
    public fun setEnabled(enabled: Boolean?) {
        _isEnabled = enabled ?: false
    }

    @JvmStatic
    public fun d(tag: String, message: String) {
        if (isAtLeast(Severity.DEBUG)) {
            android.util.Log.d(tag, message);
        }
    }

    public fun w(tag: String, message: String) {
        if (isAtLeast(Severity.WARNING)) {
            android.util.Log.w(tag, message);
        }
    }

    public fun w(tag: String, th: Throwable) {
        if (isAtLeast(Severity.WARNING)) {
            android.util.Log.w(tag, th);
        }
    }

    public fun w(tag: String, message: String, th: Throwable) {
        if (isAtLeast(Severity.WARNING)) {
            android.util.Log.w(tag, message, th);
        }
    }

    public fun i(tag: String, message: String) {
        if (isAtLeast(Severity.INFO)) {
            android.util.Log.i(tag, message);
        }
    }

    @JvmStatic
    public fun e(tag: String, message: String) {
        if (isAtLeast(Severity.ERROR)) {
            android.util.Log.e(tag, message);
        }
    }

    @JvmStatic
    public fun e(tag: String, message: String, th: Throwable) {
        if (isAtLeast(Severity.ERROR)) {
            android.util.Log.e(tag, message, th);
        }
    }

    internal fun isAtLeast(minLevel: Severity): Boolean {
        if (!isEnabled) {
            return false
        }
        return _severity.isAtLeast(minLevel)
    }
}
