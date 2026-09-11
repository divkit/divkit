package com.yandex.div.compose.screenshot

import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.LosslessWebPImageIoFormat
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziRule
import org.junit.rules.TestRule
import java.io.File

fun File.getRelativeFileName(testDataDir: String): String {
    return relativeTo(File("../../../test_data/$testDataDir")).invariantSeparatorsPath
}

fun getScreenshotFilePath(relativeFileNameWithoutExtension: String): String {
    val file = File("src/test/screenshots/$relativeFileNameWithoutExtension.webp")
    file.parentFile?.mkdirs()
    return file.path
}

@OptIn(ExperimentalRoborazziApi::class)
fun createRoborazziRule(): TestRule = RoborazziRule(
    options = RoborazziRule.Options(
        roborazziOptions = RoborazziOptions(
            compareOptions = RoborazziOptions.CompareOptions(
                changeThreshold = 0.005f
            ),
            recordOptions = RoborazziOptions.RecordOptions(
                imageIoFormat = LosslessWebPImageIoFormat()
            )
        )
    )
)
