package com.yandex.div.parser.json

import androidx.collection.arrayMapOf
import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.core.json.JsonObject
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.templates.CachingTemplateProvider
import com.yandex.div.json.templates.InMemoryTemplateProvider
import com.yandex.div.json.templates.TemplateProvider
import com.yandex.div.parser.json.logger.TemplateParsingErrorLogger
import com.yandex.div.serialization.ParsingContext
import com.yandex.div.serialization.TemplateParsingResult
import com.yandex.div2.DivData
import com.yandex.div2.DivPatch
import com.yandex.div2.DivTemplate

@ExperimentalApi
public class DivKitJsonParser private constructor(
    private val parserComponent: JsonParserComponent,
    private val context: CachingParsingContextImpl
) {

    public fun deserializeData(data: JsonObject): DivData {
        return parserComponent.divDataJsonEntityParser.value.deserialize(context, data)
    }

    public fun serializeData(value: DivData): JsonObject {
        return parserComponent.divDataJsonEntityParser.value.serialize(context, value)
    }

    public fun deserializePatch(data: JsonObject): DivPatch {
        return parserComponent.divPatchJsonEntityParser.value.deserialize(context, data)
    }

    public fun serializePatch(value: DivPatch): JsonObject {
        return parserComponent.divPatchJsonEntityParser.value.serialize(context, value)
    }

    public fun parseTemplates(data: JsonObject): TemplateParsingResult<DivTemplate> {
        val templates = arrayMapOf<String, DivTemplate>()
        val templateDependencies = arrayMapOf<String, Set<String>>()
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
                        .deserialize(templateContext, data.getObject(name))
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

    public fun retainTemplates(data: JsonObject): TemplateParsingResult<DivTemplate> {
        val result = parseTemplates(data)
        context.templates.putAll(result.templates)
        return result
    }

    public class Builder {

        private var templateProvider: CachingTemplateProvider<DivTemplate>? = null
        private var logger: ParsingErrorLogger = ParsingErrorLogger.LOG

        public fun logger(logger: ParsingErrorLogger): Builder {
            this.logger = logger
            return this
        }

        public fun build(): DivKitJsonParser {
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
