package com.yandex.div.state.db

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import androidx.core.database.getStringOrNull
import com.yandex.div.core.util.Assert
import com.yandex.div.state.db.StateSchema.SQL_UPSERT_QUERY_TEMPLATE

internal class DivStateDaoImpl(
    private val writableDatabase: SQLiteDatabase
) : DivStateDao {

    private val upsertStatement: SQLiteStatement

    init {
        if (writableDatabase.isReadOnly) {
            Assert.fail("${this.javaClass.name} require writable database!")
        }
        upsertStatement = writableDatabase.compileStatement(SQL_UPSERT_QUERY_TEMPLATE)
    }

    override fun getStates(cardId: String): List<PathToState> {
        val states = mutableListOf<PathToState>()
        writableDatabase.inTransaction {
            val cursor = writableDatabase.rawQuery(
                StateSchema.SQL_GET_STATES_QUERY_TEMPLATE,
                arrayOf(cardId)
            )
            cursor.use {
                while (cursor.moveToNext()) {
                    states.add(PathToState(cursor.getPath(), cursor.getStateId()))
                }
            }
        }

        return states
    }

    override fun updateState(state: DivStateEntity) {
        writableDatabase.inTransaction {
            upsertStatement.bindString(1, state.cardId)
            upsertStatement.bindString(2, state.path)
            upsertStatement.bindString(3, state.stateId)
            upsertStatement.bindString(4, state.modificationTime.toString())
            upsertStatement.execute()
            upsertStatement.clearBindings()
        }
    }

    override fun deleteAll() {
        writableDatabase.inTransaction {
            writableDatabase.rawQuery(StateSchema.SQL_DELETE_ALL_QUERY, emptyArray()).close()
        }
    }

    override fun deleteByCardId(cardId: String) {
        writableDatabase.inTransaction {
            writableDatabase.rawQuery(
                StateSchema.SQL_DELETE_BY_CARD_ID_QUERY_TEMPLATE,
                arrayOf(cardId)
            ).applyAndClose()
        }
    }

    override fun deleteAllExcept(cardIds: List<String>) {
        writableDatabase.inTransaction {
            val sqlInClauseTemplate =
                Array(cardIds.size) { "?" }.joinToString(",")
            writableDatabase.rawQuery(
                StateSchema.SQL_DELETE_ALL_EXCEPT_CARD_ID_QUERY_TEMPLATE.format(sqlInClauseTemplate),
                cardIds.toTypedArray()).applyAndClose()
        }
    }

    override fun deleteModifiedBefore(timestamp: Long) {
        writableDatabase.inTransaction {
            writableDatabase.rawQuery(
                StateSchema.SQL_DELETE_ALL_MODIFIED_BEFORE_QUERY_TEMPLATE,
                arrayOf(timestamp.toString())
            ).applyAndClose()
        }
    }

    override fun deleteCardRootState(cardId: String) {
        writableDatabase.inTransaction {
            writableDatabase.rawQuery(
                StateSchema.SQL_DELETE_CARD_ROOT_STATE_QUERY_TEMPLATE,
                arrayOf(cardId)
            ).applyAndClose()
        }
    }

    override fun getRootStateId(cardId: String): String? {
        var stateId: String? = null
        writableDatabase.inTransaction {
            val cursor = writableDatabase.rawQuery(
                StateSchema.SQL_GET_ROOT_STATE_ID_QUERY_TEMPLATE,
                arrayOf(cardId)
            )
            cursor.use {
                if (cursor.moveToNext()) {
                    stateId = cursor.getStateIdOrNull()
                }
            }
        }
        return stateId
    }

    private fun Cursor.getPath() = getString(getColumnIndexOrThrow(StateEntry.COLUMN_PATH))
    private fun Cursor.getStateId() = getString(getColumnIndexOrThrow(StateEntry.COLUMN_STATE_ID))
    private fun Cursor.getStateIdOrNull() = getStringOrNull(getColumnIndexOrThrow(StateEntry.COLUMN_STATE_ID))
    private fun Cursor.applyAndClose() {
        moveToLast() // Idiotic way to commit transaction? If remove this - nothing gonna be written.
        close()
    }

    private fun SQLiteDatabase.inTransaction(block: () -> Unit) {
        beginTransaction()
        try {
            block()
            setTransactionSuccessful()
        } finally {
            endTransaction()
        }
    }
}
