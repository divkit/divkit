package com.yandex.div.core.log

import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.NSLog
import platform.Foundation.NSString
import platform.Foundation.create

@OptIn(BetaInteropApi::class)
@PublishedApi
internal actual object PlatformLog {

    actual fun v(tag: String, message: String) {
        NSLog("%@", NSString.create(string = "V/$tag: $message"))
    }

    actual fun v(tag: String, message: String, throwable: Throwable) {
        v(tag, "$message\n${throwable.stackTraceToString()}")
    }

    actual fun d(tag: String, message: String) {
        NSLog("%@", NSString.create(string = "D/$tag: $message"))
    }

    actual fun d(tag: String, message: String, throwable: Throwable) {
        d(tag, "$message\n${throwable.stackTraceToString()}")
    }

    actual fun i(tag: String, message: String) {
        NSLog("%@", NSString.create(string = "I/$tag: $message"))
    }

    actual fun i(tag: String, message: String, throwable: Throwable) {
        i(tag, "$message\n${throwable.stackTraceToString()}")
    }

    actual fun w(tag: String, message: String) {
        NSLog("%@", NSString.create(string = "W/$tag: $message"))
    }

    actual fun w(tag: String, message: String, throwable: Throwable) {
        w(tag, "$message\n${throwable.stackTraceToString()}")
    }

    actual fun e(tag: String, message: String) {
        NSLog("%@", NSString.create(string = "E/$tag: $message"))
    }

    actual fun e(tag: String, message: String, throwable: Throwable) {
        e(tag, "$message\n${throwable.stackTraceToString()}")
    }
}
