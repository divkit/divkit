package com.yandex.generator

import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.TemplateParsingEnvironment
import com.yandex.div.json.TemplateParsingEnvironment.TemplateFactory
import com.yandex.testing.EntityTemplate

internal class EntityParsingEnvironment(
    logger: ParsingErrorLogger
) : TemplateParsingEnvironment<EntityTemplate>(logger) {

    override val templateFactory = TemplateFactory { env, topLevel, json -> EntityTemplate(env, topLevel, json) }
}

internal val LOG_ENVIRONMENT = EntityParsingEnvironment(logger = ParsingErrorLogger.LOG)
internal val ASSERT_ENVIRONMENT = EntityParsingEnvironment(logger = ParsingErrorLogger.ASSERT)
