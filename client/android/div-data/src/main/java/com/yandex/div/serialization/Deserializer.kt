package com.yandex.div.serialization

import com.yandex.div.core.annotations.ExperimentalApi

@ExperimentalApi
interface Deserializer<T, R> {
    fun deserialize(context: ParsingContext, data: R): T
}
