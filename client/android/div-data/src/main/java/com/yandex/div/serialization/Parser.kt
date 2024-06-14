package com.yandex.div.serialization

import com.yandex.div.core.annotations.ExperimentalApi

@ExperimentalApi
interface Parser<T, R> : Serializer<T, R>, Deserializer<T, R>
