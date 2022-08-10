package com.yandex.div.json

import com.yandex.div.json.templates.TemplateProvider

/**
 * Simple implementation of [ParsingEnvironment].
 */
internal class ParsingEnvironmentImpl(
    override val templates: TemplateProvider<JsonTemplate<*>>,
    override val logger: ParsingErrorLogger
) : ParsingEnvironment
