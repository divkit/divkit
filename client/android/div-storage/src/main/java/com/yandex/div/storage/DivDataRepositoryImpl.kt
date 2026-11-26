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
import com.yandex.div.storage.internal.CardRepositoryReader
import com.yandex.div.storage.templates.DivParsingHistogramProxy
import com.yandex.div.storage.templates.TemplatesContainer
import com.yandex.div2.DivData
import org.json.JSONObject
import java.util.Calendar
import javax.inject.Provider

internal class DivDataRepositoryImpl(
        private val divStorage: DivStorage,
        private val templateContainer: TemplatesContainer,
        private val histogramRecorder: HistogramRecorder,
        private val histogramNameProvider: HistogramNameProvider?,
        private val divParsingHistogramProxy: Provider<DivParsingHistogramProxy>,
        private val cardErrorFactory: CardErrorLoggerFactory,
) : DivDataRepository {
    private val cardReader = CardRepositoryReader(
            cardId = DivDataRepository.DivDataWithMeta::id,
            errorCardId = DivDataRepositoryException::cardId,
            loadFromStorage = ::loadFromStorage,
    )

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

        cardReader.updateCache(results)

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
        val (cards, errors) = cardReader.getAll()
        return DivDataRepositoryResult(cards, errors)
    }

    @AnyThread
    override fun get(ids: List<String>): DivDataRepositoryResult {
        if (ids.isEmpty()) {
            return DivDataRepositoryResult.EMPTY
        }

        val (cards, errors) = cardReader.get(ids)
        return DivDataRepositoryResult(cards, errors)
    }

    private fun loadFromStorage(
            ids: Set<String>,
            idsToExclude: Set<String>,
    ): CardRepositoryReader.ReadResult<DivDataRepository.DivDataWithMeta, DivDataRepositoryException> {
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

        return CardRepositoryReader.ReadResult(results, exceptions)
    }

    @AnyThread
    override fun remove(predicate: (RawDataAndMetadata) -> Boolean): DivDataRepositoryRemoveResult {
        val (deletedIds, storageErrors) = divStorage.remove(predicate)
        val exceptions = storageErrors.toDivDataRepositoryExceptions()
        cardReader.removeFromCache(deletedIds)
        return DivDataRepositoryRemoveResult(deletedIds, exceptions)
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
