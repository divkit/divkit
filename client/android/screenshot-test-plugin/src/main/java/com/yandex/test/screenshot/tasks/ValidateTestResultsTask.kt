package com.yandex.test.screenshot.tasks

import com.android.builder.core.BuilderConstants
import com.google.protobuf.TextFormat
import com.google.testing.platform.proto.api.core.TestResultProto.TestResult
import com.google.testing.platform.proto.api.core.TestStatusProto.TestStatus
import com.google.testing.platform.proto.api.core.TestSuiteResultProto.TestSuiteResult
import com.yandex.test.util.FileOutput
import com.yandex.test.util.Logger
import com.yandex.test.util.StreamOutput
import com.yandex.test.util.android
import com.yandex.test.util.filterKeysIn
import com.yandex.test.util.filterKeysNotIn
import com.yandex.test.util.reportDir
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault
abstract class ValidateTestResultsTask : DefaultTask() {

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val testResultsDir: ConfigurableFileCollection

    @get:Internal
    abstract val reportDir: DirectoryProperty

    private val logger: Logger by lazy {
        val logFile = reportDir.file(LOG_FILENAME).get().asFile.apply { delete() }
        Logger(TAG, StreamOutput(), FileOutput(logFile))
    }

    init {
        group = "verification"
    }

    @TaskAction
    fun perform() {
        val testSuiteResults = parseTestSuiteResults()

        validateTestResults(testSuiteResults.flatMap { it.testResultList })
        validateTestSuitResults(testSuiteResults)

        logger.i("Test validation passed.")
    }

    private fun parseTestSuiteResults(): List<TestSuiteResult> {
        return testResultsDir.asFileTree
            .asSequence()
            .filter { it.name == TEST_SUITE_RESULT_FILENAME }
            .map {
                TestSuiteResult.newBuilder()
                    .apply { TextFormat.merge(it.readText(), this) }
                    .build()
            }
            .toList()
    }

    private fun validateTestResults(testResults: List<TestResult>) {
        if (testResults.isEmpty()) {
            throw GradleException("No tests were run.")
        }

        val grouped = testResults.groupBy { it.testStatus }

        val passed = grouped.filterKeysIn(TestStatus.PASSED).flatten()
        val ignored = grouped.filterKeysIn(TestStatus.IGNORED, TestStatus.SKIPPED).flatten()
        val other =
            grouped.filterKeysNotIn(TestStatus.PASSED, TestStatus.IGNORED, TestStatus.SKIPPED)
                .flatten()

        logTestResults(passed = passed, ignored = ignored, failed = other)

        if (other.isNotEmpty()) {
            throw GradleException(
                "There were failing screenshot tests."
            )
        }
    }

    private fun logTestResults(
        passed: List<TestResult>,
        ignored: List<TestResult>,
        failed: List<TestResult>
    ) {
        passed.forEach { logger.i(it.toReportString()) }
        ignored.forEach { logger.w(it.toReportString()) }
        failed.forEach { logger.e(it.toReportString()) }
    }

    private fun validateTestSuitResults(testSuiteResults: List<TestSuiteResult>) {
        val issueMessages = testSuiteResults.flatMap { it.issueList }.map { it.message }

        if (issueMessages.isNotEmpty()) {
            issueMessages.forEach { logger.e(it) }

            throw GradleException("There were issues:\n${issueMessages.joinToString(separator = "\n")}")
        }

        if (testSuiteResults.any {
                it.testStatus !in listOf(
                    TestStatus.PASSED,
                    TestStatus.IGNORED,
                    TestStatus.SKIPPED
                )
            }
        ) {
            throw GradleException("Something is wrong with test suits, check $TEST_SUITE_RESULT_FILENAME and logs for more info.")
        }
    }

    companion object {
        private const val TAG = "ValidateTestResultsTask"

        private const val TEST_SUITE_RESULT_FILENAME = "test-result.textproto"
        private const val LOG_FILENAME = "test-results-validation.log"

        private fun TestResult.toReportString(): String {
            val name = testCase.run { "$testClass $testMethod" }
            return "$name - $testStatus"
        }

        fun register(project: Project): TaskProvider<ValidateTestResultsTask> = project.tasks.register(
            "validateTestResults",
            ValidateTestResultsTask::class.java
        ) {
            it.testResultsDir.from(project.androidTestResultsDir)
            it.reportDir.set(project.reportDir)
        }

        private val Project.androidTestResultsDir: Directory
            get() {
                layout.run {
                    val customDir = android.testOptions.resultsDir?.let {
                        projectDirectory.dir(it)
                    }

                    val defaultDir = buildDirectory.get()
                        .dir("outputs")
                        .dir(BuilderConstants.FD_ANDROID_RESULTS)

                    return customDir ?: defaultDir
                }
            }
    }
}
