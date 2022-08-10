package com.yandex.test.screenshot

import java.io.File

class SuiteDirs(
    suiteName: String
) {
    private val directories = mutableListOf<File>()
    private val suitePath = suiteName.trimStart('/').trimEnd('/')
    val relativeViewRenderPath: String = "viewRender/$suitePath"
    val relativeViewPixelCopyPath: String = "viewPixelCopy/$suitePath"
    private val relativeDevicePath: String = "device/$suitePath"
    val viewRenderDir = screenshotDir(relativeViewRenderPath)
    val viewPixelCopyDir = screenshotDir(relativeViewPixelCopyPath)
    val deviceScreenshotDir = screenshotDir(relativeDevicePath)

    private var prepared = false

    private fun screenshotDir(relativePath: String? = null): File {
        val path = "${ScreenshotCaptor.rootDir}/$relativePath"
        return File(path).also {
            directories.add(it)
        }
    }

    fun prepareDirectories() {
        if (prepared) {
            return
        }
        directories.forEach {
            it.mkdirs()
        }
        prepared = true
    }

    init {
        require(suiteName.isNotEmpty()) { "Suite name can not be empty" }
    }
}
