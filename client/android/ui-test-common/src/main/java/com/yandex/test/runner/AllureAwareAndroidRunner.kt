package com.yandex.test.runner

import android.os.Bundle
import androidx.test.runner.AndroidJUnitRunner
import ru.tinkoff.allure.android.AllureAndroidListener

@Suppress("unused")
class AllureAwareAndroidRunner : AndroidJUnitRunner() {
    override fun onCreate(arguments: Bundle) {
        val listeners = arguments.getString(RUNNER_ARGUMENT_LISTENER, "").split(",").toMutableList()
        arguments.putString(RUNNER_ARGUMENT_LISTENER, (listeners + allureAndroidListener).joinToString(separator = ","))

        super.onCreate(arguments)
    }

    private companion object {
        private const val RUNNER_ARGUMENT_LISTENER = "listener"
        private val allureAndroidListener = AllureAndroidListener::class.java.name
    }
}
