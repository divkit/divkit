package com.yandex.div.serialization

import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.data.EntityTemplate

@ExperimentalApi
interface TemplateParser<T : EntityTemplate<*>, R> : Serializer<T, R>, TemplateDeserializer<T, R>
