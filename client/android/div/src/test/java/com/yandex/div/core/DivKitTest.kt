package com.yandex.div.core

import androidx.test.core.app.ApplicationProvider
import com.yandex.div.DivDataTag
import com.yandex.div.storage.DivDataRepository
import com.yandex.div.storage.DivStorageComponent
import com.yandex.div.storage.RawDataAndMetadata
import com.yandex.div.storage.RawJsonRepository
import com.yandex.div.storage.rawjson.RawJson
import org.json.JSONObject
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivKitTest {
    private val tagA = DivDataTag("a")
    private val tagB = DivDataTag("b")

    private val rawJsonPredicate = argumentCaptor<(RawJson) -> Boolean>()
    private val rawJsonRepository = mock<RawJsonRepository> {
        on { remove(rawJsonPredicate.capture()) } doReturn mock()
    }

    private val divDataPredicate = argumentCaptor<(RawDataAndMetadata) -> Boolean>()
    private val divDataRepository = mock<DivDataRepository> {
        on { remove(divDataPredicate.capture()) } doReturn mock()
    }

    private val storageComponent = mock<DivStorageComponent> {
        on { rawJsonRepository } doReturn rawJsonRepository
        on { repository } doReturn divDataRepository
    }

    private val underTest = DivKit(
        context = ApplicationProvider.getApplicationContext(),
        configuration = DivKitConfiguration.Builder()
            .divStorageComponent { storageComponent }
            .build()
    )

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
    fun `reset stored div data does not remove variables`() {
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
}

private fun String.withCard(id: String) = "card_${id}_$this"
private fun String.toRawJson() = RawJson(this, JSONObject())
private fun String.toRawData() = RawDataAndMetadata(this, JSONObject())
