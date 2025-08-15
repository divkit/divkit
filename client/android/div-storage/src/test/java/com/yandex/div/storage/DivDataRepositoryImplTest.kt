package com.yandex.div.storage

import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.ParsingException
import com.yandex.div.json.templates.CachingTemplateProvider
import com.yandex.div.storage.DivStorage.LoadDataResult
import com.yandex.div.storage.DivStorage.RestoredRawData
import com.yandex.div.storage.analytics.CardErrorLoggerFactory
import com.yandex.div.storage.database.ExecutionResult
import com.yandex.div.storage.database.StorageException
import com.yandex.div.storage.templates.DivParsingHistogramProxy
import com.yandex.div.storage.templates.TemplatesContainer
import com.yandex.div2.DivData
import com.yandex.div2.DivTemplate
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argThat
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import javax.inject.Provider

private const val INVALID_CARD_ID = "card#invalid"
private const val DIV_DATA_INVALID_KEY = "isInvalid"
private const val DIV_DATA_UNEXPECTED_PARSING_ERROR_MESSAGE = "some unexpected parsing error"
private val MOCK_DIV_DATA = DivData(logId = "test", states = emptyList())

@RunWith(RobolectricTestRunner::class)
class DivDataRepositoryImplTest {
    private val invalidRawDivData = JSONObject("""
        {
            $DIV_DATA_INVALID_KEY: true
        }
    """.trimIndent())

    private val executionResult = mock<ExecutionResult>()
    private val divStorage = mock<DivStorage> {
        on { saveData(any(), any(), any(), any()) } doReturn executionResult
        on { loadData(any(), any()) } doAnswer {
            LoadDataResult(listOf(
                    RestoredRawData(INVALID_CARD_ID, invalidRawDivData, groupId = "fake_group_id")))
        }
    }

    private val histogramProxy = mock<DivParsingHistogramProxy> {
        on { createDivData(any(), any(), anyOrNull()) } doAnswer {
            val json = it.getArgument<JSONObject>(1)
            val isInvalid = json.opt(DIV_DATA_INVALID_KEY) as Boolean?
            if (isInvalid == true) {
                throw mock<ParsingException> {
                    on { message } doReturn DIV_DATA_UNEXPECTED_PARSING_ERROR_MESSAGE
                }
            }
            MOCK_DIV_DATA
        }
    }

    private val histogramProxyProvider = mock<Provider<DivParsingHistogramProxy>> {
        on { get() } doReturn histogramProxy
    }

    private val templateProvider = mock<CachingTemplateProvider<DivTemplate>>()
    private val parsingEnvironment = mock<DivParsingEnvironment> {
        on { templates } doReturn templateProvider
    }

    private val templateContainer = mock<TemplatesContainer> {
        on { getEnvironment(any()) } doReturn parsingEnvironment
    }

    private val errorLogger = mock<ParsingErrorLogger>()

    private val cardErrorFactory = mock<CardErrorLoggerFactory> {
        on { createContextualLogger(anyOrNull(), any(), any(), anyOrNull()) } doReturn errorLogger
    }

    private val underTest
        get() = DivDataRepositoryImpl(
                divStorage = divStorage,
                templateContainer = templateContainer,
                histogramRecorder = mock(),
                histogramNameProvider = mock(),
                divParsingHistogramProxy = histogramProxyProvider,
                cardErrorFactory = cardErrorFactory
        )

    @Test
    fun `put() logs unexpected parsing error`() {
        underTest.put(
                DivDataRepository.Payload(
                        listOf(RawDataAndMetadata(INVALID_CARD_ID, invalidRawDivData)),
                        emptyMap()
                )
        )

        verify(errorLogger, times(1))
                .logError(argThat {
                    this as ParsingException
                    this.message == DIV_DATA_UNEXPECTED_PARSING_ERROR_MESSAGE
                })
    }

    @Test
    fun `getAll() logs unexpected parsing error`() {
        underTest.getAll()

        verify(errorLogger, times(1))
                .logError(argThat {
                    this as ParsingException
                    this.message == DIV_DATA_UNEXPECTED_PARSING_ERROR_MESSAGE
                })
    }

    @Test
    fun `get() logs unexpected parsing error`() {
        underTest.get(listOf(INVALID_CARD_ID))

        verify(errorLogger, times(1))
                .logError(argThat {
                    this as ParsingException
                    this.message == DIV_DATA_UNEXPECTED_PARSING_ERROR_MESSAGE
                })
    }

    @Test
    fun `getAll() request storage once if result without error`() {
        val underTest = underTest
        whenever(divStorage.loadData(any(), any())) doReturn
                DivStorage.LoadDataResult(emptyList(), emptyList())

        underTest.getAll()
        underTest.getAll()
        underTest.getAll()
        verify(divStorage, times(1)).loadData(any(), any())
    }

    @Test
    fun `getAll() request storage until all data is received`() {
        val underTest = underTest
        val errors = argumentCaptor<List<String>>()
        val listOfErrors = mutableListOf(getStorageException("1"), getStorageException("2"))
        whenever(divStorage.loadData(errors.capture(), any())) doReturn
                DivStorage.LoadDataResult(emptyList(), listOfErrors)

        underTest.getAll()
        assertEquals(0, errors.allValues[0].size) // request all

        underTest.getAll()
        assertEquals(2, errors.allValues[1].size) // request cards with errors

        listOfErrors.removeAt(listOfErrors.lastIndex)
        underTest.getAll()
        assertEquals(2, errors.allValues[2].size)
        underTest.getAll()
        assertEquals(1, errors.allValues[3].size) // request last error card

        listOfErrors.removeAt(listOfErrors.lastIndex)
        underTest.getAll()
        assertEquals(1, errors.allValues[4].size)

        underTest.getAll()
        underTest.getAll()
        underTest.getAll()
        val allValues = errors.allValues
        assertEquals(5, allValues.size)
    }

    @Test
    fun `get() request storage until all data is received`() {
        val underTest = underTest
        val ids = List(2) { it.toString() }
        val listOfCards = mutableListOf<DivStorage.RestoredRawData>()
        val errors = argumentCaptor<List<String>>()
        whenever(divStorage.loadData(errors.capture(), any())) doReturn
                DivStorage.LoadDataResult(listOfCards, emptyList())

        // check that in case of error we request this data again
        underTest.get(ids)
        assertEquals(2, errors.allValues[0].size)

        listOfCards.add(getRestoredRawData(ids[0]))
        underTest.get(ids)
        assertEquals(2, errors.allValues[1].size) // store to memory first raw data

        underTest.get(ids)
        assertEquals(1, errors.allValues[2].size) // one card is in memory
        underTest.get(ids)
        assertEquals(1, errors.allValues[3].size) // nothing new to memory

        listOfCards.add(getRestoredRawData(ids[1]))
        underTest.get(ids)
        assertEquals(1, errors.allValues[4].size) // store to memory second raw data

        underTest.get(ids) // all data is in memory
        underTest.get(ids)
        underTest.get(ids)

        assertEquals(5, errors.allValues.size)

    }

    private fun getStorageException(id: String): StorageException =
            object : StorageException(cardId = id) {}

    private fun getRestoredRawData(id: String): DivStorage.RestoredRawData =
            DivStorage.RestoredRawData(id, mock(), mock(), id)
}
