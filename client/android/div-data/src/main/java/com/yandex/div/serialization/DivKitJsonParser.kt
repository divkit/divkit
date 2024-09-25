package com.yandex.div.serialization

import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.internal.parser.JsonTopologicalSorting
import com.yandex.div.internal.parser.TemplateParsingErrorLogger
import com.yandex.div.internal.util.arrayMap
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.ParsingException
import com.yandex.div.json.templates.CachingTemplateProvider
import com.yandex.div.json.templates.InMemoryTemplateProvider
import com.yandex.div.json.templates.TemplateProvider
import com.yandex.div2.DivData
import com.yandex.div2.DivPatch
import com.yandex.div2.DivTemplate
import com.yandex.div2.JsonParserComponent
import org.json.JSONObject

@ExperimentalApi
class DivKitJsonParser private constructor(
    private val parserComponent: JsonParserComponent,
    private val context: CachingParsingContextImpl
) {

    fun deserializeData(data: JSONObject): DivData {
        return parserComponent.divDataJsonEntityParser.value.deserialize(context, data)
    }

    fun serializeData(value: DivData): JSONObject {
        return parserComponent.divDataJsonEntityParser.value.serialize(context, value)
    }

    fun deserializePatch(data: JSONObject): DivPatch {
        return parserComponent.divPatchJsonEntityParser.value.deserialize(context, data)
    }

    fun serializePatch(value: DivPatch): JSONObject {
        return parserComponent.divPatchJsonEntityParser.value.serialize(context, value)
    }

    fun parseTemplates(data: JSONObject): TemplateParsingResult<DivTemplate> {
        val templates = arrayMap<String, DivTemplate>()
        val templateDependencies = arrayMap<String, Set<String>>()
        try {
            val orderedNamesWithDependencies = JsonTopologicalSorting.sort(context, data)
            context.templates.takeSnapshot(templates)
            val templateProvider = TemplateProvider.wrap(templates)

            orderedNamesWithDependencies.forEach { (name, dependencies) ->
                val templateContext = ParsingContextImpl(
                    templates = templateProvider,
                    logger = TemplateParsingErrorLogger(context.logger, name)
                )
                try {
                    val template = parserComponent.divJsonTemplateParser.value
                        .deserialize(templateContext, data.getJSONObject(name))
                    templates[name] = template
                    if (dependencies.isNotEmpty()) {
                        templateDependencies[name] = dependencies
                    }
                } catch (e: ParsingException) {
                    context.logger.logTemplateError(e, name)
                }
            }
        } catch (e: Exception) {
            context.logger.logError(e)
        }

        return TemplateParsingResult(templates, templateDependencies)
    }

    fun retainTemplates(data: JSONObject): TemplateParsingResult<DivTemplate> {
        val result = parseTemplates(data)
        context.templates.putAll(result.templates)
        return result
    }

    class Builder {

        private var templateProvider: CachingTemplateProvider<DivTemplate>? = null
        private var logger: ParsingErrorLogger = ParsingErrorLogger.LOG

        fun logger(logger: ParsingErrorLogger): Builder {
            this.logger = logger
            return this
        }

        fun build(): DivKitJsonParser {
            return DivKitJsonParser(
                parserComponent = JsonParserComponent(),
                context = CachingParsingContextImpl(
                    templates = templateProvider ?: CachingTemplateProvider(
                        InMemoryTemplateProvider(),
                        TemplateProvider.empty()
                    ),
                    logger = logger
                )
            )
        }
    }

    private class ParsingContextImpl(
        override val templates: TemplateProvider<DivTemplate>,
        override val logger: ParsingErrorLogger
    ) : ParsingContext

    private class CachingParsingContextImpl(
        override val templates: CachingTemplateProvider<DivTemplate>,
        override val logger: ParsingErrorLogger
    ) : ParsingContext
}
