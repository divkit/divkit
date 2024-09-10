package com.yandex.div.serialization

import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.data.EntityTemplate

@ExperimentalApi
interface TemplateDeserializer<T : EntityTemplate<*>, R> : Deserializer<T, R> {

    override fun deserialize(context: ParsingContext, data: R): T {
        return deserialize(context, null, data)
    }

    fun deserialize(context: ParsingContext, parent: T?, data: R): T
}
