package com.yandex.test.screenshot

import java.util.concurrent.ConcurrentHashMap

object ScreenshotTestState {
    private val stateMap = ConcurrentHashMap<String, Boolean?>()

    fun isCompleted(caseName: String): Boolean {
        return stateMap[caseName] == true
    }

    fun notifyTestStarted(caseName: String) {
        if (stateMap[caseName] == false) {
            throw IllegalStateException("Previous test not completed!")
        }

        stateMap[caseName] = false
    }

    fun notifyTestCompleted(caseName: String) {
        val completed = stateMap[caseName] ?: throw IllegalStateException("Test '$caseName' not started!")

        if (completed) {
            throw IllegalStateException("Test '$caseName' already completed!")
        }

        stateMap[caseName] = true
    }
}
