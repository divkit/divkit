package com.yandex.div.storage.database

import androidx.sqlite.SQLiteStatement

internal actual fun SQLiteStatement.readText(index: Int): String = getText(index)
