package com.yandex.test.screenshot

import com.android.build.api.variant.Variant
import com.yandex.test.screenshot.tasks.CompareScreenshotsTask
import com.yandex.test.util.androidComponents
import org.gradle.api.Plugin
import org.gradle.api.Project

class ScreenshotTestPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create(
            ScreenshotTestPluginExtension.NAME,
            ScreenshotTestPluginExtension::class.java
        )

        project.androidComponents.onVariants { variant: Variant ->
            CompareScreenshotsTask.register(project, variant, extension)
        }
    }
}
