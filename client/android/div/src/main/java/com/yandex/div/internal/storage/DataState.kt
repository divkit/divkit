package com.yandex.div.internal.storage

internal sealed interface DataState<T> {

    data object Initial : DataState<Any>

    class WithData<T>(val value: T) : DataState<T>

    class WithException<T>(val exception: Throwable) : DataState<T>

    data object Finalized : DataState<Any>
}
