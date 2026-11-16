package com.yandex.div.storage

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DivDatabaseStorageTest {

    private val path = temporaryDatabasePath()
    private val storage = BaseDivDatabaseStorage(
        openHelperProvider = { _, version, create, upgrade ->
            com.yandex.div.storage.database.SQLiteDatabaseOpenHelper(
                androidx.sqlite.driver.bundled.BundledSQLiteDriver(), path, version, create, upgrade
            )
        },
        databaseName = "test-div.db",
    )

    @BeforeTest
    fun setUp() {
        storage.writeTemplates(
            cardId = CARD_ID_01,
            templates = mapOf(
                TEMPLATE_ID_01 to "data_01".encodeToByteArray(),
                TEMPLATE_ID_02 to "data_02".encodeToByteArray()
            )
        )
        storage.writeTemplates(
            cardId = CARD_ID_02,
            templates = mapOf(
                TEMPLATE_ID_03 to "data_03".encodeToByteArray(),
                TEMPLATE_ID_04 to "data_04".encodeToByteArray()
            )
        )
        storage.writeTemplates(
            cardId = CARD_ID_03,
            templates = mapOf(
                TEMPLATE_ID_01 to "data_01".encodeToByteArray(),
                TEMPLATE_ID_03 to "data_03".encodeToByteArray()
            )
        )
    }

    @AfterTest
    fun tearDown() {
        storage.close()
        removeDatabase(path)
    }

    @Test
    fun `read templates for card`() {
        val templates = storage.readTemplates(CARD_ID_01)
        assertEquals(
            setOf(TEMPLATE_ID_01, TEMPLATE_ID_02),
            templates.keys
        )
    }

    @Test
    fun `read templates by ids`() {
        val templates = storage.readTemplatesByIds(TEMPLATE_ID_01, TEMPLATE_ID_02)
        assertEquals(
            setOf(TEMPLATE_ID_01, TEMPLATE_ID_02),
            templates.keys
        )
    }

    @Test
    fun `read all templates`() {
        val templates = storage.readAllTemplates()
        assertEquals(
            setOf(TEMPLATE_ID_01, TEMPLATE_ID_02, TEMPLATE_ID_03, TEMPLATE_ID_04),
            templates.keys
        )
    }

    @Test
    fun `replace templates for card`() {
        storage.writeTemplates(
            cardId = CARD_ID_01,
            templates = mapOf(
                TEMPLATE_ID_01 to "data_01".encodeToByteArray(),
                TEMPLATE_ID_04 to "data_04".encodeToByteArray()
            )
        )

        val templates = storage.readTemplates(CARD_ID_01)
        assertEquals(
            setOf(TEMPLATE_ID_01, TEMPLATE_ID_04),
            templates.keys
        )
    }

    @Test
    fun `remove templates for card`() {
        storage.deleteTemplates(CARD_ID_01)

        val templates = storage.readTemplates(CARD_ID_01)
        assertTrue(templates.isEmpty())
    }

    @Test
    fun `don't remove used templates`() {
        storage.deleteTemplates(CARD_ID_03)

        val templates = storage.readTemplates(CARD_ID_01)
        assertEquals(
            setOf(TEMPLATE_ID_01, TEMPLATE_ID_02),
            templates.keys
        )
    }

    @Test
    fun `remove unused templates`() {
        storage.writeTemplates(
            cardId = CARD_ID_01,
            templates = mapOf(
                TEMPLATE_ID_01 to "data_01".encodeToByteArray(),
                TEMPLATE_ID_04 to "data_04".encodeToByteArray()
            )
        )

        val templates = storage.readAllTemplates()
        assertEquals(
            setOf(TEMPLATE_ID_01, TEMPLATE_ID_03, TEMPLATE_ID_04),
            templates.keys
        )
    }

    @Test
    fun `remove all templates`() {
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
