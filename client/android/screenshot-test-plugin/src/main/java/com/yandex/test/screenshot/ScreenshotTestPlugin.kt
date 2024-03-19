package com.yandex.test.screenshot

import com.yandex.test.screenshot.tasks.CompareScreenshotsTask
import com.yandex.test.screenshot.tasks.GenerateScreenshotConfigTask
import com.yandex.test.screenshot.tasks.PullScreenshotsTask
import com.yandex.test.screenshot.tasks.ValidateTestResultsTask
import com.yandex.test.util.android
import com.yandex.test.util.androidComponents
import org.gradle.api.Plugin
import org.gradle.api.Project

class ScreenshotTestPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create(
            ScreenshotTestPluginExtension.NAME,
            ScreenshotTestPluginExtension::class.java
        )

        val generateScreenshotConfigTask = project.tasks.register(
            "generateScreenshotConfig",
            GenerateScreenshotConfigTask::class.java,
        ) {
            it.deviceDir.set(extension.deviceDir)
        }
        project.androidComponents.onVariants { variant ->
            if (extension.enabled) {
                val javaSources = checkNotNull(variant.sources.java) {
                    "Not found java sources for variant $variant"
                }
                javaSources.addGeneratedSourceDirectory(
                    generateScreenshotConfigTask,
                    GenerateScreenshotConfigTask::outputDir
                )
            }
        }

        project.afterEvaluate {
            val validateTestResultsTask = ValidateTestResultsTask.register(project)
            val pullScreenshotsTask = PullScreenshotsTask.register(project, extension)
            val compareScreenshotsTask = CompareScreenshotsTask.register(project, extension,) {
                it.mustRunAfter(pullScreenshotsTask)
            }

            if (extension.enabled) {
                project.tasks.matching {
                    it.name.startsWith("connected") && it.name.endsWith("AndroidTest")
                }.configureEach { task ->
                    task.finalizedBy(pullScreenshotsTask)
                    if (extension.enableComparison) {
                        task.finalizedBy(compareScreenshotsTask)
                    }
                    task.finalizedBy(validateTestResultsTask)
                }
            }
        }

        @Suppress("UnstableApiUsage")
        project.androidComponents.finalizeDsl {
            val screenshotTestAnnotations = extension.testAnnotations
            if (screenshotTestAnnotations.isNotEmpty()) {
                if (extension.enabled) {
                    includeByAnnotations(project, screenshotTestAnnotations)
                } else {
                    excludeByAnnotations(project, screenshotTestAnnotations)
                }
            }
        }
    }

    private fun includeByAnnotations(project: Project, screenshotTestAnnotations: List<String>) {
        val runnerArguments = project.android.defaultConfig.testInstrumentationRunnerArguments
        val annotations = runnerArguments[RUNNER_ARGUMENT_INCLUDE]?.split(",") ?: emptyList()
        runnerArguments[RUNNER_ARGUMENT_INCLUDE] =
            (annotations + screenshotTestAnnotations).joinToString(separator = ",")
    }

    private fun excludeByAnnotations(project: Project, screenshotTestAnnotations: List<String>) {
        val runnerArguments = project.android.defaultConfig.testInstrumentationRunnerArguments
        val annotations = runnerArguments[RUNNER_ARGUMENT_EXCLUDE]?.split(",") ?: emptyList()
        runnerArguments[RUNNER_ARGUMENT_EXCLUDE] =
            (annotations + screenshotTestAnnotations).joinToString(separator = ",")
    }

    companion object {
        private const val RUNNER_ARGUMENT_INCLUDE = "annotation"
        private const val RUNNER_ARGUMENT_EXCLUDE = "notAnnotation"
    }
}
