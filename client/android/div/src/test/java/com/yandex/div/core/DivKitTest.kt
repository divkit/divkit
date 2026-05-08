package com.yandex.div.core

import android.content.Context
import com.yandex.div.DivDataTag
import com.yandex.div.core.expression.storedvalues.StoredValuesController
import com.yandex.div.storage.DivDataRepository
import com.yandex.div.storage.DivStorageComponent
import com.yandex.div.storage.RawDataAndMetadata
import com.yandex.div.storage.RawJsonRepository
import com.yandex.div.storage.rawjson.RawJson
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

@RunWith(RobolectricTestRunner::class)
@Config(shadows = [DivKitTest.ShadowStoredValuesController::class])
class DivKitTest {

    private val appContext = mock<Context>()
    private val context = mock<Context> {
        on { applicationContext } doReturn appContext
    }
    private val tagA = DivDataTag("a")
    private val tagB = DivDataTag("b")
    private val rawJsonPredicate = argumentCaptor<(RawJson) -> Boolean>()
    private val divDataPredicate = argumentCaptor<(RawDataAndMetadata) -> Boolean>()
    private val underTest = DivKit.getInstance(context)

    @Before
    fun setup() {
        whenever(rawJsonRepository.remove(rawJsonPredicate.capture())).doReturn(mock())
        whenever(divDataRepository.remove(divDataPredicate.capture())).doReturn(mock())
    }

    @Test
    fun `reset with no flags does not touch repositories`() {
        underTest.reset(flags = 0)

        verify(rawJsonRepository, never()).remove(any())
        verify(divDataRepository, never()).remove(any())
    }

    @Test
    fun `reset with default arguments clears all storages when tags empty`() {
        underTest.reset()

        assertTrue(rawJsonPredicate.firstValue.invoke(mock()))
        assertTrue(divDataPredicate.firstValue.invoke(mock()))
    }

    @Test
    fun `reset with RESET_ALL clears all storages when tags empty`() {
        underTest.reset(DivKit.RESET_ALL)

        assertTrue(rawJsonPredicate.firstValue.invoke(mock()))
        assertTrue(divDataPredicate.firstValue.invoke(mock()))
    }

    @Test
    fun `reset stored variables does not remove div data`() {
        underTest.reset(DivKit.RESET_STORED_VARIABLES)
        verify(divDataRepository, never()).remove(any())
    }

    @Test
    fun `reset stored variables with empty tags removes all variables`() {
        underTest.reset(DivKit.RESET_STORED_VARIABLES, emptyList())
        assertTrue(rawJsonPredicate.firstValue.invoke(mock()))
    }

    @Test
    fun `reset stored variables with particular tags removes variables for tag`() {
        underTest.reset(DivKit.RESET_STORED_VARIABLES, listOf(tagA))

        assertTrue(rawJsonPredicate.firstValue.invoke("x".withCard(tagA.id).toRawJson()))
        assertTrue(rawJsonPredicate.firstValue.invoke("y".withCard(tagA.id).toRawJson()))
    }

    @Test
    fun `reset stored variables with particular tags does not remove variables for other tags`() {
        underTest.reset(DivKit.RESET_STORED_VARIABLES, listOf(tagA))
        assertFalse(rawJsonPredicate.firstValue.invoke("x".withCard(tagB.id).toRawJson()))
    }

    @Test
    fun `reset stored variables with multiple tags removes variables for every tag`() {
        underTest.reset(DivKit.RESET_STORED_VARIABLES, listOf(tagA, tagB))

        assertTrue(rawJsonPredicate.firstValue.invoke("x".withCard(tagA.id).toRawJson()))
        assertTrue(rawJsonPredicate.firstValue.invoke("y".withCard(tagB.id).toRawJson()))
    }

    @Test
    fun `reset stored div data does not remove variables`(){
        underTest.reset(DivKit.RESET_STORED_DIV_DATA)
        verify(rawJsonRepository, never()).remove(any())
    }

    @Test
    fun `reset stored div data with empty tags removes all div data`() {
        underTest.reset(DivKit.RESET_STORED_DIV_DATA, emptyList())
        assertTrue(divDataPredicate.firstValue.invoke(mock()))
    }

    @Test
    fun `reset stored div data with particular tags removes matching ids`() {
        underTest.reset(DivKit.RESET_STORED_DIV_DATA, listOf(tagA))
        assertTrue(divDataPredicate.firstValue.invoke(tagA.id.toRawData()))
    }

    @Test
    fun `reset stored div data with particular tags does not remove data with another id`() {
        underTest.reset(DivKit.RESET_STORED_DIV_DATA, listOf(tagA))
        assertFalse(divDataPredicate.firstValue.invoke("x".toRawData()))
    }

    @Test
    fun `reset stored div data with multiple tags matches any id`() {
        underTest.reset(DivKit.RESET_STORED_DIV_DATA, listOf(tagA, tagB))

        assertTrue(divDataPredicate.firstValue.invoke(tagA.id.toRawData()))
        assertTrue(divDataPredicate.firstValue.invoke(tagB.id.toRawData()))
    }

    @Test
    fun `reset with both storage flags and empty tags clears everything`() {
        underTest.reset(DivKit.RESET_STORED_VARIABLES or DivKit.RESET_STORED_DIV_DATA, emptyList())

        assertTrue(rawJsonPredicate.firstValue.invoke(mock()))
        assertTrue(divDataPredicate.firstValue.invoke(mock()))
    }

    @Test
    fun `reset with RESET_ALL and tags removes data for tags`() {
        underTest.reset(DivKit.RESET_ALL, listOf(tagA))

        assertTrue(rawJsonPredicate.firstValue.invoke("x".withCard(tagA.id).toRawJson()))
        assertFalse(rawJsonPredicate.firstValue.invoke("x".withCard(tagB.id).toRawJson()))
        assertTrue(divDataPredicate.firstValue.invoke(tagA.id.toRawData()))
        assertFalse(divDataPredicate.firstValue.invoke(tagB.id.toRawData()))
    }

    @After
    fun tearDown() {
        reset(rawJsonRepository, divDataRepository)
    }

    @Implements(StoredValuesController.Companion::class)
    class ShadowStoredValuesController {

        @Implementation
        @Suppress("unused")
        fun isStoredForCard(receiver: RawJson, dataTag: String) = receiver.id.startsWith("$CARD_LINE_PREFIX$dataTag:")
    }

    private companion object {

        const val CARD_LINE_PREFIX = "card:"

        val json = JSONObject()
        val rawJsonRepository = mock<RawJsonRepository>()
        val divDataRepository = mock<DivDataRepository>()
        val storageComponent = mock<DivStorageComponent> {
            on { rawJsonRepository } doReturn rawJsonRepository
            on { repository } doReturn divDataRepository
        }
        val configuration = DivKitConfiguration.Builder()
            .divStorageComponent { storageComponent }
            .build()

        init {
            DivKit.configure(configuration)
        }

        private fun String.withCard(id: String) = "$CARD_LINE_PREFIX$id:$this"

        private fun String.toRawJson() = RawJson(this, json)

        private fun String.toRawData() = RawDataAndMetadata(this, json)
    }
}
