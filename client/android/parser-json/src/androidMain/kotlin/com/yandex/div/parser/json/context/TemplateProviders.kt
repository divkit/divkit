package com.yandex.div.parser.json.context

import com.yandex.div.core.json.JsonObject
import com.yandex.div.data.EntityTemplate
import com.yandex.div.json.templates.TemplateProvider
import com.yandex.div.parser.json.templateNotFound

internal fun <T : EntityTemplate<*>> TemplateProvider<T>.getOrThrow(templateId: String, json: JsonObject): T {
    return get(templateId) ?: throw templateNotFound(json, templateId)
}
