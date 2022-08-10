package com.yandex.test.screenshot.tasks

import java.io.File


class TestCaseStatuses(testCaseReferencesDir: File) {
    private val caseResolver = TestCaseResolver(testCaseReferencesDir)
    private val testCases = hashMapOf<String, Boolean>()

    fun notifyPassed(actualScreenshotPath: String) {
        caseResolver.getTaskName(actualScreenshotPath)?.let {
            testCases.getOrPut(it) { true }
        }
    }

    fun notifyFailed(actualScreenshotPath: String) {
        caseResolver.getTaskName(actualScreenshotPath)?.let {
            testCases[it] = false
        }
    }

    fun getPassedCases(): Collection<String> {
        return testCases.filterValues { it }.keys
    }
}
