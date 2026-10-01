package com.yandex.div.core.log

import android.util.Log as AndroidLog

@PublishedApi
internal actual object PlatformLog {

    actual fun v(tag: String, message: String) {
        AndroidLog.v(tag, message)
    }

    actual fun v(tag: String, message: String, throwable: Throwable) {
        AndroidLog.v(tag, message, throwable)
    }

    actual fun d(tag: String, message: String) {
        AndroidLog.d(tag, message)
    }

    actual fun d(tag: String, message: String, throwable: Throwable) {
        AndroidLog.d(tag, message, throwable)
    }

    actual fun i(tag: String, message: String) {
        AndroidLog.i(tag, message)
    }

    actual fun i(tag: String, message: String, throwable: Throwable) {
        AndroidLog.i(tag, message, throwable)
    }

    actual fun w(tag: String, message: String) {
        AndroidLog.w(tag, message)
    }

    actual fun w(tag: String, message: String, throwable: Throwable) {
        AndroidLog.w(tag, message, throwable)
    }

    actual fun e(tag: String, message: String) {
        AndroidLog.e(tag, message)
    }

    actual fun e(tag: String, message: String, throwable: Throwable) {
        AndroidLog.e(tag, message, throwable)
    }
}
