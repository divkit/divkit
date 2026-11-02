package com.yandex.div.storage

import androidx.annotation.AnyThread
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.histogram.util.HistogramClock
import com.yandex.div.internal.util.UiThreadHandler
import com.yandex.div.json.ParsingException
import com.yandex.div.storage.DivDataRepositoryException.JsonParsingException
import com.yandex.div.storage.analytics.CardErrorLoggerFactory
import com.yandex.div.storage.database.StorageException
import com.yandex.div.storage.histogram.HistogramNameProvider
import com.yandex.div.storage.histogram.HistogramRecorder
import com.yandex.div.storage.templates.DivParsingHistogramProxy
import com.yandex.div.storage.templates.TemplatesContainer
import com.yandex.div2.DivData
import org.json.JSONObject
import java.util.Calendar
import java.util.Collections
import javax.inject.Provider

internal class DivDataRepositoryImpl(
        private val divStorage: DivStorage,
        private val templateContainer: TemplatesContainer,
        private val histogramRecorder: HistogramRecorder,
        private val histogramNameProvider: HistogramNameProvider?,
        private val divParsingHistogramProxy: Provider<DivParsingHistogramProxy>,
        private val cardErrorFactory: CardErrorLoggerFactory,
) : DivDataRepository {
    private val inMemoryData =
            Collections.synchronizedMap(linkedMapOf<String, DivDataRepository.DivDataWithMeta>())

    @Volatile
    private var areCardsSynchronizedWithInMemory = false
    @Volatile
    private var cardsWithErrors = mapOf<String, List<DivDataRepositoryException>>()

    @AnyThread
    override fun put(payload: DivDataRepository.Payload): DivDataRepositoryResult {
        val exceptions = mutableListOf<DivDataRepositoryException>()

        // Generating in-memory templates group so we could check that DivData
        // could be parsed with these templates and onlye then we'll continue
        // and save data to persistent storage.
        val groupId = generateUniqueId()
        val hashedTemplates = if (payload.templates.isNotEmpty()) {
            templateContainer.addTemplates(
                    groupId, payload.templates.asJSONObject(), payload.sourceType
            )
        } else emptyList()
        val environment = templateContainer.getEnvironment(groupId)

        val parseStarted = HistogramClock.uptime()

        val results = ArrayList<DivDataRepository.DivDataWithMeta>(payload.divs.size)
        val validPayload = ArrayList<RawDataAndMetadata>(payload.divs.size)

        payload.divs.forEach { rawData ->
            val parsingEnvironment = environment
                    .includeCardContext(rawData.id, groupId, rawData.metadata)
            val divData = try {
                parseRawDivData(
                        rawData.divData,
                        parsingEnvironment,
                        rawData.id
                )
            } catch (e: ParsingException) {
                parsingEnvironment.logger.logError(e)
                exceptions.add(JsonParsingException("Error parsing DivData", e, rawData.id))
                return@forEach
            }

            validPayload.add(rawData)
            results.add(DivDataRepository.DivDataWithMeta(
                    rawData.id,
                    divData,
                    rawData.metadata,
            ))
        }

        if (UiThreadHandler.get().isMainThread()) {
            histogramRecorder.reportDivDataLoadTime(HistogramClock.uptime() - parseStarted)
        }

        results.forEach { inMemoryData[it.id] = it }

        val validStoragePayload = if (validPayload.size == payload.divs.size) {
            payload
        } else {
            payload.copy(divs = validPayload)
        }

        val divStorageErrors = divStorage.saveData(
                groupId,
                validStoragePayload.divs,
                hashedTemplates,
                payload.actionOnError).errors
        exceptions.addAll(divStorageErrors.toDivDataRepositoryExceptions())

        return DivDataRepositoryResult(results, exceptions)
    }

    private fun generateUniqueId(): String {
        val time = Calendar.getInstance().time.toString()
        val salt = Any().hashCode()
        return "group-$time-$salt"
    }

    private fun DivParsingEnvironment.includeCardContext(
            cardId: String,
            groupId: String,
            metadata: JSONObject?
    ) = DivParsingEnvironment(
            logger = cardErrorFactory.createContextualLogger(
                    this.logger, cardId, groupId, metadata),
            this.templates,
    )

    @AnyThread
    override fun getAll(): DivDataRepositoryResult {
        if (areCardsSynchronizedWithInMemory && cardsWithErrors.isEmpty()) {
            return DivDataRepositoryResult(inMemoryValues(), emptyList())
        }

        val (cardsToRequest, cardsToExclude) = if (areCardsSynchronizedWithInMemory) {
            cardsWithErrors.keys to emptySet<String>()
        } else {
            emptySet<String>() to synchronized(inMemoryData) {
                inMemoryData.keys.minus(cardsWithErrors.keys)
            }
        }

        val storageResults = loadFromStorage(cardsToRequest, cardsToExclude)
        val loaded = cache(storageResults.resultData)
        val loadedIds = loaded.mapTo(HashSet()) { it.id }
        val resultsWithInMemory = storageResults.copy(
                resultData = loaded + inMemoryValues().filterNot { it.id in loadedIds })
        areCardsSynchronizedWithInMemory = true

        cardsWithErrors = storageResults.errors.groupBy { it.cardId }.filterKeysNotNull()

        return resultsWithInMemory
    }

    private fun inMemoryValues() = synchronized(inMemoryData) { inMemoryData.values.toList() }

    private fun cache(cards: List<DivDataRepository.DivDataWithMeta>) = synchronized(inMemoryData) {
        cards.map { card -> inMemoryData.getOrPut(card.id) { card } }
    }

    private fun <K, V> Map<K?, V>.filterKeysNotNull(): Map<K, V> {
        val newMap = LinkedHashMap<K, V>(size)
        for ((key, value) in entries) {
            if (key != null) {
                newMap[key] = value
            }
        }
        return newMap
    }

    @AnyThread
    override fun get(ids: List<String>): DivDataRepositoryResult {
        if (ids.isEmpty()) {
            return DivDataRepositoryResult.EMPTY
        }

        val idsToLoad = ids.toMutableSet()
        val inMemoryResults = ArrayList<DivDataRepository.DivDataWithMeta>(ids.size)
        ids.forEach { id ->
            inMemoryData[id]?.let {
                inMemoryResults.add(it)
                idsToLoad.remove(id)
            }
        }

        if (idsToLoad.isNotEmpty()) {
            val storageResults = loadFromStorage(idsToLoad, emptySet())
            val loaded = cache(storageResults.resultData)
            return storageResults.copy(resultData = loaded + inMemoryResults)
        }
        return DivDataRepositoryResult(inMemoryResults, emptyList())
    }

    private fun loadFromStorage(ids: Set<String>, idsToExclude: Set<String>): DivDataRepositoryResult {
        val exceptions = mutableListOf<DivDataRepositoryException>()
        val (rawData, errors) = divStorage.loadData(ids.toList(), idsToExclude.toList())
        exceptions.addAll(errors.toDivDataRepositoryExceptions())
        val environments = rawData.map { it.groupId }.toSet()
                .associateWith { templateContainer.getStoredEnvironment(it) }

        val parseStarted = HistogramClock.uptime()

        val results = ArrayList<DivDataRepository.DivDataWithMeta>(ids.size)
        rawData.forEach {
            val environment = environments.getValue(it.groupId) ?: run {
                exceptions.add(DivDataRepositoryException.StorageException(DivStorageErrorException(
                        "Failed to load templates of group ${it.groupId}.", cardId = it.id)))
                return@forEach
            }
            val parsingEnvironment = environment
                    .includeCardContext(it.id, it.groupId, it.metadata)
            val divData = try {
                parseRawDivData(
                        it.divData,
                        parsingEnvironment,
                        it.id
                )
            } catch (e: ParsingException) {
                parsingEnvironment.logger.logError(e)
                exceptions.add(JsonParsingException("Error parsing DivData", e, it.id))
                return@forEach
            }

            results.add(
                    DivDataRepository.DivDataWithMeta(
                            it.id,
                            divData,
                            it.metadata,
                    )
            )
        }

        histogramRecorder.reportDivDataLoadTime(HistogramClock.uptime() - parseStarted)

        return DivDataRepositoryResult(results, exceptions)
    }

    @AnyThread
    override fun remove(predicate: (RawDataAndMetadata) -> Boolean): DivDataRepositoryRemoveResult {
        val (deletedIds, storageErrors) = divStorage.remove(predicate)
        val exceptions = storageErrors.toDivDataRepositoryExceptions()
        removeFromInMemory(deletedIds)
        return DivDataRepositoryRemoveResult(deletedIds, exceptions)
    }

    private fun removeFromInMemory(deletedRecords: Set<String>) {
        deletedRecords.forEach {
            inMemoryData.remove(it)
        }
    }

    @Throws(ParsingException::class)
    private fun parseRawDivData(rawDivData: JSONObject,
                                parsingEnvironment: DivParsingEnvironment,
                                cardId: String,
    ): DivData {
        val componentName = histogramNameProvider?.getHistogramNameFromCardId(cardId)
        return divParsingHistogramProxy.get().createDivData(
                parsingEnvironment,
                rawDivData,
                componentName
        )
    }

    private fun Map<String, JSONObject>.asJSONObject(): JSONObject {
        val result = JSONObject()
        this.forEach { (id, template) ->
            result.put(id, template)
        }
        return result
    }

    private fun List<StorageException>.toDivDataRepositoryExceptions() =
            this.map(DivDataRepositoryException::StorageException)
}
