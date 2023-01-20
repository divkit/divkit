package com.yandex.test.screenshot

enum class ScreenshotType(private val path: String) {
    ViewRender("viewRender"),
    ViewPixelCopy("viewPixelCopy"),
    Device("device");

    fun relativeDirPath(suiteName: String): String {
        val suitePath = suiteName.trim('/')
        return "$this/$suitePath"
    }

    fun relativeScreenshotPath(suiteName: String, name: String): String {
        return "${relativeDirPath(suiteName)}/$name$SCREENSHOT_EXTENSION"
    }

    override fun toString(): String {
        return path
    }

    companion object {
        private const val SCREENSHOT_EXTENSION = ".png"
    }
}

