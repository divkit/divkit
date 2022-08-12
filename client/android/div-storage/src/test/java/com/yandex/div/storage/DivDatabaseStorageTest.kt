package com.yandex.div.storage

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivDatabaseStorageTest {

    private val context = ApplicationProvider.getApplicationContext<Application>()
    private val storage = DivDatabaseStorage(context = context, databaseName = "test-div.db")

    @Before
    fun setUp() = runBlocking(Dispatchers.IO) {
        storage.writeTemplates(
            cardId = CARD_ID_01,
            templates = mapOf(
                TEMPLATE_ID_01 to "data_01".toByteArray(),
                TEMPLATE_ID_02 to "data_02".toByteArray()
            )
        )
        storage.writeTemplates(
            cardId = CARD_ID_02,
            templates = mapOf(
                TEMPLATE_ID_03 to "data_03".toByteArray(),
                TEMPLATE_ID_04 to "data_04".toByteArray()
            )
        )
        storage.writeTemplates(
            cardId = CARD_ID_03,
            templates = mapOf(
                TEMPLATE_ID_01 to "data_01".toByteArray(),
                TEMPLATE_ID_03 to "data_03".toByteArray()
            )
        )
    }

    @After
    fun tearDown() {
        storage.close()
    }

    @Test
    fun `read templates for card`() = runBlocking(Dispatchers.IO) {
        val templates = storage.readTemplates(CARD_ID_01)
        assertEquals(
            setOf(TEMPLATE_ID_01, TEMPLATE_ID_02),
            templates.keys
        )
    }

    @Test
    fun `read templates by ids`() = runBlocking(Dispatchers.IO) {
        val templates = storage.readTemplatesByIds(TEMPLATE_ID_01, TEMPLATE_ID_02)
        assertEquals(
            setOf(TEMPLATE_ID_01, TEMPLATE_ID_02),
            templates.keys
        )
    }

    @Test
    fun `read all templates`() = runBlocking(Dispatchers.IO) {
        val templates = storage.readAllTemplates()
        assertEquals(
            setOf(TEMPLATE_ID_01, TEMPLATE_ID_02, TEMPLATE_ID_03, TEMPLATE_ID_04),
            templates.keys
        )
    }

    @Test
    fun `replace templates for card`() = runBlocking(Dispatchers.IO) {
        storage.writeTemplates(
            cardId = CARD_ID_01,
            templates = mapOf(
                TEMPLATE_ID_01 to "data_01".toByteArray(),
                TEMPLATE_ID_04 to "data_04".toByteArray()
            )
        )

        val templates = storage.readTemplates(CARD_ID_01)
        assertEquals(
            setOf(TEMPLATE_ID_01, TEMPLATE_ID_04),
            templates.keys
        )
    }

    @Test
    fun `remove templates for card`() = runBlocking(Dispatchers.IO) {
        storage.deleteTemplates(CARD_ID_01)

        val templates = storage.readTemplates(CARD_ID_01)
        assertTrue(templates.isEmpty())
    }

    @Test
    fun `don't remove used templates`() = runBlocking(Dispatchers.IO) {
        storage.deleteTemplates(CARD_ID_03)

        val templates = storage.readTemplates(CARD_ID_01)
        assertEquals(
            setOf(TEMPLATE_ID_01, TEMPLATE_ID_02),
            templates.keys
        )
    }

    @Test
    fun `remove unused templates`() = runBlocking(Dispatchers.IO) {
        storage.writeTemplates(
            cardId = CARD_ID_01,
            templates = mapOf(
                TEMPLATE_ID_01 to "data_01".toByteArray(),
                TEMPLATE_ID_04 to "data_04".toByteArray()
            )
        )

        val templates = storage.readAllTemplates()
        assertEquals(
            setOf(TEMPLATE_ID_01, TEMPLATE_ID_03, TEMPLATE_ID_04),
            templates.keys
        )
    }

    @Test
    fun `remove all templates`() = runBlocking(Dispatchers.IO) {
        storage.clear()

        val templates = storage.readAllTemplates()
        assertTrue(templates.isEmpty())
    }

    private companion object {

        private const val CARD_ID_01 = "card_01"
        private const val CARD_ID_02 = "card_02"
        private const val CARD_ID_03 = "card_03"

        private const val TEMPLATE_ID_01 = "template_01"
        private const val TEMPLATE_ID_02 = "template_02"
        private const val TEMPLATE_ID_03 = "template_03"
        private const val TEMPLATE_ID_04 = "template_04"
    }
}
