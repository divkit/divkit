package com.yandex.div.storage.templates

import android.os.SystemClock
import androidx.annotation.WorkerThread
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.util.forEach
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.TemplateParsingEnvironment
import com.yandex.div.json.templates.CachingTemplateProvider
import com.yandex.div.json.templates.InMemoryTemplateProvider
import com.yandex.div.json.templates.TemplateProvider
import com.yandex.div.storage.DivStorage
import com.yandex.div.storage.DivStorageErrorException
import com.yandex.div.storage.analytics.ErrorExplanation
import com.yandex.div.storage.histogram.HistogramNameProvider
import com.yandex.div.storage.histogram.HistogramRecorder
import com.yandex.div2.DivTemplate
import org.json.JSONException
import org.json.JSONObject
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Provider
import kotlin.system.measureTimeMillis

private typealias TemplateHash = String
private typealias TemplateId = String
private typealias GroupId = String

@Mockable
internal class TemplatesContainer(
        private val divStorage: DivStorage,
        private val errorLogger: ParsingErrorLogger,
        private val histogramRecorder: HistogramRecorder,
        private val parsingHistogramProxy: Provider<DivParsingHistogramProxy>,
        private val histogramNameProvider: HistogramNameProvider? = null,
) {
    private var templateReferencesLoaded = false
    private val histogramComponentName = histogramNameProvider?.componentName
    private val templatesPool = CommonTemplatesPool(
            divStorage,
            errorLogger,
            histogramComponentName,
            histogramRecorder,
            parsingHistogramProxy,)
    private val groupTemplateReferences =
            mutableMapOf<GroupId, TemplateReferenceResolver>()
    private val templateEnvironments = mutableMapOf<GroupId, DivParsingEnvironment>()
    private val messageDigest: MessageDigest? by lazy {
        try {
            MessageDigest.getInstance("MD5")
        } catch (e: NoSuchAlgorithmException) {
            errorLogger.logError(IllegalStateException("Storage cannot work with templates!", e))
            null
        }
    }

    fun getEnvironment(groupId: String): DivParsingEnvironment {
        loadIfNeeded()
        return templateEnvironments.getOrPut(groupId) { createEnvBy(groupId) }
    }

    @WorkerThread
    fun explainMissingTemplate(
            cardId: String?, groupId: String, templateId: String
    ): ErrorExplanation {
        val templateHash = groupTemplateReferences[groupId]?.resolveTemplateHash(templateId)
        val templateProvider = templateEnvironments[groupId]?.templates
        val shortReason: String
        val details = StringBuilder()
        val exceptions = mutableListOf<DivStorageErrorException>()

        fun cardStored() = if (cardId != null) {
            try {
                divStorage.isCardExists(cardId, groupId)
            } catch (e: DivStorageErrorException) {
                exceptions.add(e)
                false
            }
        } else false

        fun templateStored(): Boolean {
            if (templateHash == null) return false
            return try {
                divStorage.isTemplateExists(templateHash)
            } catch (e: DivStorageErrorException) {
                exceptions.add(e)
                false
            }
        }

        when {
            // Check if template exists in caches.
            templateProvider?.get(templateId) != null -> {
                shortReason = "unknown, template is loaded"
            }
            // Template is missing at in-memory cache, let's then check persistent cache.
            !templateStored() -> {
                details.append(
                        "supported responses: ${groupTemplateReferences.keys.joinToString()}"
                )
                if (!groupTemplateReferences.keys.contains(groupId)) {
                    shortReason = "cached, but not loaded into memory"
                } else {
                    shortReason = "cached, but loaded partially"
                    details.append(" ${templateProvider?.loadedTemplatesDetails()}")
                }
            }
            // At this point we know that card's templates are not present in storage,
            // but yet this does not mean that there's something wrong with original response.
            // If this card is also not present in storage, then this means that it is now
            // being parsed from response. There is only one explanation why card could not
            // find any templates - we're building card, but templates haven't yet arrived
            // to in-memory storage. This is a client-side bug!
            !cardStored() -> {
                shortReason = "access templates ahead of time"
            }
            // Each response creates own storage for its templates (they are grouped by "group_id").
            // This storage will not be updated anymore and will be used ONLY for reading.
            // With this approach we can treat contents in persistent cache as pure snapshot
            // of data received from backend. That is why 'template not found in persistent
            // storage' is equal to 'template not present in response'!
            else -> {
                shortReason = if (exceptions.isNotEmpty()) {
                    exceptions.forEach { details.append("${it.message};\n") }
                    "fatal exception when explaining reason"
                } else "not present in original response"

                templateProvider?.loadedTemplatesDetails()?.let { details.append(it) }
            }
        }

        return ErrorExplanation(shortReason, details.toString())
    }

    fun addTemplates(groupId: String, json: JSONObject, sourceType: String?): List<Template> {
        val parseHistogramBaseName = histogramNameProvider?.divParsingHistogramName
        val templateHashes = calculateTemplateHashes(json)
        val env = getEnvironment(groupId)
        val templateParsingResult = if (parseHistogramBaseName != null) {
            val parsingHistogramNames = parsingHistograms(parseHistogramBaseName, sourceType)

            val results: TemplateParsingEnvironment.TemplateParsingResult<DivTemplate>
            val duration = measureTimeMillis {
                results = parsingHistogramProxy.get()
                        .parseTemplatesWithResultsAndDependencies(env, json,
                                histogramComponentName)
            }

            histogramRecorder.reportTemplatesParseTime(parsingHistogramNames, duration)
            results
        } else {
            parsingHistogramProxy.get()
                    .parseTemplatesWithResultsAndDependencies(env, json, histogramComponentName)
        }
        env.templates.putAll(templateParsingResult.parsedTemplates)

        val results = ArrayList<Template>(templateParsingResult.parsedTemplates.size)
        templateParsingResult.parsedTemplates.forEach { (id, template) ->
            val templateHash = templateHashes[id] ?: run {
                errorLogger.logError(IllegalStateException(
                        "No corresponding template was found for templateId = $id"))
                return@forEach
            }

            templatesPool.put(templateHash, template)
            json.optJSONObject(id)?.let { rawTemplate ->
                results.add(Template(
                        id = id,
                        hash = templateHash,
                        template = rawTemplate,
                ))
            } ?: run {
                errorLogger.logError(IllegalStateException(
                        "No raw template found for templateId = $id"))
                return@forEach
            }
        }
        templateEnvironments[groupId] = env
        groupTemplateReferences[groupId] = TemplateReferenceResolver(errorLogger)
        return results
    }

    private fun CachingTemplateProvider<DivTemplate>.loadedTemplatesDetails(): String {
        val snapshot = mutableMapOf<String, DivTemplate>()
        this.takeSnapshot(snapshot)
        return "in-memory templates count: ${snapshot.size}"
    }

    private fun parsingHistograms(baseName: String?, sourceType: String?): Set<String> {
        return if (baseName != null) {
            linkedSetOf(baseName).apply {
                if (sourceType != null) add("$baseName.$sourceType")
            }
        } else emptySet()
    }

    private fun loadIfNeeded() {
        if (templateReferencesLoaded) {
            return
        }
        templateReferencesLoaded = true
        val loadResult = divStorage.readTemplateReferences()
        loadResult.errors.forEach {
            errorLogger.logError(it)
        }

        loadResult.restoredData.forEach {
            val references = groupTemplateReferences.getOrPut(it.groupId) {
                TemplateReferenceResolver(errorLogger)
            }
            references.add(it)
        }
    }

    private fun createEnvBy(groupId: String): DivParsingEnvironment {
        val referenceResolver: TemplateReferenceResolver = getTemplates(groupId)
                ?: return createEmptyEnv(errorLogger)

        val templatesByHash: Map<TemplateHash, DivTemplate> = templatesPool.load(referenceResolver)
        val env = createEmptyEnv(errorLogger)
        val templatesById = HashMap<String, DivTemplate>(templatesByHash.size)
        templatesByHash.forEach { (hash, json) ->
            referenceResolver.doOnEachResolvedId(hash) { templateId ->
                templatesById[templateId] = json
            }
        }

        env.templates.putAll(templatesById)
        return env
    }

    private fun getTemplates(groupId: String): TemplateReferenceResolver? {
        loadIfNeeded()
        return groupTemplateReferences[groupId]
    }

    private fun calculateTemplateHashes(json: JSONObject): MutableMap<TemplateId, TemplateHash> {
        val results = mutableMapOf<TemplateId, TemplateHash>()
        json.forEach { templateId, templateJson: JSONObject ->
            val checksum = calculateJsonChecksum(templateJson) ?: return@forEach
            results[templateId] = checksum
        }
        return results
    }

    private fun calculateJsonChecksum(json: JSONObject): String? {
        val input = json.toString().toByteArray(Charsets.UTF_8)
        val md5bytes = synchronized(this) {
            messageDigest?.digest(input)
        } ?: return null
        return BigInteger(1, md5bytes).toString(16).padStart(length = 32, padChar = '0')
    }
}

private class CommonTemplatesPool(
        private val divStorage: DivStorage,
        logger: ParsingErrorLogger,
        private val histogramComponent: String?,
        private val histogramRecorder: HistogramRecorder,
        private val parsingHistogramProxy: Provider<DivParsingHistogramProxy>,
) {
    private val commonTemplates = ConcurrentHashMap<TemplateHash, DivTemplate>()
    private val env: DivParsingEnvironment = createEmptyEnv(logger)

    fun load(templateReferences: TemplateReferenceResolver): Map<TemplateHash, DivTemplate> {
        val templateHashes = templateReferences.templateHashes
        val nonLoaded = templateHashes.toMutableSet()
                .apply { removeAll(commonTemplates.keys) }
        if (nonLoaded.isNotEmpty()) {
            loadFromStorage(env, templateReferences, nonLoaded)
        }
        return commonTemplates.filter { templateHashes.contains(it.key) }
    }

    private fun loadFromStorage(env: DivParsingEnvironment,
                                templateReferences: TemplateReferenceResolver,
                                templateHashes: Set<String>) {
        val loadingStarted = SystemClock.uptimeMillis()
        val loadResult = divStorage.readTemplates(templateHashes)
        histogramRecorder.reportTemplateLoadedTime(SystemClock.uptimeMillis() - loadingStarted)
        val templatesJson = JSONObject()
        loadResult.restoredData.forEach {
            val hash = it.hash

            val json = try {
                JSONObject(it.data.toString(Charsets.UTF_8))
            } catch (e: JSONException) {
                env.logger.logError(IllegalStateException(
                        "Template deserialization failed (hash: $hash)!", e))
            }
            templateReferences.doOnEachResolvedId(hash) { templateId: String ->
                try {
                    templatesJson.put(templateId, json)
                } catch (e: JSONException) {
                    env.logger.logError(IllegalStateException(
                            "Template '$templateId' adding to json failed!", e))
                }
            }
        }
        loadResult.errors.forEach {
            env.logger.logError(it)
        }

        val results: Map<TemplateId, DivTemplate> = parsingHistogramProxy.get()
                .parseTemplatesWithResultsAndDependencies(env, templatesJson, histogramComponent)
                .parsedTemplates
        results.forEach { (id, template) ->
            val hash = templateReferences.resolveTemplateHash(id) ?: run {
                env.logger.logError(IllegalStateException(
                        "Failed to resolve template hash for id: $id"))
                return@forEach
            }
            commonTemplates[hash] = template
        }
    }

    fun put(templateHash: TemplateHash, template: DivTemplate) {
        commonTemplates[templateHash] = template
    }
}

private class TemplateReferenceResolver(
        private val logger: ParsingErrorLogger,
) {
    private val templateHashRefs = mutableMapOf<TemplateId, TemplateHash>()
    private val _templateHashes = mutableSetOf<TemplateHash>()
    val templateHashes: Set<TemplateHash> = _templateHashes

    private val templateIdRefs: Map<TemplateHash, TemplateHashIds> by lazy {
        val refs = HashMap<TemplateHash, TemplateHashIds>(templateHashRefs.size)
        templateHashRefs.forEach { (id, hash) ->
            val existent = refs[hash]
            refs[hash] = when (existent) {
                null -> TemplateHashIds.Single(id)
                is TemplateHashIds.Single -> {
                    TemplateHashIds.Collection(mutableListOf(existent.id, id))
                }
                is TemplateHashIds.Collection -> {
                    existent.ids.add(id)
                    existent
                }
            }
        }
        refs
    }

    fun add(templateReference: DivStorage.TemplateReference) {
        templateHashRefs[templateReference.templateId] = templateReference.templateHash
        _templateHashes.add(templateReference.templateHash)
    }

    inline fun doOnEachResolvedId(hash: String, block: (templateId: String) -> Unit) {
        when (val ids = templateIdRefs[hash]) {
            is TemplateHashIds.Collection -> ids.ids.forEach { block(it) }
            is TemplateHashIds.Single -> block(ids.id)
            null -> logger.logError(IllegalStateException("No template id was found for hash!"))
        }
    }

    fun resolveTemplateHash(id: String): String? {
        return templateHashRefs[id]
    }
}

private sealed interface TemplateHashIds {
    @JvmInline
    value class Single(val id: String) : TemplateHashIds
    @JvmInline
    value class Collection(val ids: MutableList<String>) : TemplateHashIds
}

private fun createEmptyEnv(logger: ParsingErrorLogger) = DivParsingEnvironment(
        logger,
        CachingTemplateProvider(
                InMemoryTemplateProvider(),
                TemplateProvider.empty(),
        ),
)
