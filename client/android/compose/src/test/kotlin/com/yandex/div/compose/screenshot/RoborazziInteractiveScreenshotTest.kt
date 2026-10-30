package com.yandex.div.compose.screenshot

import android.graphics.PointF
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.TouchInjectionScope
import androidx.compose.ui.test.click
import androidx.compose.ui.test.doubleClick
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.isFocused
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziTaskType
import com.github.takahirom.roborazzi.captureRoboImage
import com.github.takahirom.roborazzi.provideRoborazziContext
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.extensions.DivExtensionEnvironment
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div.test.INTERACTIVE_SCREENSHOT_CASES_PATH
import com.yandex.div.test.ScreenshotTests
import com.yandex.div.test.composeRoborazziInteractiveScreenshotCases
import com.yandex.div.test.crossplatform.InteractiveScreenshotTestData.Step
import com.yandex.div.test.crossplatform.ParsingResult
import com.yandex.div.test.crossplatform.ParsingUtils
import com.yandex.div.test.crossplatform.clipSwipeToBounds
import org.junit.Rule
import org.junit.experimental.categories.Category
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Verify interactive screenshots:
 * ```
 * ./gradlew :compose:verifyRoborazziDebug -Proborazzi-tests --tests "*.RoborazziInteractiveScreenshotTest"
 * ```
 *
 * Record golden screenshots:
 * ```
 * ./gradlew :compose:verifyAndRecordRoborazziDebug -Proborazzi-tests --tests "*.RoborazziInteractiveScreenshotTest"
 * ```
 *
 * Use `-PdivkitTestFilter=div-action/base.json` to select a single scenario.
 *
 * Goldens are stored in `src/test/screenshots/interactive/`.
 */
@Config(qualifiers = "w360dp-h728dp-xxhdpi")
@Category(ScreenshotTests::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(ParameterizedRobolectricTestRunner::class)
class RoborazziInteractiveScreenshotTest(
    parsingResult: ParsingResult<InteractiveScreenshotTestConfiguration>
) {
    private val configuration = parsingResult.getOrThrow()
    private val painterTracker = ImagePainterTracker()
    private val expectedErrorCount = configuration.runtimeErrorCount
    private val reporter = TestReporter().apply { failOnError = expectedErrorCount == null }

    private val divContext = DivContext(
        baseContext = getApplicationContext(),
        configuration = DivConfiguration(
            extensionHandlers = mapOf("markdown" to SolidBackgroundExtensionHandler()),
            fontSourceProvider = TestFontSourceProvider(),
            imageLoaderConfiguration = LocalImageLoaderConfiguration(getApplicationContext()),
            reporter = reporter,
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

    private lateinit var viewConfiguration: ViewConfiguration

    @Test
    fun test() {
        val data = configuration.baseConfiguration.parseDivData()
        var isViewEmpty by mutableStateOf(false)

        composeRule.setContent {
            CompositionLocalProvider(
                LocalContext provides divContext,
                LocalLayoutDirection provides configuration.baseConfiguration.layoutDirection
            ) {
                viewConfiguration = LocalViewConfiguration.current
                if (isViewEmpty) {
                    Text("<Empty>")
                } else {
                    Box(
                        modifier = Modifier
                            .onSizeChanged { isViewEmpty = it.width == 0 || it.height == 0 },
                    ) {
                        DivView(data = data)
                    }
                }
            }
        }

        val recordedSnapshotNames = mutableSetOf<String>()
        configuration.steps.forEachIndexed { index, step ->
            composeRule.waitForIdle()

            when (step) {
                is Step.Action -> divContext.debugFeatures.performAction(data = data, action = step.action)

                is Step.Tap -> performTap(step.id)

                is Step.DoubleTap -> performOnNode(step.id) { doubleClick() }

                is Step.LongTap -> performOnNode(step.id) { longClick() }

                is Step.Swipe -> performSwipe(step)

                is Step.Wait -> Unit

                is Step.VerifyText -> verifyText(step)

                is Step.VerifyFocus -> verifyFocus(step)

                is Step.VerifySnapshot -> verifySnapshot(step, index, recordedSnapshotNames)
            }

            isViewEmpty = false
        }
        composeRule.waitForIdle()
        expectedErrorCount?.let { verifyExpectedErrors(it) }
    }

    private fun verifyExpectedErrors(count: Int) {
        val errors = reporter.errors
        assertEquals(count, errors.size, "Expected $count error(s), got: $errors")
    }

    private fun performTap(id: String) {
        performOnNode(id) { click() }
        composeRule.mainClock.advanceTimeBy(viewConfiguration.doubleTapTimeoutMillis)
    }

    private fun performOnNode(id: String, gesture: TouchInjectionScope.() -> Unit) =
        composeRule.onNodeWithTag(id).performTouchInput(gesture)

    private fun performSwipe(step: Step.Swipe) {
        val target = composeRule.onNodeWithTag(step.id)
        val node = target.fetchSemanticsNode()
        val size = node.size
        val from = PointF(size.width * step.from.x, size.height * step.from.y)
        val to = PointF(size.width * step.to.x, size.height * step.to.y)
        val visibleBounds = node.boundsInWindow.translate(-node.positionInWindow)
        clipSwipeToBounds(from, to, visibleBounds.toAndroidRectF())
        val offset = node.positionInRoot - node.boundsInRoot.topLeft
        target.performTouchInput {
            swipe(offset + Offset(from.x, from.y), offset + Offset(to.x, to.y))
        }
    }

    private fun verifyText(step: Step.VerifyText) = verifyStep(step.id, hasTextExactly(step.text))

    private fun verifyFocus(step: Step.VerifyFocus) {
        val focusMatcher = isFocused() or hasAnyDescendant(isFocused())
        val focused = if (step.focused) focusMatcher else !focusMatcher
        verifyStep(step.id, focused)
    }

    private fun verifyStep(id: String, check: SemanticsMatcher) =
        composeRule.waitUntilVisible(id, check)

    @OptIn(ExperimentalRoborazziApi::class)
    private fun verifySnapshot(step: Step.VerifySnapshot, index: Int, recordedSnapshotNames: MutableSet<String>) {
        val options = provideRoborazziContext().options
        val taskType = RoborazziTaskType.of(
            isRecording = options.taskType.isRecording() && recordedSnapshotNames.add(step.name),
            isComparing = options.taskType.isComparing(),
            isVerifying = options.taskType.isVerifying(),
        )
        val compareOptions = options.compareOptions.copy(
            outputDirectoryPath = "${options.compareOptions.outputDirectoryPath}/interactive/" +
                "${configuration.baseConfiguration.name}/step$index",
        )
        val target = step.id?.let {
            composeRule.onNodeWithTag(it, useUnmergedTree = true)
                .assertExists("Failed to find screenshot target with div_id '$it'")
        } ?: composeRule.onRoot()
        target.captureRoboImage(
            filePath = getScreenshotFilePath(
                "interactive/${configuration.baseConfiguration.name}/${step.name}"
            ),
            roborazziOptions = options.copy(taskType = taskType, compareOptions = compareOptions),
        )
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
            ParsingUtils.parseFiles(INTERACTIVE_SCREENSHOT_CASES_PATH) { file, json ->
                val fileName = file.getRelativeFileName(INTERACTIVE_SCREENSHOT_CASES_PATH)
                if (fileName in composeRoborazziInteractiveScreenshotCases) {
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
