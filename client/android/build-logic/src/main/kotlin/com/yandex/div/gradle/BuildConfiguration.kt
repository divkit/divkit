package com.yandex.div.gradle

import org.gradle.api.Project
import java.lang.Boolean.parseBoolean

fun Project.disableLogs(): Boolean = getBooleanProperty("disableLogsInBuild")

fun Project.disableAsserts(): Boolean = getBooleanProperty("disableAssertsInBuild")

private fun Project.getBooleanProperty(name: String): Boolean {
    return providers.gradleProperty(name)
        .map(::parseBoolean)
        .getOrElse(false)
}
