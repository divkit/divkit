package com.yandex.div.core.log

@PublishedApi
internal expect object PlatformLog {
    fun v(tag: String, message: String)
    fun v(tag: String, message: String, throwable: Throwable)
    fun d(tag: String, message: String)
    fun d(tag: String, message: String, throwable: Throwable)
    fun i(tag: String, message: String)
    fun i(tag: String, message: String, throwable: Throwable)
    fun w(tag: String, message: String)
    fun w(tag: String, message: String, throwable: Throwable)
    fun e(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable)
}
