package com.yandex.div.json.templates

import com.yandex.div.json.JsonTemplate

/**
 * Mixes in-memory and fallback (persistent in most cases) providers to provide best performance.
 */
open class CachingTemplateProvider<T: JsonTemplate<*>>(
    private val cacheProvider: InMemoryTemplateProvider<T>,
    private var fallbackProvider: TemplateProvider<T>,
) : TemplateProvider<T> {

    override fun get(templateId: String): T? {
        cacheProvider[templateId]?.let {
            return it
        }

        fallbackProvider[templateId]?.let {
            cacheProvider.put(templateId, it)
            return it
        }

        return null
    }

    fun takeSnapshot(target: MutableMap<String, T>) {
        cacheProvider.takeSnapshot(target)
    }

    fun putAll(parsed: Map<String, T>) {
        parsed.forEach { cacheProvider.put(it.key, it.value) }
    }
}
