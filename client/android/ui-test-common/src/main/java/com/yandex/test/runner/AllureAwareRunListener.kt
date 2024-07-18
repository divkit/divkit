package com.yandex.test.runner

import androidx.test.internal.runner.listener.InstrumentationRunListener
import androidx.test.uiautomator.UiDevice
import com.yandex.test.util.formattedName
import org.junit.runner.Description
import org.junit.runner.Result
import org.junit.runner.notification.Failure
import ru.tinkoff.allure.AllureRunListener
import ru.tinkoff.allure.android.AllureAndroidLifecycle
import ru.tinkoff.allure.model.Status
import ru.tinkoff.allure.model.StatusDetails

/**
 * Based on https://github.com/allure-framework/allure-android/blob/v2.5.1/allure-android/src/main/kotlin/ru/tinkoff/allure/android/AllureAndroidListener.kt
 */
class AllureAwareRunListener : InstrumentationRunListener() {
    private val lifecycle = AllureAndroidLifecycle
    private val allureListenerDelegate = AllureRunListener(lifecycle)

    override fun testStarted(description: Description) {
        allureListenerDelegate.testStarted(description)
    }

    override fun testFinished(description: Description) {
        allureListenerDelegate.testFinished(description)
    }

    override fun testFailure(failure: Failure) {
        if (failure.description.isTest) {
            val uuid = failure.description.formattedName()
            testFailed(uuid, failure)
        } else {
            suiteFailed(failure)
        }
    }

    override fun testRunStarted(description: Description?) {
        grantPermissions()
        allureListenerDelegate.testRunStarted()
    }

    override fun testRunFinished(result: Result?) {
        allureListenerDelegate.testRunFinished()
    }

    private fun testFailed(uuid: String, failure: Failure) {
        with(lifecycle) {
            updateTestCase(uuid) {
                status = Status.fromThrowable(failure.exception)
                statusDetails = StatusDetails.fromThrowable(failure.exception)
            }
            writeTestCase(uuid)
        }
    }

    private fun suiteFailed(failure: Failure) {
        failure.description.children.forEach { childDescription ->
            val uuid = childDescription.formattedName()
            testFailed(uuid, failure)
        }
    }

    private fun grantPermissions() {
        with(UiDevice.getInstance(instrumentation)) {
            executeShellCommand("pm grant ${instrumentation.context.packageName} android.permission.WRITE_EXTERNAL_STORAGE")
            executeShellCommand("pm grant ${instrumentation.targetContext.packageName} android.permission.WRITE_EXTERNAL_STORAGE")
            executeShellCommand("pm grant ${instrumentation.context.packageName} android.permission.READ_EXTERNAL_STORAGE")
            executeShellCommand("pm grant ${instrumentation.targetContext.packageName} android.permission.READ_EXTERNAL_STORAGE")
            executeShellCommand("appops set --uid ${instrumentation.targetContext.packageName} MANAGE_EXTERNAL_STORAGE allow")
        }
    }
}
