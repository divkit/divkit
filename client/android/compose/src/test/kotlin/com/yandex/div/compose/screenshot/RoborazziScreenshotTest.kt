package com.yandex.div.compose.screenshot

import com.yandex.div.test.SNAPSHOT_TEST_CASES_PATH
import com.yandex.div.test.ScreenshotTests
import com.yandex.div.test.composeRoborazziIgnoredScreenshotCases
import com.yandex.div.test.crossplatform.ParsingResult
import com.yandex.div.test.crossplatform.ParsingUtils
import org.junit.Rule
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.test.Test

/**
 * JVM-based snapshot test for the DivKit Compose renderer using Roborazzi.
 *
 * Verify against goldens:
 * ```
 * ./gradlew :compose:verifyRoborazziDebug -Proborazzi-tests --tests "*.RoborazziScreenshotTest"
 * ```
 *
 * Record golden screenshots:
 * ```
 * ./gradlew :compose:verifyAndRecordRoborazziDebug -Proborazzi-tests --tests "*.RoborazziScreenshotTest"
 * ```
 *
 * Use `-PdivkitTestFilter=div-text/font_weight.json` to select a single scenario.
 *
 * Goldens are stored in `src/test/screenshots/` and committed to the repository.
 */
@Config(qualifiers = "w360dp-h728dp-xxhdpi")
@Category(ScreenshotTests::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(ParameterizedRobolectricTestRunner::class)
class RoborazziScreenshotTest(
    parsingResult: ParsingResult<ScreenshotTestConfiguration>
) {
    private val configuration = parsingResult.getOrThrow()

    @get:Rule
    val contentRule = ComposeContentRoborazziRule()

    @Test
    fun test() {
        contentRule.run {
            setContent(configuration)
            waitForIdle()
            captureScreenshot(configuration.name)
        }
    }

    companion object {

        // Cache cases because ParameterizedRobolectricTestRunner calls the provider repeatedly.
        private val cases =
            ParsingUtils.parseFiles(SNAPSHOT_TEST_CASES_PATH) { file, json ->
                val fileName = file.getRelativeFileName(SNAPSHOT_TEST_CASES_PATH)
                if (fileName in composeRoborazziIgnoredScreenshotCases) {
                    return@parseFiles emptyList()
                }
                val configuration = ScreenshotTestConfiguration(
                    name = fileName.removeSuffix(".json"),
                    json = json
                )
                listOf(ParsingResult.Success(configuration))
            }

        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        @Suppress("unused")
        fun cases() = cases
    }
}
