package com.yandex.div.state.db

import android.provider.BaseColumns
import com.yandex.div.state.db.StateEntry.COLUMN_CARD_ID
import com.yandex.div.state.db.StateEntry.COLUMN_ID
import com.yandex.div.state.db.StateEntry.COLUMN_MOD_TIME
import com.yandex.div.state.db.StateEntry.COLUMN_PATH
import com.yandex.div.state.db.StateEntry.COLUMN_STATE_ID
import com.yandex.div.state.db.StateEntry.INDICES_NAME
import com.yandex.div.state.db.StateEntry.TABLE_NAME

internal object StateEntry : BaseColumns {
    const val TABLE_NAME = "div_card_states"

    const val COLUMN_ID = "id"
    const val COLUMN_CARD_ID = "card_id"
    const val COLUMN_PATH = "path"
    const val COLUMN_STATE_ID = "state_id"
    const val COLUMN_MOD_TIME = "modification_time"

    const val INDICES_NAME = "index_${TABLE_NAME}_${COLUMN_CARD_ID}_${COLUMN_PATH}"

    const val INIT_DB_VERSION = 1
}

internal object StateSchema {
    const val SQL_CREATE_TABLE_QUERY =
        "CREATE TABLE IF NOT EXISTS `$TABLE_NAME` (`$COLUMN_ID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `$COLUMN_CARD_ID` TEXT NOT NULL, `$COLUMN_PATH` TEXT NOT NULL, `$COLUMN_STATE_ID` TEXT NOT NULL, `$COLUMN_MOD_TIME` INTEGER NOT NULL)"
    const val SQL_CREATE_INDICES_TABLE_QUERY =
        "CREATE UNIQUE INDEX IF NOT EXISTS `$INDICES_NAME` ON `$TABLE_NAME` (`$COLUMN_CARD_ID`, `$COLUMN_PATH`)"

    const val SQL_DROP_TABLE_QUERY = "DROP TABLE IF EXISTS $TABLE_NAME"

    const val SQL_UPSERT_QUERY_TEMPLATE =
        "INSERT OR REPLACE INTO `$TABLE_NAME` (`$COLUMN_CARD_ID`,`$COLUMN_PATH`,`$COLUMN_STATE_ID`,`$COLUMN_MOD_TIME`) VALUES (?,?,?,?)"

    const val SQL_GET_STATES_QUERY_TEMPLATE =
        "SELECT $COLUMN_PATH, $COLUMN_STATE_ID " +
                "FROM $TABLE_NAME WHERE $COLUMN_CARD_ID=?"

    const val SQL_DELETE_BY_CARD_ID_QUERY_TEMPLATE =
        "DELETE FROM $TABLE_NAME WHERE $COLUMN_CARD_ID=?"

    const val SQL_DELETE_ALL_EXCEPT_CARD_ID_QUERY_TEMPLATE =
        "DELETE FROM $TABLE_NAME WHERE $COLUMN_CARD_ID NOT IN (%s)"

    const val SQL_DELETE_ALL_QUERY = "DELETE FROM $TABLE_NAME"

    const val SQL_DELETE_ALL_MODIFIED_BEFORE_QUERY_TEMPLATE =
        "DELETE FROM $TABLE_NAME WHERE $COLUMN_MOD_TIME < ?"

    const val SQL_GET_ROOT_STATE_ID_QUERY_TEMPLATE =
        "SELECT $COLUMN_STATE_ID FROM $TABLE_NAME WHERE $COLUMN_CARD_ID=? AND $COLUMN_PATH='/'"

    const val SQL_DELETE_CARD_ROOT_STATE_QUERY_TEMPLATE =
        "DELETE FROM $TABLE_NAME WHERE $COLUMN_CARD_ID=? AND $COLUMN_PATH='/'"
}
