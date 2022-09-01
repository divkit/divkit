package com.yandex.test.util

import org.gradle.api.Project

internal class AndroidDevice(
    private val project: Project,
    private val exec: CmdExecutor = CmdExecutor(project.rootDir)
) {

    init {
        // warm up the adb server
        try {
            adb("devices")
        } catch (e: RuntimeException) {
            project.logger.info("AndroidDevice: adb warm-up failed due to '$e'")
        }
    }

    val externalStorage: String
        get () = exec.runCommand("$adb shell echo \$EXTERNAL_STORAGE")

    private val android: AndroidExtension
        get() = project.android

    private val adb: String
        get() = android.adbExecutable.absolutePath

    fun adb(args: String) = exec.runCommand("$adb $args")

    fun pull(devicePath: String, hostPath: String) {
        exec.runCommand(command = "$adb pull -p $devicePath $hostPath", timeoutSec = 300L)
    }

    fun remove(devicePath: String) = adb("shell rm -rf $devicePath")
}
