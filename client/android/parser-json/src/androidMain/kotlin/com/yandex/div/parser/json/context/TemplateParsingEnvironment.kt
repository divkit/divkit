package com.yandex.div.parser.json.context

import androidx.collection.arrayMapOf
import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.core.json.JsonObject
import com.yandex.div.json.JsonTemplate
import com.yandex.div.json.ParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.templates.CachingTemplateProvider
import com.yandex.div.json.templates.InMemoryTemplateProvider
import com.yandex.div.json.templates.TemplateProvider
import com.yandex.div.parser.json.JsonTopologicalSorting
import com.yandex.div.parser.json.ParsingException
import com.yandex.div.parser.json.logger.TemplateParsingErrorLogger

@ExperimentalApi
public abstract class TemplateParsingEnvironment<T: JsonTemplate<*>> @JvmOverloads constructor(
    override val logger: ParsingErrorLogger,
    private val mainTemplateProvider: CachingTemplateProvider<T> = CachingTemplateProvider(
        InMemoryTemplateProvider(),
        TemplateProvider.empty(),
    ),
) : ParsingEnvironment {

    public abstract val templateFactory: TemplateFactory<T>

    override val templates: TemplateProvider<T> = mainTemplateProvider

    public fun parseTemplates(json: JsonObject) {
        val result = parseTemplatesWithResult(json)
        mainTemplateProvider.putAll(result.parsedTemplates)
    }

    /**
     * Parses json with templates memoizing dependencies between them along the way.
     * @return object consisting of two maps with parsed templates and their dependencies.
     */
    public fun parseTemplatesWithResult(json: JsonObject): TemplateParsingResult<T> {
        val parsedTemplates = arrayMapOf<String, T>()
        val templateDependencies = arrayMapOf<String, Set<String>>()
        try {
            val orderedNamesWithDependencies = JsonTopologicalSorting.sort(this, json)
            mainTemplateProvider.takeSnapshot(parsedTemplates)
            val tmpProvider = TemplateProvider.wrap(parsedTemplates)

            orderedNamesWithDependencies.forEach { (name, dependencies) ->
                try {
                    val env = ParsingEnvironmentImpl(tmpProvider, TemplateParsingErrorLogger(logger, name))
                    val template = templateFactory.create(env, topLevel = true, json.getObject(name))
                    parsedTemplates[name] = template
                    if (dependencies.isNotEmpty()) {
                        templateDependencies[name] = dependencies
                    }
                } catch (e: ParsingException) {
                    logger.logTemplateError(e, name)
                }
            }
        } catch (e: Exception) {
            logger.logError(e)
        }

        return TemplateParsingResult(parsedTemplates, templateDependencies)
    }

    public fun interface TemplateFactory<T> {
        public fun create(env: ParsingEnvironment, topLevel: Boolean, json: JsonObject): T
    }

    public class TemplateParsingResult<T>(
        public val parsedTemplates: Map<String, T>,
        public val templateDependencies: Map<String, Set<String>>
    )
}
