package com.yandex.div.serialization

import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.data.EntityTemplate

@ExperimentalApi
interface TemplateParser<D, T : EntityTemplate<*>> : Serializer<D, T>, TemplateDeserializer<D, T>
