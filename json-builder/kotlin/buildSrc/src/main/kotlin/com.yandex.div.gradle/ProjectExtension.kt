@file:JvmName("ProjectExtensions")

package com.yandex.div.gradle

import org.gradle.api.Project
import org.gradle.internal.extensions.core.extra
import java.util.Properties

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

inline fun <reified T> Project.optProperty(name: String): T? {
    return findProperty(name) as? T
}
