package com.yandex.test.screenshot.tasks

import com.yandex.test.screenshot.ScreenshotTestPluginExtension
import com.yandex.test.util.AndroidDevice
import com.yandex.test.util.reportDir
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class PullScreenshotsTask : DefaultTask() {

    private val screenshots = project.extensions.getByType(ScreenshotTestPluginExtension::class.java)

    init {
        group = "verification"
    }

    @TaskAction
    fun perform() {
        val device by lazy { AndroidDevice(project) }

        val devicePath = "${device.externalStorage}/${screenshots.deviceDir}"
        val hostPath = "${project.reportDir}/${screenshots.hostDir}"
        val collectedPath = "${project.reportDir}/${screenshots.collectedDir}"

        File(hostPath).apply {
            deleteRecursively()
            mkdirs()
        }

        device.pull(devicePath, collectedPath)
        device.remove(devicePath)
    }

    companion object {
        const val NAME = "pullScreenshots"
    }
}
