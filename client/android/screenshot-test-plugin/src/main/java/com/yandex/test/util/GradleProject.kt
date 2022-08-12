package com.yandex.test.util

import org.gradle.api.Project
import java.io.File

internal val Project.reportDir
    get() = File(buildDir, "reports")
