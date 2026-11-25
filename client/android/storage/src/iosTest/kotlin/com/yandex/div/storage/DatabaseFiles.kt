package com.yandex.div.storage

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSUUID

internal actual fun temporaryDatabasePath(): String {
    val path = NSTemporaryDirectory().trimEnd('/') + "/div-storage-${NSUUID().UUIDString}.db"
    check(NSFileManager.defaultManager.createFileAtPath(path, contents = null, attributes = null))
    return path
}

@OptIn(ExperimentalForeignApi::class)
internal actual fun removeDatabase(path: String) {
    val files = NSFileManager.defaultManager
    if (files.fileExistsAtPath(path)) {
        check(files.removeItemAtPath(path, error = null)) { "Failed to remove test database: $path" }
    }
}
