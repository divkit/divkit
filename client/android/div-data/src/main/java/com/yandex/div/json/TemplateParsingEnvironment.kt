package com.yandex.div.json

import com.yandex.div.internal.parser.JsonTopologicalSorting
import com.yandex.div.internal.parser.ParsingEnvironmentImpl
import com.yandex.div.internal.parser.TemplateParsingErrorLogger
import com.yandex.div.internal.util.arrayMap
import com.yandex.div.json.templates.CachingTemplateProvider
import com.yandex.div.json.templates.InMemoryTemplateProvider
import com.yandex.div.json.templates.TemplateProvider
import org.json.JSONException
import org.json.JSONObject

abstract class TemplateParsingEnvironment<T: JsonTemplate<*>> @JvmOverloads constructor(
    override val logger: ParsingErrorLogger,
    private val mainTemplateProvider: CachingTemplateProvider<T> = CachingTemplateProvider(
        InMemoryTemplateProvider(),
        TemplateProvider.empty(),
    ),
) : ParsingEnvironment {

    abstract val templateFactory: TemplateFactory<T>

    override val templates: TemplateProvider<T> = mainTemplateProvider

    fun parseTemplates(json: JSONObject) {
        val parsed = parseTemplatesWithResult(json)
        mainTemplateProvider.putAll(parsed)
    }

    /**
     * Tries to convert json to a collection of templates and retain them for future usage.
     * @return names of successfully parsed templates
     */
    fun parseTemplatesWithResult(json: JSONObject): Map<String, T> {
        return parseTemplatesWithResultAndDependencies(json).parsedTemplates
    }

    /**
     * Parses json with templates memoizing dependencies between them along the way.
     * @return object consisting of two maps with parsed templates and their dependencies.
     */
    fun parseTemplatesWithResultAndDependencies(json: JSONObject): TemplateParsingResult<T> {
        val parsedTemplates = arrayMap<String, T>()
        val templateDependencies = arrayMap<String, Set<String>>()
        try {
            val orderedNamesWithDependencies = JsonTopologicalSorting.sort(this, json)
            mainTemplateProvider.takeSnapshot(parsedTemplates)
            val tmpProvider = TemplateProvider.wrap(parsedTemplates)

            orderedNamesWithDependencies.forEach { (name, dependencies) ->
                try {
                    val env = ParsingEnvironmentImpl(tmpProvider, TemplateParsingErrorLogger(logger, name))
                    val template = templateFactory.create(env, topLevel = true, json.getJSONObject(name))
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

    fun interface TemplateFactory<T> {

        @Throws(JSONException::class)
        fun create(env: ParsingEnvironment, topLevel: Boolean, json: JSONObject): T
    }

    class TemplateParsingResult<T>(
        val parsedTemplates: Map<String, T>,
        val templateDependencies: Map<String, Set<String>>
    )
}
