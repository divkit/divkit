package com.yandex.div.serialization

import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.data.EntityTemplate
import com.yandex.div.data.R

@ExperimentalApi
interface TemplateResolver<D, T : EntityTemplate<D>, R> {
    fun resolve(context: ParsingContext, template: T, data: R) : D
}
