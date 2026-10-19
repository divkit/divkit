package com.yandex.div.parser.json.context

import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.templates.CachingTemplateProvider
import com.yandex.div.json.templates.InMemoryTemplateProvider
import com.yandex.div.json.templates.TemplateProvider
import com.yandex.div.parser.json.fromJson
import com.yandex.div2.DivTemplate

@ExperimentalApi
public class DivParsingEnvironment @JvmOverloads constructor(
    logger: ParsingErrorLogger,
    templateProvider: CachingTemplateProvider<DivTemplate> = CachingTemplateProvider(
        InMemoryTemplateProvider(),
        TemplateProvider.empty(),
    )
) : TemplateParsingEnvironment<DivTemplate>(logger, templateProvider) {

    override val templates: CachingTemplateProvider<DivTemplate> = templateProvider

    override val templateFactory: TemplateFactory<DivTemplate> =
        TemplateFactory { env, _, json -> DivTemplate.fromJson(env, json) }
}
