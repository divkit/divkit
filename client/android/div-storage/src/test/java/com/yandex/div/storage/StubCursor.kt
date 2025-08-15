package com.yandex.div.storage

import android.content.ContentResolver
import android.database.CharArrayBuffer
import android.database.ContentObserver
import android.database.Cursor
import android.database.DataSetObserver
import android.net.Uri
import android.os.Bundle

abstract class StubCursor: Cursor {
    override fun close() {}

    override fun getCount(): Int = 1

    override fun isClosed(): Boolean = false

    override fun move(offset: Int): Boolean = true

    override fun moveToFirst(): Boolean = true

    override fun getPosition(): Int = throw NotImplementedError()
    override fun moveToPosition(position: Int): Boolean = throw NotImplementedError()
    override fun moveToLast(): Boolean = throw NotImplementedError()
    override fun moveToPrevious(): Boolean = throw NotImplementedError()
    override fun isFirst(): Boolean = throw NotImplementedError()
    override fun isLast(): Boolean = throw NotImplementedError()
    override fun isBeforeFirst(): Boolean = throw NotImplementedError()
    override fun isAfterLast(): Boolean = throw NotImplementedError()
    override fun getColumnIndexOrThrow(columnName: String?): Int = throw NotImplementedError()
    override fun getColumnName(columnIndex: Int): String = throw NotImplementedError()
    override fun getColumnNames(): Array<String> = throw NotImplementedError()
    override fun getColumnCount(): Int = throw NotImplementedError()
    override fun copyStringToBuffer(columnIndex: Int, buffer: CharArrayBuffer?) =
            throw NotImplementedError()
    override fun getShort(columnIndex: Int): Short = throw NotImplementedError()
    override fun getInt(columnIndex: Int): Int = throw NotImplementedError()
    override fun getLong(columnIndex: Int): Long = throw NotImplementedError()
    override fun getFloat(columnIndex: Int): Float = throw NotImplementedError()
    override fun getDouble(columnIndex: Int): Double = throw NotImplementedError()
    override fun getType(columnIndex: Int): Int = throw NotImplementedError()
    override fun deactivate() = throw NotImplementedError()
    override fun requery(): Boolean = throw NotImplementedError()
    override fun registerContentObserver(observer: ContentObserver?) = throw NotImplementedError()
    override fun unregisterContentObserver(observer: ContentObserver?) = throw NotImplementedError()
    override fun registerDataSetObserver(observer: DataSetObserver?) = throw NotImplementedError()
    override fun unregisterDataSetObserver(observer: DataSetObserver?) = throw NotImplementedError()
    override fun setNotificationUri(cr: ContentResolver?, uri: Uri?) = throw NotImplementedError()
    override fun getNotificationUri(): Uri = throw NotImplementedError()
    override fun getWantsAllOnMoveCalls(): Boolean = throw NotImplementedError()
    override fun setExtras(extras: Bundle?) = throw NotImplementedError()
    override fun getExtras(): Bundle = throw NotImplementedError()
    override fun respond(extras: Bundle?): Bundle = throw NotImplementedError()
}
