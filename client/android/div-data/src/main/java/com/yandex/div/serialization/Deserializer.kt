package com.yandex.div.serialization

import com.yandex.div.core.annotations.ExperimentalApi

@ExperimentalApi
interface Deserializer<D, V> {
    fun deserialize(context: ParsingContext, data: D): V
}
