package com.yandex.div.internal.util

import com.yandex.div.core.annotations.InternalApi
import javax.inject.Provider

@InternalApi
public class DoubleCheckProvider<T>(init: () -> T) : Provider<T> {

    private val value: T by lazy(init)

    override fun get(): T = value
}
