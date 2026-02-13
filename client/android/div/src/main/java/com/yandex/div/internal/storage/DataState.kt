package com.yandex.div.internal.storage

internal sealed interface DataState<T> {

    object Initial : DataState<Any>

    class WithData<T>(val value: T) : DataState<T>

    class WithException<T>(val exception: Throwable) : DataState<T>

    object Finalized : DataState<Any>
}
