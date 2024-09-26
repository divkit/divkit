package com.yandex.div.serialization

import com.yandex.div.json.JsonTemplate
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.templates.TemplateProvider
import com.yandex.div2.JsonParserComponent

internal val builtInParsingContext = object : ParsingContext {

    override val templates = TemplateProvider.empty<JsonTemplate<*>>()

    override val logger: ParsingErrorLogger = ParsingErrorLogger.LOG
}

internal val builtInParserComponent = JsonParserComponent()
