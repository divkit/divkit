package com.yandex.div.core.log

import java.util.logging.Level
import java.util.logging.Logger

@PublishedApi
internal actual object PlatformLog {

    actual fun v(tag: String, message: String) {
        Logger.getLogger(tag).log(Level.FINEST, message)
    }

    actual fun v(tag: String, message: String, throwable: Throwable) {
        Logger.getLogger(tag).log(Level.FINEST, message, throwable)
    }

    actual fun d(tag: String, message: String) {
        Logger.getLogger(tag).log(Level.FINE, message)
    }

    actual fun d(tag: String, message: String, throwable: Throwable) {
        Logger.getLogger(tag).log(Level.FINE, message, throwable)
    }

    actual fun i(tag: String, message: String) {
        Logger.getLogger(tag).log(Level.INFO, message)
    }

    actual fun i(tag: String, message: String, throwable: Throwable) {
        Logger.getLogger(tag).log(Level.INFO, message, throwable)
    }

    actual fun w(tag: String, message: String) {
        Logger.getLogger(tag).log(Level.WARNING, message)
    }

    actual fun w(tag: String, message: String, throwable: Throwable) {
        Logger.getLogger(tag).log(Level.WARNING, message, throwable)
    }

    actual fun e(tag: String, message: String) {
        Logger.getLogger(tag).log(Level.SEVERE, message)
    }

    actual fun e(tag: String, message: String, throwable: Throwable) {
        Logger.getLogger(tag).log(Level.SEVERE, message, throwable)
    }
}
