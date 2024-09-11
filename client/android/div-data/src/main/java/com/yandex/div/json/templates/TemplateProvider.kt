package com.yandex.div.json.templates

import com.yandex.div.data.EntityTemplate
import com.yandex.div.json.ParsingException
import com.yandex.div.json.templateNotFound
import org.json.JSONObject

interface TemplateProvider<out T : EntityTemplate<*>> {

    operator fun get(templateId: String): T?

    @Throws(ParsingException::class)
    fun getOrThrow(templateId: String, json: JSONObject): T {
        val template = get(templateId)
            ?: throw templateNotFound(json = json, templateId = templateId)

        return template as T
    }

    companion object {
        fun <T : EntityTemplate<*>> empty() = object : TemplateProvider<T> {
            override fun get(templateId: String): T? = null
        }

        fun <T : EntityTemplate<*>> wrap(map: Map<String, T>) = object : TemplateProvider<T> {
            override fun get(templateId: String): T? {
                return map[templateId]
            }
        }
    }
}
