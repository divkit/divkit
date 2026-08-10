package com.yandex.test.util

import org.gradle.api.Project

internal val Project.reportDir
    get() = layout.buildDirectory.dir("reports")
