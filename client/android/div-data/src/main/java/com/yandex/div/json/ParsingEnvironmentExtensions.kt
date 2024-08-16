package com.yandex.div.json

import com.yandex.div.json.templates.TemplateProvider

fun ParsingEnvironment.withLogger(logger: ParsingErrorLogger): ParsingEnvironmentWrapper {
    return ParsingEnvironmentWrapper(this, logger)
}

class ParsingEnvironmentWrapper(
    base: ParsingEnvironment,
    override val logger: ParsingErrorLogger
) : ParsingEnvironment {

    override val templates: TemplateProvider<JsonTemplate<*>>
        get() = _templates

    val requestedKeys: Set<String>
        get() = _templates.requestedKeys

    private val _templates = KeyWatchingTemplateProvider(base.templates)
}

private class KeyWatchingTemplateProvider(
    private val base: TemplateProvider<JsonTemplate<*>>
): TemplateProvider<JsonTemplate<*>> {

    val requestedKeys: Set<String>
        get() = _requestedKeys

    private val _requestedKeys = linkedSetOf<String>()

    override fun get(templateId: String): JsonTemplate<*>? {
        _requestedKeys.add(templateId)
        return base[templateId]
    }
}
