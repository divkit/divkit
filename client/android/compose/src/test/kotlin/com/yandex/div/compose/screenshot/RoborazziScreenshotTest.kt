package com.yandex.div.compose.screenshot

import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.github.takahirom.roborazzi.captureRoboImage
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div.test.SNAPSHOT_TEST_CASES_PATH
import com.yandex.div.test.ScreenshotTests
import com.yandex.div.test.composeRoborazziIgnoredScreenshotCases
import com.yandex.div.test.crossplatform.ParsingResult
import com.yandex.div.test.crossplatform.ParsingUtils
import org.junit.Rule
import org.junit.experimental.categories.Category
import org.junit.rules.RuleChain
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
    private val painterTracker = ImagePainterTracker()

    private val divContext = DivContext(
        baseContext = getApplicationContext(),
        configuration = DivConfiguration(
            fontSourceProvider = TestFontSourceProvider(),
            imageLoaderConfiguration = LocalImageLoaderConfiguration(getApplicationContext()),
            reporter = TestReporter()
        ),
        debugConfiguration = DivDebugConfiguration(
            imagePainterStateListener = painterTracker::onStateChanged
        )
    )

    private val composeRule = createComposeRule().apply {
        registerIdlingResource(painterTracker)
    }

    @get:Rule
    val rule: RuleChain = RuleChain
        .outerRule(composeRule)
        .around(createRoborazziRule())

    @Test
    fun test() {
        var isViewEmpty by mutableStateOf(false)

        composeRule.apply {
            setContent {
                CompositionLocalProvider(
                    LocalContext provides divContext,
                    LocalLayoutDirection provides configuration.layoutDirection
                ) {
                    if (isViewEmpty) {
                        Text("<Empty>")
                    } else {
                        DivView(
                            modifier = Modifier.testTag("DivView"),
                            data = configuration.parseDivData()
                        )
                    }
                }
            }

            waitForIdle()

            val viewSize = onNodeWithTag("DivView").fetchSemanticsNode().size
            isViewEmpty = viewSize.width == 0 || viewSize.height == 0

            waitForIdle()

            onRoot().captureRoboImage(filePath = getScreenshotFilePath(configuration.name))
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
