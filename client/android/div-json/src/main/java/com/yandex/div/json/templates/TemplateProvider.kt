package com.yandex.div.json.templates

import com.yandex.div.json.JsonTemplate
import com.yandex.div.json.ParsingException
import com.yandex.div.json.templateNotFound
import org.json.JSONObject

interface TemplateProvider<out T : JsonTemplate<*>> {

    operator fun get(templateId: String): T?

    @Throws(ParsingException::class)
    fun getOrThrow(templateId: String, json: JSONObject): T {
        val template = get(templateId)
            ?: throw templateNotFound(json = json, templateId = templateId)

        return template as T
    }

    companion object {
        fun <T : JsonTemplate<*>> empty() = object : TemplateProvider<T> {
            override fun get(templateId: String): T? = null
        }

        fun <T : JsonTemplate<*>> wrap(map: Map<String, T>) = object : TemplateProvider<T> {
            override fun get(templateId: String): T? {
                return map[templateId]
            }
        }
    }
}
