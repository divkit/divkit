package com.yandex.test.screenshot.tasks

import com.yandex.test.screenshot.ScreenshotTestPluginExtension
import com.yandex.test.util.AndroidDevice
import com.yandex.test.util.android
import com.yandex.test.util.reportDir
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider

abstract class PullScreenshotsTask : DefaultTask() {

    @get:Internal
    abstract val adbExecutable: RegularFileProperty

    @get:Input
    abstract val deviceDir: Property<String>

    @get:OutputDirectory
    abstract val collectedDir: DirectoryProperty

    init {
        group = "verification"
    }

    @TaskAction
    fun perform() {
        collectedDir.asFile.get().deleteRecursively()

        val device = AndroidDevice(adbExecutable.get().asFile, logger)
        val devicePath = "${device.externalStorage}/${deviceDir.get()}"

        device.pull(devicePath, collectedDir.get().asFile.absolutePath)
        device.remove(devicePath)
    }

    companion object {
        fun register(
            project: Project,
            extension: ScreenshotTestPluginExtension,
        ): TaskProvider<PullScreenshotsTask> = project.tasks.register("pullScreenshots", PullScreenshotsTask::class.java) {
            it.outputs.upToDateWhen { false }
            it.adbExecutable.set(project.android.adbExecutable)
            it.deviceDir.set(extension.deviceDir)
            it.collectedDir.set(project.reportDir.map { it.dir(extension.collectedDir) })
        }
    }
}
