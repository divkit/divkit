package com.yandex.test.screenshot

import com.yandex.test.screenshot.tasks.CompareScreenshotsTask
import com.yandex.test.screenshot.tasks.ValidateTestResultsTask
import com.yandex.test.util.android
import com.yandex.test.util.androidComponents
import org.gradle.api.Plugin
import org.gradle.api.Project
import kotlin.io.path.Path

class ScreenshotTestPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create(
            ScreenshotTestPluginExtension.NAME,
            ScreenshotTestPluginExtension::class.java
        )

        project.androidComponents.onVariants { variant ->
            val variantName = variant.name.replaceFirstChar { it.uppercase() }
            CompareScreenshotsTask.register(project, extension, variantName)
        }

        project.afterEvaluate {
            val validateTestResultsTask = ValidateTestResultsTask.register(project)
            if (extension.enabled) {
                project.tasks.matching {
                    it.name.startsWith("connected") && it.name.endsWith("AndroidTest")
                }.configureEach { task ->
                    val additionalOutputs = task.outputs.files.single {
                        val path = it.toPath()
                        path.contains(Path("connected_android_test_additional_output"))
                    }

                    val variant = task.name.removePrefix("connected")
                        .removeSuffix("AndroidTest")

                    val compareScreenshotsTask =
                        project.tasks.withType(CompareScreenshotsTask::class.java)
                            .named("compare${variant}Screenshots")
                            .get()

                    compareScreenshotsTask.screenshotDir.set(additionalOutputs)

                    if (extension.enableComparison) {
                        task.finalizedBy(compareScreenshotsTask)
                    }
                    task.finalizedBy(validateTestResultsTask)
                }
            }
        }

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
