package com.yandex.div.storage

import androidx.test.core.app.ApplicationProvider
import com.yandex.div.storage.DivStorage.LoadDataResult
import com.yandex.div.storage.DivStorage.RemoveResult
import com.yandex.div.storage.database.DB_VERSION
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val ID_0 = "card_id_0"
private const val ID_1 = "card_id_1"
private const val TEMPLATE_0 = "template_id_0"
private const val TEMPLATE_1 = "template_id_1"
private const val METADATA_ID = "metadata_id"

/**
 * Integration test for [DivStorage] default implementation.
 */
@RunWith(RobolectricTestRunner::class)
class DivStorageTest {
    private val rawDivData = JSONObject("""
        {
                    "type": "div2",
                    "log_id": "snapshot_test_card",
                    "states": [
                        {
                            "state_id": 0,
                            "div": {
                                "type": "text",
                                "text": "test"
                            }
                        }
                    ]
        }
    """.trimIndent())

    private val template0 = JSONObject().put("template", "0")
    private val template1 = JSONObject().put("template", "1")

    private val metadata0 = JSONObject().put(METADATA_ID, 0)
    private val metadata1 = JSONObject().put(METADATA_ID, 1)

    private val underTest = DivStorageComponent.createInternal(ApplicationProvider.getApplicationContext()).storage

    @Test
    fun `migration does exist for current database version`() {
        val migrationVersions = underTest.migrations.keys
        val migrationExist = migrationVersions.any { (_, newVersion) -> newVersion == DB_VERSION }

        Assert.assertTrue(migrationExist)
    }

    @Test
    fun `what is saved can be restored`() {
        val exceptions = underTest.saveData(
                groupId = "groupId",
                divs = listOf(
                        RawDataAndMetadata(
                                id = ID_0,
                                divData = rawDivData,
                        )
                ),
                templatesByHash = emptyList(),
                actionOnError = DivDataRepository.ActionOnError.ABORT_TRANSACTION
        ).errors

        Assert.assertTrue(exceptions.isEmpty())

        val loadDataResult = underTest.loadData(ID_0)
        Assert.assertEquals(1, loadDataResult.restoredData.size)
        val restoredRawData = loadDataResult.assertNoErrorsAndGetRestoredData().first()

        Assert.assertEquals(ID_0, restoredRawData.id)
        Assert.assertEquals(rawDivData.toString(), restoredRawData.divData.toString())
    }

    @Test
    fun `save-restore multiple data`() {
        var exceptions = underTest.saveData(
                groupId = "groupId",
                divs = listOf(RawDataAndMetadata(ID_0, rawDivData)),
                templatesByHash = emptyList(),
                actionOnError = DivDataRepository.ActionOnError.ABORT_TRANSACTION
        ).errors
        Assert.assertTrue(exceptions.isEmpty())
        exceptions = underTest.saveData(
                groupId = "groupId",
                divs = listOf(RawDataAndMetadata(ID_1, rawDivData)),
                templatesByHash = emptyList(),
                actionOnError = DivDataRepository.ActionOnError.ABORT_TRANSACTION
        ).errors
        Assert.assertTrue(exceptions.isEmpty())

        val restoredRawData = underTest.loadData(ID_0, ID_1).assertNoErrorsAndGetRestoredData()

        Assert.assertEquals(2, restoredRawData.size)
        Assert.assertEquals(ID_0, restoredRawData[0].id)
        Assert.assertEquals(ID_1, restoredRawData[1].id)
    }

    @Test
    fun `erasing data by id`() {
        val exceptions = underTest.saveData(
                groupId = "groupId",
                divs = listOf(
                        RawDataAndMetadata(ID_0, rawDivData),
                        RawDataAndMetadata(ID_1, rawDivData),
                ),
                templatesByHash = emptyList(),
                actionOnError = DivDataRepository.ActionOnError.ABORT_TRANSACTION
        ).errors
        Assert.assertTrue(exceptions.isEmpty())

        val deletedRecords = underTest.remove { rawDataAndMetadata ->
            rawDataAndMetadata.id == ID_1
        }.assertNoErrorsAndGetRemovedIds()
        val remainingRecords = underTest.loadData().assertNoErrorsAndGetRestoredData().map { it.id }

        Assert.assertArrayEquals(listOf(ID_1).toTypedArray(), deletedRecords.toTypedArray())
        Assert.assertArrayEquals(listOf(ID_0).toTypedArray(), remainingRecords.toTypedArray())
    }

    @Test
    fun `erasing data by metadata content`() {
        val exceptions = underTest.saveData("groupId",
                divs = listOf(
                        RawDataAndMetadata(ID_0, rawDivData, metadata0),
                        RawDataAndMetadata(ID_1, rawDivData, metadata1),
                ),
                templatesByHash = emptyList(),
                actionOnError = DivDataRepository.ActionOnError.ABORT_TRANSACTION
        ).errors
        Assert.assertTrue(exceptions.isEmpty())

        val deletedRecords = underTest.remove { rawDataAndMetadata ->
            rawDataAndMetadata.metadata?.getInt(METADATA_ID) == 0
        }.assertNoErrorsAndGetRemovedIds()
        val remainingRecords = underTest.loadData().assertNoErrorsAndGetRestoredData().map { it.id }

        Assert.assertArrayEquals(listOf(ID_0).toTypedArray(), deletedRecords.toTypedArray())
        Assert.assertArrayEquals(listOf(ID_1).toTypedArray(), remainingRecords.toTypedArray())
    }

    @Test
    fun `erasing data by DivData content`() {
        val divDataDeleteField = "to_be_deleted"
        val exceptions = underTest.saveData("groupId",
                divs = listOf(
                        RawDataAndMetadata(ID_1, JSONObject(rawDivData.toString()).put(divDataDeleteField, true)),
                        RawDataAndMetadata(ID_0, JSONObject(rawDivData.toString()).put(divDataDeleteField, false)),
                ),
                templatesByHash = emptyList(),
                actionOnError = DivDataRepository.ActionOnError.ABORT_TRANSACTION
        ).errors
        Assert.assertTrue(exceptions.isEmpty())

        val deletedRecords = underTest.remove { rawDataAndMetadata ->
            rawDataAndMetadata.divData.getBoolean(divDataDeleteField)
        }.assertNoErrorsAndGetRemovedIds()
        val remainingRecords = underTest.loadData().assertNoErrorsAndGetRestoredData().map { it.id }

        Assert.assertArrayEquals(listOf(ID_1).toTypedArray(), deletedRecords.toTypedArray())
        Assert.assertArrayEquals(listOf(ID_0).toTypedArray(), remainingRecords.toTypedArray())
    }

    private fun <T> LoadDataResult<T>.assertNoErrorsAndGetRestoredData(): List<T> {
        Assert.assertTrue(errors.isEmpty())
        return restoredData
    }

    private fun RemoveResult.assertNoErrorsAndGetRemovedIds(): Set<String> {
        Assert.assertTrue(errors.isEmpty())
        return ids
    }

    private fun DivStorage.loadData(vararg ids: String) = loadData(ids.toList())
}
