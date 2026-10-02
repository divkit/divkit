package com.yandex.div.gradle

import com.android.build.api.dsl.ApplicationExtension
import com.android.builder.core.BuilderConstants
import com.google.protobuf.TextFormat
import com.google.testing.platform.proto.api.core.TestResultProto.TestResult
import com.google.testing.platform.proto.api.core.TestStatusProto.TestStatus
import com.google.testing.platform.proto.api.core.TestSuiteResultProto.TestSuiteResult
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

    private val logFile by lazy {
        reportDir.file(LOG_FILENAME).get().asFile.apply { delete() }
    }

    init {
        group = "verification"
    }

    @TaskAction
    fun perform() {
        val testSuiteResults = parseTestSuiteResults()

        validateTestResults(testSuiteResults.flatMap { it.testResultList })
        validateTestSuitResults(testSuiteResults)

        log("I", "Test validation passed.")
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

        val passed = grouped.filterKeys { it == TestStatus.PASSED }.values.flatten()
        val ignored = grouped.filterKeys { it in listOf(TestStatus.IGNORED, TestStatus.SKIPPED) }.values.flatten()
        val other =
            grouped.filterKeys { it !in listOf(TestStatus.PASSED, TestStatus.IGNORED, TestStatus.SKIPPED) }
                .values.flatten()

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
        passed.forEach { log("I", it.toReportString()) }
        ignored.forEach { log("W", it.toReportString()) }
        failed.forEach { log("E", it.toReportString()) }
    }

    private fun validateTestSuitResults(testSuiteResults: List<TestSuiteResult>) {
        val issueMessages = testSuiteResults.flatMap { it.issueList }.map { it.message }

        if (issueMessages.isNotEmpty()) {
            issueMessages.forEach { log("E", it) }

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

    private fun log(level: String, message: String) {
        val line = "$level/$TAG: $message"
        println(line)
        logFile.appendText("$line\n")
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
            testResultsDir.from(project.androidTestResultsDir)
            reportDir.set(project.layout.buildDirectory.dir("reports"))
        }

        private val Project.androidTestResultsDir: Directory
            get() {
                layout.run {
                    val android = extensions.getByType(ApplicationExtension::class.java)
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
