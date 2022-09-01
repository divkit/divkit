package com.yandex.test.runner

import android.os.Bundle
import androidx.test.runner.AndroidJUnitRunner

@Suppress("unused")
class AllureAwareAndroidRunner : AndroidJUnitRunner() {
    override fun onCreate(arguments: Bundle) {
        val runnerListeners = arguments.getString(RUNNER_ARGUMENT_LISTENER, "").split(",").toMutableList()
        runnerListeners += allureAndroidListener
        arguments.putString(RUNNER_ARGUMENT_LISTENER, runnerListeners.joinToString(separator = ","))

        super.onCreate(arguments)
    }

    private companion object {
        private const val RUNNER_ARGUMENT_LISTENER = "listener"
        private val allureAndroidListener = AllureAwareRunListener::class.java.name
    }
}
