package com.yandex.div.json.templates

import com.yandex.div.json.JsonTemplate

/**
 * Mixes in-memory and persistent providers to provide best performance.
 */
open class MainTemplateProvider<T: JsonTemplate<*>>(
    private val inMemoryProvider: InMemoryTemplateProvider<T>,
    private var dbProvider: TemplateProvider<T>,
) : TemplateProvider<T> {

    override fun get(templateId: String): T? {
        inMemoryProvider[templateId]?.let {
            return it
        }

        dbProvider[templateId]?.let {
            inMemoryProvider.put(templateId, it)
            return it
        }

        return null
    }

    fun takeSnapshot(target: MutableMap<String, T>) {
        inMemoryProvider.takeSnapshot(target)
    }

    fun putAll(parsed: Map<String, T>) {
        parsed.forEach { inMemoryProvider.put(it.key, it.value) }
    }
}
