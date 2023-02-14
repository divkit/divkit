package com.yandex.div.data

import com.yandex.div.core.annotations.Mockable
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.TemplateParsingEnvironment
import com.yandex.div.json.TemplateParsingEnvironment.TemplateFactory
import com.yandex.div.json.templates.CachingTemplateProvider
import com.yandex.div.json.templates.InMemoryTemplateProvider
import com.yandex.div.json.templates.TemplateProvider
import com.yandex.div2.DivTemplate

@Mockable
class DivParsingEnvironment @JvmOverloads constructor(
    logger: ParsingErrorLogger,
    templateProvider: CachingTemplateProvider<DivTemplate> = CachingTemplateProvider(
        InMemoryTemplateProvider(),
        TemplateProvider.empty(),
    )
) : TemplateParsingEnvironment<DivTemplate>(logger, templateProvider) {

    override val templates: CachingTemplateProvider<DivTemplate> = templateProvider

    override val templateFactory = TemplateFactory { env, topLevel, json -> DivTemplate(env, topLevel, json) }
}
