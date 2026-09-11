package com.yandex.div.compose.screenshot

import androidx.compose.foundation.background
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.github.takahirom.roborazzi.captureRoboImage
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.extensions.DivExtensionEnvironment
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div.compose.screenshot.InteractiveScreenshotTestConfiguration.Step
import com.yandex.div.test.crossplatform.ParsingResult
import com.yandex.div.test.crossplatform.ParsingUtils
import org.junit.Rule
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.test.Test

/**
 * Verify interactive screenshots:
 * ```
 * ./gradlew :compose:verifyRoborazziDebug --tests "*.RoborazziInteractiveScreenshotTest"
 * ```
 *
 * Record golden screenshots:
 * ```
 * ./gradlew :compose:verifyAndRecordRoborazziDebug --tests "*.RoborazziInteractiveScreenshotTest"
 * ```
 *
 * Use `-PdivkitTestFilter=div-action/base.json` to select a single scenario.
 *
 * Goldens are stored in `src/test/screenshots/interactive/`.
 */
@Config(qualifiers = "w360dp-h728dp-xxhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(ParameterizedRobolectricTestRunner::class)
class RoborazziInteractiveScreenshotTest(
    parsingResult: ParsingResult<InteractiveScreenshotTestConfiguration>
) {
    private val configuration = parsingResult.getOrThrow()
    private val painterTracker = ImagePainterTracker()

    private val divContext = DivContext(
        baseContext = getApplicationContext(),
        configuration = DivConfiguration(
            extensionHandlers = mapOf("markdown" to SolidBackgroundExtensionHandler()),
            fontSourceProvider = TestFontSourceProvider(),
            imageLoaderConfiguration = LocalImageLoaderConfiguration(),
            reporter = TestReporter(),
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
        val data = configuration.baseConfiguration.parseDivData()
        var isViewEmpty by mutableStateOf(false)

        composeRule.setContent {
            CompositionLocalProvider(
                LocalContext provides divContext,
                LocalLayoutDirection provides configuration.baseConfiguration.layoutDirection
            ) {
                if (isViewEmpty) {
                    Text("<Empty>")
                } else {
                    DivView(
                        modifier = Modifier
                            .onSizeChanged { isViewEmpty = it.width == 0 || it.height == 0 },
                        data = data,
                    )
                }
            }
        }

        configuration.steps.forEach { step ->
            composeRule.waitForIdle()

            when (step) {
                is Step.Action ->
                    divContext.debugFeatures.performAction(data = data, action = step.action)

                is Step.VerifySnapshot ->
                    composeRule
                        .onRoot()
                        .captureRoboImage(
                            filePath = getScreenshotFilePath("interactive/${configuration.baseConfiguration.name}/${step.name}")
                        )

                is Step.Wait -> Unit
            }

            isViewEmpty = false
        }
    }

    private class SolidBackgroundExtensionHandler : DivExtensionHandler {
        @Composable
        override fun Content(
            modifier: Modifier,
            environment: DivExtensionEnvironment,
            content: @Composable (Modifier) -> Unit
        ) {
            content(modifier.background(Color.Red))
        }
    }

    companion object {

        // Cache cases because ParameterizedRobolectricTestRunner calls the provider repeatedly.
        private val cases =
            ParsingUtils.parseFiles("interactive_snapshot_test_data") { file, json ->
                val fileName = file.getRelativeFileName("interactive_snapshot_test_data")
                if (fileName in selectedFiles) {
                    val configuration = InteractiveScreenshotTestConfiguration(
                        name = fileName.removeSuffix(".json"),
                        json = json,
                    )
                    listOf(ParsingResult.Success(configuration))
                } else {
                    emptyList()
                }
            }

        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        @Suppress("unused")
        fun cases() = cases
    }
}

private val selectedFiles = setOf(
    "div-action/base.json",
    "div-action/set-variable.json",
    "div-container/base-properties.json",
    "div-container/visibility.json",
    "div-extension/is-enabled.json",
    "div-grid/grid_layout.json",
    "div-input/currency_input_mask.json",
    "div-input/fixed_length_input_mask.json",
    "div-input/phone_input_mask.json",
    "div-switch/base-properties.json",
    "div-switch/switch-properties.json",
    "div-text/text-properties.json",
)
