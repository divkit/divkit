package com.yandex.div.json.templates

import com.yandex.div.json.JsonTemplate
import com.yandex.div.util.arrayMap

class InMemoryTemplateProvider<T: JsonTemplate<*>> : TemplateProvider<T> {
    private val templatesMap: MutableMap<String, T> = arrayMap()

    override fun get(templateId: String) = templatesMap[templateId]

    internal fun put(templateId: String, jsonTemplate: T) {
        templatesMap[templateId] = jsonTemplate
    }

    internal fun takeSnapshot(target: MutableMap<String, T>) {
        target.putAll(templatesMap)
    }
}
