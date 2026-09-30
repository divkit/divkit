package com.yandex.test.screenshot.tasks

import io.qameta.allure.kotlin.AllureConstants.TEST_RESULT_FILE_SUFFIX
import io.qameta.allure.kotlin.FileSystemResultsWriter
import io.qameta.allure.kotlin.model.Attachment
import io.qameta.allure.kotlin.model.ExecutableItem
import io.qameta.allure.kotlin.model.Parameter
import io.qameta.allure.kotlin.model.Status
import io.qameta.allure.kotlin.model.StatusDetails
import io.qameta.allure.kotlin.model.TestResult
import io.qameta.allure.kotlin.util.ResultsUtils.PARENT_SUITE_LABEL_NAME
import io.qameta.allure.kotlin.util.ResultsUtils.SUB_SUITE_LABEL_NAME
import io.qameta.allure.kotlin.util.ResultsUtils.SUITE_LABEL_NAME
import io.qameta.allure.kotlin.util.ResultsUtils.TEST_CLASS_LABEL_NAME
import io.qameta.allure.kotlin.util.ResultsUtils.createParentSuiteLabel
import io.qameta.allure.kotlin.util.ResultsUtils.createSubSuiteLabel
import io.qameta.allure.kotlin.util.ResultsUtils.createSuiteLabel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.io.File
import java.util.Base64
import java.util.UUID

internal class ScreenshotAllureResults(
    screenshotDir: File,
    outputDir: File,
    private val apiLevel: String?,
) {

    private val results = mutableListOf<TestResult>()
    private val screenshotResults = mutableMapOf<String, List<ExecutableItem>>()
    private val writer = FileSystemResultsWriter { outputDir }

    val failedTests: List<TestResult>
        get() = results.filter { it.status == Status.FAILED || it.status == Status.BROKEN }

    init {
        File(screenshotDir, "allure-results").walkTopDown().filter { it.isFile }.forEach {
            handleResult(it, outputDir)
        }
    }

    private fun handleResult(file: File, outputDir: File) {
        if (!file.name.endsWith(TEST_RESULT_FILE_SUFFIX)) {
            file.copyTo(File(outputDir, file.name), overwrite = true)
            return
        }

        val result = Json.decodeFromString(TestResult.serializer(), file.readText())
        val method = requireNotNull(result.name).substringBefore('[').removePrefix("test")
        val casePath = result.parameters.firstOrNull { it.name == "Case" }?.value
        val testClass = result.labels.lastOrNull { it.name == TEST_CLASS_LABEL_NAME }?.value
        val source = SHARD_DIRECTORY.matchEntire(file.parentFile.name)?.destructured
            ?.let { (index, count) -> "Shard $index of $count" } ?: "Test run"
        formatResult(result, casePath, requireNotNull(testClass), source, method)
        results.add(result)
        registerScreenshots(listOf(result))
    }

    fun addMissingReference(imagePath: String, actual: File) {
        addComparison(imagePath, Status.FAILED, "No reference screenshot", mapOf("actual" to actual))
    }

    fun addMissingScreenshot(imagePath: String, required: Boolean) {
        addComparison(imagePath, if (required) Status.FAILED else Status.SKIPPED, "Screenshot was not produced")
    }

    fun addMatch(imagePath: String) {
        addComparison(imagePath, Status.PASSED)
    }

    fun addDifference(imagePath: String, actual: File, expected: File, diff: File) {
        addComparison(imagePath, Status.FAILED, attachments = mapOf(
            "actual" to actual,
            "expected" to expected,
            "diff" to diff,
        ))
    }

    private fun addComparison(
        imagePath: String,
        status: Status,
        message: String? = null,
        attachments: Map<String, File> = emptyMap(),
    ) {
        val stages = screenshotResults[imagePath]
            ?: listOf(comparisonResult(imagePath).also(results::add))
        stages.forEach { stage ->
            if (stage.status == Status.FAILED || stage.status == Status.BROKEN) return@forEach
            stage.status = status
            message?.let { stage.statusDetails = StatusDetails(message = it) }
        }
        val images = attachments.filterValues { it.isFile }.takeIf { it.isNotEmpty() } ?: return
        val content = buildJsonObject {
            images.forEach { (name, file) ->
                val bytes = file.readBytes()
                val format = if (bytes.size >= 12 && String(bytes, 8, 4, Charsets.US_ASCII) == "WEBP") "webp" else "png"
                put(name, "data:image/$format;base64,${Base64.getEncoder().encodeToString(bytes)}")
            }
        }
        val filename = "${UUID.randomUUID()}-comparison.json"
        writer.write(filename, content.toString().byteInputStream())
        stages.last().attachments.add(Attachment(
            name = "Screenshot comparison",
            source = filename,
            type = "application/vnd.allure.image.diff",
        ))
    }

    fun write() {
        results.forEach { writer.write(it) }
    }

    private fun registerScreenshots(stages: List<ExecutableItem>) {
        val stage = stages.last()
        stage.parameters.forEach { parameter ->
            if (parameter.name == "Screenshot") {
                screenshotResults[requireNotNull(parameter.value)] = stages
            }
        }
        stage.steps.forEach { registerScreenshots(stages + it) }
    }

    private fun formatResult(
        result: TestResult,
        path: String?,
        testClass: String,
        source: String,
        method: String = ""
    ) {
        path?.let { result.name = it.substringAfterLast('/') }
        val labels = result.labels
        labels.removeAll { it.name in listOf(PARENT_SUITE_LABEL_NAME, SUITE_LABEL_NAME, SUB_SUITE_LABEL_NAME) }
        labels.add(createParentSuiteLabel(testClass.substringAfterLast('.')))
        val directories = path?.substringBeforeLast('/', "").orEmpty()
        val suite = method.ifEmpty { directories.substringBefore('/') }
        if (suite.isNotEmpty()) {
            labels.add(createSuiteLabel(suite))
        }
        if (method.isEmpty() && '/' in directories) {
            labels.add(createSubSuiteLabel(directories.substringAfter('/')))
        }
        val resultApi = labels.firstOrNull { it.name == "androidApi" }?.value ?: apiLevel
        result.parameters.add(Parameter(name = "Source", value = resultApi?.let { "[api$it] " }.orEmpty() + source))
    }

    private fun comparisonResult(imagePath: String): TestResult {
        val classPath = imagePath.substringAfter('/')
        val testClass = classPath.substringBefore('/')
        val path = classPath.substringAfter('/')
        val result = TestResult(uuid = UUID.randomUUID().toString())
        result.parameters.add(Parameter(name = "Screenshot", value = imagePath))
        formatResult(result, path, testClass, "Collect and compare screenshots")
        return result
    }

    private companion object {
        val SHARD_DIRECTORY = Regex("shard-(\\d+)-of-(\\d+)")
    }
}
