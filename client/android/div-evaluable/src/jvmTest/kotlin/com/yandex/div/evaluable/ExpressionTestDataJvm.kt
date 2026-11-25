package com.yandex.div.evaluable

import java.io.File

internal actual fun loadExpressionTestData(): Map<String, String> {
    val root = File(checkNotNull(System.getenv("DIVKIT_EXPRESSION_TEST_DATA")))
    check(root.isDirectory) { "Expression fixtures directory is missing: $root" }
    return root.walkTopDown()
        .filter { it.isFile && it.extension == "json" }
        .associate { it.relativeTo(root).invariantSeparatorsPath to it.readText() }
}
