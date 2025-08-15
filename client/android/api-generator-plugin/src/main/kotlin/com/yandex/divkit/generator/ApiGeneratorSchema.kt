package com.yandex.divkit.generator

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import javax.inject.Inject

abstract class ApiGeneratorSchema @Inject constructor(
    val name: String,
) {
    abstract val config: RegularFileProperty
    abstract val schemas: DirectoryProperty
}
