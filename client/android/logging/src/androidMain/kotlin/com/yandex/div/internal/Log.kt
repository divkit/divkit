package com.yandex.div.internal;

import com.yandex.div.logging.BuildKonfig
import com.yandex.div.logging.Severity

object Log {

    @Volatile
    private var _isEnabled = false

    @Volatile
    private var _severity = Severity.VERBOSE

    @JvmStatic
    var isEnabled: Boolean
        get() {
            if (BuildKonfig.DISABLE_LOGS) {
                return false
            }
            return _isEnabled
        }
        set(value) {
            _isEnabled = value
        }

    var severity: Severity
        get() = _severity
        set(value) {
            _severity = value
        }

    @JvmStatic
    fun d(tag: String, message: String) {
        if (isAtLeast(Severity.DEBUG)) {
            android.util.Log.d(tag, message);
        }
    }

    fun w(tag: String, message: String) {
        if (isAtLeast(Severity.WARNING)) {
            android.util.Log.w(tag, message);
        }
    }

    fun w(tag: String, th: Throwable) {
        if (isAtLeast(Severity.WARNING)) {
            android.util.Log.w(tag, th);
        }
    }

    fun w(tag: String, message: String, th: Throwable) {
        if (isAtLeast(Severity.WARNING)) {
            android.util.Log.w(tag, message, th);
        }
    }

    fun i(tag: String, message: String) {
        if (isAtLeast(Severity.INFO)) {
            android.util.Log.i(tag, message);
        }
    }

    @JvmStatic
    fun e(tag: String, message: String) {
        if (isAtLeast(Severity.ERROR)) {
            android.util.Log.e(tag, message);
        }
    }

    @JvmStatic
    fun e(tag: String, message: String, th: Throwable) {
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
