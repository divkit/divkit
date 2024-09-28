package com.yandex.div.serialization

import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.data.EntityTemplate

@ExperimentalApi
interface TemplateResolver<D, T : EntityTemplate<V>, V> {
    fun resolve(context: ParsingContext, template: T, data: D) : V
}
