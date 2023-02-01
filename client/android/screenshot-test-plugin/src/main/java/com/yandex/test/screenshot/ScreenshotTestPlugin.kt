package com.yandex.test.screenshot

import com.yandex.test.screenshot.tasks.CompareScreenshotsTask
import com.yandex.test.screenshot.tasks.GenerateScreenshotConfigTask
import com.yandex.test.screenshot.tasks.PullScreenshotsTask
import com.yandex.test.screenshot.tasks.ValidateTestResultsTask
import com.yandex.test.util.android
import com.yandex.test.util.androidComponents
import com.yandex.test.util.variants
import org.gradle.api.Plugin
import org.gradle.api.Project

class ScreenshotTestPlugin : Plugin<Project> {

    private val enabled: Boolean
        get() = screenshotTests.enabled

    private lateinit var screenshotTests: ScreenshotTestPluginExtension

    override fun apply(project: Project) {
        configure(project)
        register(project)
    }

    private fun configure(project: Project) {
        screenshotTests = project.extensions.create(
            ScreenshotTestPluginExtension.NAME,
            ScreenshotTestPluginExtension::class.java
        )
    }

    private fun register(project: Project) {
        project.tasks.register(GenerateScreenshotConfigTask.NAME, GenerateScreenshotConfigTask::class.java) { task ->
            project.android.variants.all { variant ->
                variant.registerJavaGeneratingTask(task, task.outputDir)
            }
        }
        project.tasks.register(ValidateTestResultsTask.NAME, ValidateTestResultsTask::class.java)
        project.tasks.register(PullScreenshotsTask.NAME, PullScreenshotsTask::class.java)
        project.tasks.register(CompareScreenshotsTask.NAME, CompareScreenshotsTask::class.java)

        project.tasks.whenTaskAdded { task ->
            val isDependentTask = task.name.run {
                startsWith("compile") && (endsWith("JavaWithJavac") || endsWith("Kotlin"))
            }
            if (enabled && isDependentTask) task.dependsOn(GenerateScreenshotConfigTask.NAME)
        }

        project.tasks.whenTaskAdded { task ->
            val isDependentTask = task.name.run {
                startsWith("connected") && endsWith("AndroidTest")
            }
            if (enabled && isDependentTask) task.finalizedBy(CompareScreenshotsTask.NAME)
        }

        project.androidComponents.finalizeDsl {
            val screenshotTestAnnotations = screenshotTests.testAnnotations
            if (screenshotTestAnnotations.isNotEmpty()) {
                if (enabled) {
                    includeByAnnotations(project, screenshotTestAnnotations)
                } else {
                    excludeByAnnotations(project, screenshotTestAnnotations)
                }
            }
        }
    }

    @Suppress("UnstableApiUsage")
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
