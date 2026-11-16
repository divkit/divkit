package com.yandex.div.storage

internal expect fun temporaryDatabasePath(): String
internal expect fun removeDatabase(path: String)
