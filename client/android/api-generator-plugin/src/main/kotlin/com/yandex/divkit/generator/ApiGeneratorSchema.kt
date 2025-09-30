package com.yandex.divkit.generator

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

abstract class ApiGeneratorSchema @Inject constructor(
    val name: String,
    objects: ObjectFactory
) {
    val config: RegularFileProperty = objects.fileProperty()
    val schemas: DirectoryProperty = objects.directoryProperty()
}
