package com.yandex.div.serialization

import com.yandex.div.core.annotations.ExperimentalApi

@ExperimentalApi
interface Serializer<D, V> {
    fun serialize(context: ParsingContext, value: V) : D
}
