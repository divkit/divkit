package com.yandex.generator

import com.yandex.div.json.ParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.TemplateParsingEnvironment
import com.yandex.testing.EntityTemplate
import org.json.JSONObject

internal class EntityParsingEnvironment(
    logger: ParsingErrorLogger
) : TemplateParsingEnvironment<EntityTemplate>(logger) {

    override val templateFactory = object: TemplateFactory<EntityTemplate> {

        override fun create(env: ParsingEnvironment, topLevel: Boolean, json: JSONObject): EntityTemplate {
            return EntityTemplate(env, topLevel, json)
        }
    }
}

internal val LOG_ENVIRONMENT = EntityParsingEnvironment(logger = ParsingErrorLogger.LOG)
internal val ASSERT_ENVIRONMENT = EntityParsingEnvironment(logger = ParsingErrorLogger.ASSERT)
