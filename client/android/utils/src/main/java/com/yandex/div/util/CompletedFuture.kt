package com.yandex.div.util

import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class CompletedFuture<T>(private val value: T) : Future<T> {
    override fun isDone() = true

    override fun get() = value

    override fun get(timeout: Long, unit: TimeUnit) = value

    override fun cancel(mayInterruptIfRunning: Boolean) = true

    override fun isCancelled() = false
}
