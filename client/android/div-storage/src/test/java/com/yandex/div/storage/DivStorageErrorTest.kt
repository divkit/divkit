package com.yandex.div.storage

import androidx.test.core.app.ApplicationProvider
import com.yandex.div.storage.database.AndroidDatabaseOpenHelper
import com.yandex.div.storage.database.AndroidDatabaseOpenHelperProvider
import com.yandex.div.storage.database.DatabaseOpenHelper
import com.yandex.div.storage.database.asStorageCursor
import java.util.UUID
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

private const val ID_1 = "card_id_1"

/**
 * Integration test for [DivStorage] default implementation.
 */
@RunWith(RobolectricTestRunner::class)
class DivStorageErrorTest {
    @Test
    fun `loadData rethrows SQL errors other than database locking`() {
        val cause = android.database.SQLException("database failure")
        whenCardsTableQuery().thenThrow(cause)

        val error = kotlin.test.assertFailsWith<android.database.SQLException> {
            underTest.loadData(ID_1)
        }

        kotlin.test.assertSame(cause, error)
    }

    @Test
    fun `loadData returns database locking errors`() {
        val cause = android.database.sqlite.SQLiteDatabaseLockedException("database locked")
        whenCardsTableQuery().thenThrow(cause)

        val result = underTest.loadData(ID_1)

        kotlin.test.assertTrue(result.restoredData.isEmpty())
        kotlin.test.assertSame(cause, result.errors.single().cause)
    }

    @kotlin.test.Test
    fun `statement error preserves the legacy SQL cause chain`() {
        val cause = android.database.SQLException("database failure")
        doThrow(cause).whenever(db).compileStatement(any())

        val result = underTest.saveData(
            "group", emptyList(), emptyList(), DivDataRepository.ActionOnError.ABORT_TRANSACTION
        )

        val error = kotlin.test.assertIs<DivStorageErrorException>(result.errors.single())
        kotlin.test.assertSame(cause, error.cause?.cause)
    }

    @kotlin.test.Test
    fun `existence check does not expose common exception types to Android`() {
        doThrow(android.database.SQLException("database failure"))
            .whenever(db).rawQuery(any(), anyOrNull())

        val error = kotlin.test.assertFailsWith<DivStorageErrorException> {
            underTest.isCardExists("card", "group")
        }

        kotlin.test.assertIs<DivStorageErrorException>(error.cause)
    }
    private var dbHelper: AndroidDatabaseOpenHelper? = null
    private var db: DatabaseOpenHelper.Database = mock()
    private val openHelperProvider: AndroidDatabaseOpenHelperProvider =
            AndroidDatabaseOpenHelperProvider { context, name, version, ccb, ucb ->
                dbHelper ?: spy(AndroidDatabaseOpenHelper(context, name, version, ccb, ucb, mock())).apply {
                    whenever(writableDatabase).doReturn(db)
                    whenever(readableDatabase).doReturn(db)
                }.also { dbHelper = it }
            }
    private val underTest = AndroidDivStorage(
            context = ApplicationProvider.getApplicationContext(),
            openHelperProvider = openHelperProvider,
    )

    @Test
    fun `DivData is null`() {
        whenCardsTableQuery().doReturn(createCursor(
                listOf(RestoredData(
                        cardData = null,
                        metaData = null
                ))
        ))
        val loadDataResult = underTest.loadData(ID_1)
        Assert.assertEquals(0, loadDataResult.restoredData.size)
        Assert.assertEquals(1, loadDataResult.errors.size)
    }

    @Test
    fun `DivData is invalid`() {
        whenCardsTableQuery().doReturn(createCursor(
                listOf(RestoredData(
                        cardData = invalidByteArray(),
                        metaData = null
                ))
        ))
        val loadDataResult = underTest.loadData(ID_1)
        Assert.assertEquals(0, loadDataResult.restoredData.size)
        Assert.assertEquals(1, loadDataResult.errors.size)
    }

    @Test
    fun `metadata is invalid`() {
        whenCardsTableQuery().doReturn(createCursor(
                listOf(RestoredData(
                        cardData = validByteArray(),
                        metaData = invalidByteArray()
                ))
        ))

        val loadDataResult = underTest.loadData(ID_1)
        Assert.assertEquals(1, loadDataResult.restoredData.size)
        Assert.assertEquals(1, loadDataResult.errors.size)
    }

    private class RestoredData(
            val cardData: ByteArray?,
            val metaData: ByteArray?,
            val id: String = "id-" + UUID.randomUUID().toString(),
            val groupId: String = "groupId-" + UUID.randomUUID().toString()
    )

    private fun whenCardsTableQuery() = whenever(db.query(
            argThat { this == "cards" },
            anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(),
            anyOrNull(), anyOrNull(), anyOrNull()
    ))

    private fun createCursor(listData: List<RestoredData>): DatabaseOpenHelper.Cursor {
        return object: StubCursor() {
            private var countMovingToNext = 0
            override fun moveToNext(): Boolean {
                return countMovingToNext++ < listData.size - 1
            }

            override fun getColumnIndex(columnName: String?): Int {
                return when(columnName) {
                    "card_id", "layout_id" -> 0
                    "group_id" -> 1
                    "card_data" -> 2
                    "metadata" -> 3
                    else -> TODO()
                }
            }

            override fun getBlob(columnIndex: Int): ByteArray {
                return when(columnIndex) {
                    2 -> listData[countMovingToNext].cardData!!
                    3 -> listData[countMovingToNext].metaData!!
                    else -> TODO()
                }
            }

            override fun getString(columnIndex: Int): String {
                return when(columnIndex) {
                    0 -> listData[countMovingToNext].id
                    1 -> listData[countMovingToNext].groupId
                    else -> TODO()
                }
            }

            override fun isNull(columnIndex: Int): Boolean {
                return when(columnIndex) {
                    2 -> listData[countMovingToNext].cardData
                    3 -> listData[countMovingToNext].metaData
                    else -> TODO()
                } == null
            }
        }.asStorageCursor()
    }

    private fun validByteArray() = JSONObject().toString().toByteArray()
    private fun invalidByteArray() = byteArrayOf(1.toByte(), 2.toByte())

    private fun DivStorage.loadData(vararg ids: String) = loadData(ids.toList(), emptyList())
}
