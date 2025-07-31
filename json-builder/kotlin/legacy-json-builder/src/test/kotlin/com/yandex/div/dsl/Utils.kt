package com.yandex.div.dsl

import java.nio.file.Files
import java.nio.file.Path

fun readResource(path: String): String {
    return Files.readString(Path.of(object {}.javaClass.getResource(path).toURI()))
}
