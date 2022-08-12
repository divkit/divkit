// Copyright (c) 2022 Yandex LLC. All rights reserved.

package com.yandex.div.dsl

import java.nio.file.Files
import java.nio.file.Path

internal fun readResource(path: String): String {
    return Files.readString(Path.of(object {}.javaClass.getResource(path).toURI()))
}
