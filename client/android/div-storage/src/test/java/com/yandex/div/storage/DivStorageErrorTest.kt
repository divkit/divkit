package com.yandex.div.storage

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.storage.database.AndroidDatabaseOpenHelper
import com.yandex.div.storage.database.COLUMN_CARD_DATA
import com.yandex.div.storage.database.COLUMN_CARD_GROUP_ID
import com.yandex.div.storage.database.COLUMN_CARD_ID
import com.yandex.div.storage.database.COLUMN_CARD_METADATA
import com.yandex.div.storage.database.COLUMN_LAYOUT_ID
import com.yandex.div.storage.database.DatabaseOpenHelper
import com.yandex.div.storage.database.DatabaseOpenHelperProvider
import com.yandex.div.storage.database.TABLE_CARDS
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import java.util.UUID

private const val ID_1 = "card_id_1"

/**
 * Integration test for [DivStorage] default implementation.
 */
@RunWith(RobolectricTestRunner::class)
class DivStorageErrorTest {
    private var dbHelper: AndroidDatabaseOpenHelper? = null
    private var db: DatabaseOpenHelper.Database = mock()
    private val openHelperProvider: DatabaseOpenHelperProvider =
            DatabaseOpenHelperProvider { context, name, version, ccb, ucb ->
                dbHelper ?: spy(AndroidDatabaseOpenHelper(context, name, version, ccb, ucb)).apply {
                    whenever(writableDatabase).doReturn(db)
                    whenever(readableDatabase).doReturn(db)
                }.also { dbHelper = it }
            }
    private val underTest = DivStorageImpl(
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
            argThat { this == TABLE_CARDS },
            anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(),
            anyOrNull(), anyOrNull(), anyOrNull()
    ))

    private fun createCursor(listData: List<RestoredData>): Cursor {
        return object: StubCursor() {
            private var countMovingToNext = 0
            override fun moveToNext(): Boolean {
                return countMovingToNext++ < listData.size - 1
            }

            override fun getColumnIndex(columnName: String?): Int {
                return when(columnName) {
                    COLUMN_CARD_ID, COLUMN_LAYOUT_ID -> 0
                    COLUMN_CARD_GROUP_ID -> 1
                    COLUMN_CARD_DATA -> 2
                    COLUMN_CARD_METADATA -> 3
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
        }
    }

    private fun validByteArray() = JSONObject().toString().toByteArray()
    private fun invalidByteArray() = byteArrayOf(1.toByte(), 2.toByte())

    private fun DivStorage.loadData(vararg ids: String) = loadData(ids.toList(), emptyList())
}
