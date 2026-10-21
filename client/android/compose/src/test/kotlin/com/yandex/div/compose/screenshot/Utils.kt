package com.yandex.div.compose.screenshot

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.LosslessWebPImageIoFormat
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziRule
import org.junit.rules.TestRule
import java.io.File

internal fun ComposeTestRule.waitUntilVisible(
    id: String,
    matcher: SemanticsMatcher,
    timeoutMillis: Long = 3_000
): SemanticsNodeInteraction {
    val condition = hasTestTag(id) and matcher
    val node = onNode(condition)
    waitUntil("Node matching (${condition.description}) is visible", timeoutMillis) {
        node.isDisplayed()
    }
    return node
}

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
