package com.yandex.div.storage

import java.nio.file.Files
import java.nio.file.Path

internal actual fun temporaryDatabasePath(): String = Files.createTempFile("div-storage-", ".db").toString()

internal actual fun removeDatabase(path: String) {
    Files.deleteIfExists(Path.of(path))
}
