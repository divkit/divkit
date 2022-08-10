package com.yandex.div.data

import com.yandex.div.json.ParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.TemplateParsingEnvironment
import com.yandex.div.json.templates.InMemoryTemplateProvider
import com.yandex.div.json.templates.MainTemplateProvider
import com.yandex.div.json.templates.TemplateProvider
import com.yandex.div2.DivTemplate
import org.json.JSONException
import org.json.JSONObject

class DivParsingEnvironment @JvmOverloads constructor(
    logger: ParsingErrorLogger,
    mainTemplateProvider: MainTemplateProvider<DivTemplate> = MainTemplateProvider(
        InMemoryTemplateProvider(),
        TemplateProvider.empty(),
    )
) : TemplateParsingEnvironment<DivTemplate>(logger, mainTemplateProvider) {
    override val templates: MainTemplateProvider<DivTemplate> = mainTemplateProvider
    override val templateFactory = object: TemplateFactory<DivTemplate> {

        @Throws(JSONException::class)
        override fun create(env: ParsingEnvironment, topLevel: Boolean, json: JSONObject): DivTemplate {
            return DivTemplate(env, topLevel, json)
        }
    }
}
