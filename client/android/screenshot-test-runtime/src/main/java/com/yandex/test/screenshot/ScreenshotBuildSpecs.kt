package com.yandex.test.screenshot

internal object ScreenshotBuildSpecs {

    private const val DEVICE_SCREENSHOT_DIR = "DEVICE_SCREENSHOT_DIR"

    val screenshotRootDir: String
        get() = config[DEVICE_SCREENSHOT_DIR] ?: throw NoSuchElementException()

    private val config by lazy { resolveConfig() }

    @Suppress("UNCHECKED_CAST")
    private fun resolveConfig(): Map<String, String> {
        return try {
            val configClass = Class.forName("com.yandex.test.screenshot.ScreenshotConfig")
            configClass.newInstance() as Map<String, String>
        } catch (e: ReflectiveOperationException) {
            throw IllegalStateException("Make sure 'com.yandex.test.screenshot-tests' plugin is applied", e)
        }
    }
}
