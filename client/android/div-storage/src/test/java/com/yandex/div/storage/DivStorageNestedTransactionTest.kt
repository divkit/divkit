package com.yandex.div.storage

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.storage.rawjson.RawJson
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.json.JSONObject
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DivStorageNestedTransactionTest {
    private val storage = DivStorageComponent.createInternal(
        ApplicationProvider.getApplicationContext()
    ).storage

    @Test
    fun `card removal reports rollback after a handled nested failure`() {
        storage.saveData("group", listOf(RawDataAndMetadata("card", JSONObject())),
            emptyList(), DivDataRepository.ActionOnError.ABORT_TRANSACTION)

        val result = storage.remove {
            val nested = storage.remove { error("nested predicate failed") }
            assertTrue(nested.errors.isNotEmpty())
            true
        }

        assertTrue(result.errors.isNotEmpty())
        assertTrue(result.ids.isEmpty())
        assertEquals(listOf("card"), storage.loadData(listOf("card"), emptyList())
            .restoredData.map { it.id })
    }

    @Test
    fun `raw removal reports rollback after a handled nested failure`() {
        storage.saveRawJsons(listOf(RawJson("raw", JSONObject())),
            DivDataRepository.ActionOnError.ABORT_TRANSACTION)

        val result = storage.removeRawJsons {
            val nested = storage.removeRawJsons { error("nested predicate failed") }
            assertTrue(nested.errors.isNotEmpty())
            true
        }

        assertTrue(result.errors.isNotEmpty())
        assertTrue(result.ids.isEmpty())
        assertEquals(listOf("raw"), storage.readRawJsons(setOf("raw"))
            .restoredData.map { it.id })
    }
    @Test
    fun `raw removal commits normally after a handled child rollback`() {
        storage.saveRawJsons(listOf(RawJson("raw", JSONObject())),
            DivDataRepository.ActionOnError.ABORT_TRANSACTION)
        val rolledBack = storage.removeRawJsons {
            storage.removeRawJsons { error("nested predicate failed") }
            true
        }
        assertTrue(rolledBack.errors.isNotEmpty())

        val result = storage.removeRawJsons { true }

        assertTrue(result.errors.isEmpty())
        assertEquals(setOf("raw"), result.ids)
        assertTrue(storage.readRawJsons(setOf("raw")).restoredData.isEmpty())
    }

}
