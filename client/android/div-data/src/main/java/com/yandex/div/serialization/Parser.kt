package com.yandex.div.serialization

import com.yandex.div.core.annotations.ExperimentalApi

@ExperimentalApi
interface Parser<D, V> : Serializer<D, V>, Deserializer<D, V>
