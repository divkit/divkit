package com.yandex.test.screenshot

import com.android.build.api.variant.Variant
import com.yandex.test.screenshot.tasks.CompareScreenshotsTask
import com.yandex.test.util.androidComponents
import io.qameta.allure.gradle.report.AllureReportPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class ScreenshotTestPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create(
            ScreenshotTestPluginExtension.NAME,
            ScreenshotTestPluginExtension::class.java
        )

        project.androidComponents.onVariants { variant: Variant ->
            val comparisonTask = CompareScreenshotsTask.register(project, variant, extension)
            if (project.gradle.startParameter.taskNames.any { it.substringAfterLast(':') == comparisonTask.name }) {
                project.pluginManager.withPlugin(AllureReportPlugin.PLUGIN_NAME) {
                    project.tasks.named(AllureReportPlugin.REPORT_TASK_NAME) { reportTask ->
                        reportTask.inputs.files(comparisonTask.flatMap { it.allureResultsDir })
                    }
                }
            }
        }
    }
}
