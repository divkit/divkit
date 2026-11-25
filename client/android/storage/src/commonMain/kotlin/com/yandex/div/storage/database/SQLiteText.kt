package com.yandex.div.storage.database

import androidx.sqlite.SQLiteStatement

internal expect fun SQLiteStatement.readText(index: Int): String
