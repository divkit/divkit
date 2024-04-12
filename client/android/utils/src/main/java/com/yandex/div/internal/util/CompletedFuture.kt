package com.yandex.div.internal.util

import com.yandex.div.core.annotations.InternalApi
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

@InternalApi
public class CompletedFuture<T>(private val value: T) : Future<T> {
    override fun isDone(): Boolean = true

    override fun get(): T = value

    override fun get(timeout: Long, unit: TimeUnit): T = value

    override fun cancel(mayInterruptIfRunning: Boolean): Boolean = true

    override fun isCancelled(): Boolean = false
}
