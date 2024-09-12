package com.yandex.div.serialization

import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.data.EntityTemplate
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.templates.TemplateProvider

@ExperimentalApi
interface ParsingContext {

    val templates: TemplateProvider<EntityTemplate<*>>

    val logger: ParsingErrorLogger

    val allowPropertyOverride: Boolean
        get() = true
}

private class ErrorCollectingParsingContext(
    private val baseContext: ParsingContext
) : ParsingContext by baseContext {

    val errors = mutableListOf<Exception>()

    override val logger = ParsingErrorLogger { error ->
        errors += error
        baseContext.logger.logError(error)
    }
}

internal fun ParsingContext.collectErrors(): ParsingContext {
    return if (this is ErrorCollectingParsingContext) this else ErrorCollectingParsingContext(this)
}

internal val ParsingContext.collectedErrors: List<Exception>
    get() {
        return if (this is ErrorCollectingParsingContext) {
            errors
        } else {
            emptyList()
        }
}

private class OverrideRestrictingParsingContext(
    val baseContext: ParsingContext
) : ParsingContext by baseContext {

    override val allowPropertyOverride: Boolean
        get() = false
}

internal fun ParsingContext.restrictPropertyOverride(): ParsingContext {
    return if (this is OverrideRestrictingParsingContext) this else OverrideRestrictingParsingContext(this)
}
