package com.yandex.test.screenshot

import com.android.build.api.variant.Variant
import com.yandex.test.screenshot.tasks.CompareScreenshotsTask
import com.yandex.test.screenshot.tasks.ValidateTestResultsTask
import com.yandex.test.util.android
import com.yandex.test.util.androidComponents
import org.gradle.api.Plugin
import org.gradle.api.Project
import kotlin.io.path.Path

class ScreenshotTestPlugin : Plugin<Project> {

    @Suppress("UnstableApiUsage")
    override fun apply(project: Project) {
        val extension = project.extensions.create(
            ScreenshotTestPluginExtension.NAME,
            ScreenshotTestPluginExtension::class.java
        )

        val validateTestResultsTask = ValidateTestResultsTask.register(project)

        project.androidComponents.onVariants { variant: Variant ->
            val compareTask = CompareScreenshotsTask.register(project, variant, extension)

            project.androidComponents.finalizeDsl {
                project.tasks.matching {
                    it.name == variant.computeTaskName("connected", "androidTest")
                }.configureEach { task ->
                    val additionalOutputs = task.outputs.files.single {
                        val path = it.toPath()
                        path.contains(Path("connected_android_test_additional_output"))
                    }
                    compareTask.configure {
                        it.screenshotDir.set(additionalOutputs)
                    }
                    task.finalizedBy(
                        compareTask,
                        validateTestResultsTask,
                    )
                }
            }
        }

        project.androidComponents.finalizeDsl {
            val screenshotTestAnnotations = extension.testAnnotations.get()
            if (screenshotTestAnnotations.isNotEmpty()) {
                if (extension.enabled.get()) {
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
