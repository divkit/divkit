package com.yandex.div.parser.json.context

import com.yandex.div.json.JsonTemplate
import com.yandex.div.json.ParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.templates.TemplateProvider

internal class ParsingEnvironmentImpl(
    override val templates: TemplateProvider<JsonTemplate<*>>,
    override val logger: ParsingErrorLogger
) : ParsingEnvironment
