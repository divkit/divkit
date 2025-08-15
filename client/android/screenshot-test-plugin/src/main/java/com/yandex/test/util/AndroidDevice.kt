package com.yandex.test.util

import org.gradle.api.logging.Logger
import java.io.File

internal class AndroidDevice(
    private val adbExecutable: File,
    logger: Logger,
    private val exec: CmdExecutor = CmdExecutor()
) {

    init {
        // warm up the adb server
        try {
            adb("devices")
        } catch (e: RuntimeException) {
            logger.info("AndroidDevice: adb warm-up failed due to '$e'")
        }
    }

    val externalStorage: String
        get () = exec.runCommand("$adb shell echo \$EXTERNAL_STORAGE")

    private val adb: String
        get() = adbExecutable.absolutePath

    fun adb(args: String) = exec.runCommand("$adb $args")

    fun pull(devicePath: String, hostPath: String) {
        exec.runCommand(command = "$adb pull -p $devicePath $hostPath", timeoutSec = 300L)
    }

    fun remove(devicePath: String) = adb("shell rm -rf $devicePath")
}
