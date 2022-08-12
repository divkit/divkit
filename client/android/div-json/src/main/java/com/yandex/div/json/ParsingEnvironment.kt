package com.yandex.div.json

import com.yandex.div.json.templates.TemplateProvider

interface ParsingEnvironment {

    val templates: TemplateProvider<JsonTemplate<*>>
    val logger: ParsingErrorLogger
}
