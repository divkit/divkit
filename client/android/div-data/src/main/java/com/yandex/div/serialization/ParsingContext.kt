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

@ExperimentalApi
internal interface ParsingContextWrapper : ParsingContext {
    val baseContext: ParsingContext
}

private class ErrorCollectingParsingContext(
    override val baseContext: ParsingContext
) : ParsingContext by baseContext, ParsingContextWrapper {

    val errors = mutableListOf<Exception>()

    override val logger = ParsingErrorLogger { error ->
        errors += error
        baseContext.logger.logError(error)
    }
}

internal fun ParsingContext.collectingErrors(): ParsingContext {
    return if (this is ErrorCollectingParsingContext) this else ErrorCollectingParsingContext(this)
}

@Suppress("RecursivePropertyAccessor")
internal val ParsingContext.collectedErrors: List<Exception>
    get() = when (this) {
        is ErrorCollectingParsingContext -> errors
        is ParsingContextWrapper -> baseContext.collectedErrors
        else -> emptyList()
    }

private class OverrideRestrictingParsingContext(
    override val baseContext: ParsingContext
) : ParsingContext by baseContext, ParsingContextWrapper {

    override val allowPropertyOverride: Boolean
        get() = false
}

internal fun ParsingContext.restrictPropertyOverride(): ParsingContext {
    return if (this is OverrideRestrictingParsingContext) this else OverrideRestrictingParsingContext(this)
}
