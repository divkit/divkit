package com.yandex.div.internal.parser

import com.yandex.div.json.JsonTemplate
import com.yandex.div.json.ParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.templates.TemplateProvider

/**
 * Simple implementation of [ParsingEnvironment].
 */
internal class ParsingEnvironmentImpl(
    override val templates: TemplateProvider<JsonTemplate<*>>,
    override val logger: ParsingErrorLogger
) : ParsingEnvironment
