package com.yandex.div.internal.util

import javax.inject.Provider

class DoubleCheckProvider<T>(init: () -> T) : Provider<T> {

    private val value: T by lazy(init)

    override fun get(): T = value
}
