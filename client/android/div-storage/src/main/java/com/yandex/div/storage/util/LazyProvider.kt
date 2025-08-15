package com.yandex.div.storage.util

import javax.inject.Provider

internal class LazyProvider<T>(init: () -> T) : Provider<T> {

    private val value: T by lazy(init)

    override fun get(): T = value
}
