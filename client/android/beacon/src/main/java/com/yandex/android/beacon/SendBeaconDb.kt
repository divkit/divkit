package com.yandex.android.beacon

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.provider.BaseColumns
import androidx.annotation.VisibleForTesting
import androidx.annotation.WorkerThread
import com.yandex.android.beacon.BeaconItem.Persistent
import com.yandex.android.beacon.SendBeaconDb.Factory
import com.yandex.android.util.deserializeMap
import com.yandex.android.util.serialize
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.util.Assert
import org.json.JSONException
import org.json.JSONObject

@Mockable
internal class SendBeaconDb constructor(
    context: Context,
    databaseName: String
) : SQLiteOpenHelper(context, databaseName, null, DATABASE_VERSION) {

    init {
        Assert.assertTrue(context is Application)
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion == DATABASE_INIT_VERSION) {
            sqLiteDatabase.execSQL(ADD_PAYLOAD_COLUMN_TO_ITEM)
        }
    }

    @WorkerThread
    fun allItems(): List<Persistent> {
        val result = mutableListOf<Persistent>()
        val db = readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.query(TABLE_ITEMS, QUERY_COLUMNS, null, null, null, null, null, null)
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    result.add(beaconItemFromCursor(cursor))
                }
            }
        } finally {
            cursor?.close()
            db.close()
        }
        return result
    }

    @WorkerThread
    fun add(url: Uri, headers: Map<String, String>, addTimestamp: Long, payload: JSONObject?): Persistent {
        val contentValues = ContentValues(3).apply {
            put(COLUMN_URL, url.toString())
            put(COLUMN_HEADERS, headers.serialize())
            put(COLUMN_ADD_TIMESTAMP, addTimestamp)
        }

        val database = writableDatabase
        val rowId = database.use {
            database.insert(TABLE_ITEMS, null, contentValues)
        }
        return Persistent(url, headers, payload, addTimestamp, rowId)
    }

    @WorkerThread
    fun remove(item: Persistent?): Boolean {
        if (item == null) return false

        val database = writableDatabase
        val res = database.use {
            database.delete(TABLE_ITEMS, REMOVE_CLAUSE, arrayOf(item.rowId.toString()))
        }
        return res != 0
    }

    private fun beaconItemFromCursor(cursor: Cursor): Persistent {
        return Persistent(
            Uri.parse(cursor.getString(1)),
            cursor.getString(2).deserializeMap(),
            cursor.getNullableJson(4),
            cursor.getLong(3),
            cursor.getLong(0)
        )
    }

    fun interface Factory {

        fun create(context: Context, databaseName: String): SendBeaconDb
    }

    companion object {

        private const val DATABASE_VERSION = 2
        private const val DATABASE_INIT_VERSION = 1

        private const val TABLE_ITEMS = "items"
        private const val COLUMN_ID = BaseColumns._ID
        private const val COLUMN_URL = "url"
        private const val COLUMN_HEADERS = "headers"
        private const val COLUMN_ADD_TIMESTAMP = "add_timestamp"
        private const val COLUMN_PAYLOAD = "payload"

        private const val DATABASE_CREATE = """
            CREATE TABLE $TABLE_ITEMS(
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_URL TEXT NOT NULL,
            $COLUMN_HEADERS TEXT,
            $COLUMN_ADD_TIMESTAMP INTEGER, 
            $COLUMN_PAYLOAD TEXT)"""

        private const val ADD_PAYLOAD_COLUMN_TO_ITEM = """
            ALTER TABLE $TABLE_ITEMS ADD COLUMN $COLUMN_PAYLOAD TEXT;
        """

        private val QUERY_COLUMNS = arrayOf(COLUMN_ID, COLUMN_URL, COLUMN_HEADERS, COLUMN_ADD_TIMESTAMP, COLUMN_PAYLOAD)
        private const val REMOVE_CLAUSE = "$COLUMN_ID = ?"

        @JvmField
        @VisibleForTesting
        internal var factory: Factory = Factory(::SendBeaconDb)
    }

    private fun Cursor.getNullableString(columnIndex: Int): String? =
        if (isNull(columnIndex)) null else getString(columnIndex)

    private fun Cursor.getNullableJson(columnIndex: Int): JSONObject? {
        return getNullableString(columnIndex)?.let {
            if (it.isNotEmpty()) {
                try {
                    JSONObject(it)
                } catch (e : JSONException) {
                    Assert.fail("Payload parsing exception: $e")
                    null
                }
            } else {
                null
            }
        }
    }
}
