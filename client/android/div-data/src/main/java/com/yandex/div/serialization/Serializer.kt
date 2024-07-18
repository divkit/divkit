package com.yandex.div.serialization

import com.yandex.div.core.annotations.ExperimentalApi

@ExperimentalApi
interface Serializer<T, R> {
    fun serialize(context: ParsingContext, value: T) : R
}
