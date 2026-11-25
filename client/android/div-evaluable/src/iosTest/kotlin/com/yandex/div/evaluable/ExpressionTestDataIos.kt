@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.yandex.div.evaluable

import kotlinx.cinterop.readBytes
import kotlinx.cinterop.toKString
import platform.Foundation.NSFileManager
import platform.posix.getenv

internal actual fun loadExpressionTestData(): Map<String, String> {
    val root = checkNotNull(getenv("DIVKIT_EXPRESSION_TEST_DATA")?.toKString())
    val files = NSFileManager.defaultManager
    val entries = checkNotNull(files.subpathsAtPath(root)) { "Expression fixtures directory is missing: $root" }
    return entries.filterIsInstance<String>().filter { it.endsWith(".json") }.associateWith { path ->
        val data = checkNotNull(files.contentsAtPath("$root/$path")) {
            "Unable to read expression fixture: $root/$path"
        }
        data.bytes?.readBytes(data.length.toInt())?.decodeToString(throwOnInvalidSequence = true).orEmpty()
    }
}
