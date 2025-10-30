@file:JvmName("ProjectExtensions")

package com.yandex.divkit.gradle

import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.internal.extensions.core.extra
import java.util.Properties

val Project.sourceSets: SourceSetContainer
    get() = extensions.getByName("sourceSets") as SourceSetContainer

fun Project.applyProperties(from: String) {
    val propertiesFile = layout.projectDirectory.file(from)
    if (propertiesFile.asFile.exists()) {
        val propertiesFileContent = providers.fileContents(propertiesFile).asText
        val properties = Properties().apply {
            load(propertiesFileContent.get().reader())
        }
        properties.forEach { (key, value) ->
            if (key is String) extra.set(key, value)
        }
    }
}

fun Project.optStringProperty(name: String): String? {
    return findProperty(name) as? String
}

fun Project.optBooleanProperty(name: String): Boolean? {
    return optStringProperty(name)?.toBoolean()
}
