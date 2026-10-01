package com.yandex.div.internal

import com.yandex.div.logging.Severity
import com.yandex.div.logging.toCoreSeverity
import com.yandex.div.logging.toSeverity
import com.yandex.div.core.log.Log as CoreLog

public object Log {

    @JvmStatic
    public val isEnabled: Boolean
        get() {
            return CoreLog.isEnabled
        }

    public var severity: Severity
        get() = CoreLog.severity.toSeverity()
        set(value) {
            CoreLog.severity = value.toCoreSeverity()
        }

    @JvmStatic
    public fun setEnabled(enabled: Boolean?) {
        CoreLog.setEnabled(enabled ?: false)
    }

    @JvmStatic
    public fun d(tag: String, message: String) {
        if (isAtLeast(Severity.DEBUG)) {
            CoreLog.d(tag, message)
        }
    }

    public fun w(tag: String, message: String) {
        if (isAtLeast(Severity.WARNING)) {
            CoreLog.w(tag, message)
        }
    }

    public fun w(tag: String, th: Throwable) {
        if (isAtLeast(Severity.WARNING)) {
            CoreLog.w(tag, "", th)
        }
    }

    public fun w(tag: String, message: String, th: Throwable) {
        if (isAtLeast(Severity.WARNING)) {
            CoreLog.w(tag, message, th)
        }
    }

    public fun i(tag: String, message: String) {
        if (isAtLeast(Severity.INFO)) {
            CoreLog.i(tag, message)
        }
    }

    @JvmStatic
    public fun e(tag: String, message: String) {
        if (isAtLeast(Severity.ERROR)) {
            CoreLog.e(tag, message)
        }
    }

    @JvmStatic
    public fun e(tag: String, message: String, th: Throwable) {
        if (isAtLeast(Severity.ERROR)) {
            CoreLog.e(tag, message, th)
        }
    }

    internal fun isAtLeast(minLevel: Severity): Boolean {
        return isEnabled && severity.isAtLeast(minLevel)
    }
}
