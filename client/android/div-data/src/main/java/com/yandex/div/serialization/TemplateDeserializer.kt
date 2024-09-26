package com.yandex.div.serialization

import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.data.EntityTemplate

@ExperimentalApi
interface TemplateDeserializer<D, T : EntityTemplate<*>> : Deserializer<D, T> {

    override fun deserialize(context: ParsingContext, data: D): T {
        return deserialize(context, null, data)
    }

    fun deserialize(context: ParsingContext, parent: T?, data: D): T
}
