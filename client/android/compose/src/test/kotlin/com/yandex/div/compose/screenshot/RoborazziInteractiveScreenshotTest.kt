package com.yandex.div.compose.screenshot

import android.graphics.PointF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.doubleClick
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.isFocused
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziTaskType
import com.github.takahirom.roborazzi.provideRoborazziContext
import com.yandex.div.test.INTERACTIVE_SCREENSHOT_CASES_PATH
import com.yandex.div.test.ScreenshotTests
import com.yandex.div.test.composeIgnoredInteractiveScreenshotCases
import com.yandex.div.test.crossplatform.InteractiveScreenshotTestData.Step
import com.yandex.div.test.crossplatform.ParsingResult
import com.yandex.div.test.crossplatform.ParsingUtils
import com.yandex.div.test.crossplatform.clipSwipeToBounds
import org.junit.Rule
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds

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
@Category(ScreenshotTests::class)
@Config(qualifiers = "w360dp-h728dp-xxhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(ParameterizedRobolectricTestRunner::class)
class RoborazziInteractiveScreenshotTest(
    parsingResult: ParsingResult<InteractiveScreenshotTestConfiguration>
) {
    private val configuration = parsingResult.getOrThrow()
    private val expectedErrorCount = configuration.runtimeErrorCount

    @get:Rule
    val contentRule = ComposeContentRoborazziRule(failOnError = expectedErrorCount == null)

    private val composeRule get() = contentRule.composeRule

    @Test
    fun test() {
        val data = contentRule.setContent(configuration.baseConfiguration)

        val recordedSnapshotNames = mutableSetOf<String>()
        configuration.steps.forEachIndexed { index, step ->
            contentRule.waitForIdle()

            when (step) {
                is Step.Action ->
                    contentRule.divContext.debugFeatures.performAction(data, step.action)

                is Step.Tap -> performTap(step.id)
                is Step.DoubleTap -> onNodeWithTag(step.id).performTouchInput { doubleClick() }
                is Step.LongTap -> onNodeWithTag(step.id).performTouchInput { longClick() }
                is Step.Swipe -> performSwipe(step)
                is Step.VerifyText -> verifyText(step)
                is Step.VerifyFocus -> verifyFocus(step)
                is Step.VerifySnapshot -> verifySnapshot(step, index, recordedSnapshotNames)
                is Step.Wait -> wait(step)
            }
        }

        contentRule.waitForIdle()
        expectedErrorCount?.let { verifyExpectedErrors(it) }
    }

    private fun verifyExpectedErrors(count: Int) {
        val errors = contentRule.reporter.errors
        assertEquals(count, errors.size, "Expected $count error(s), got: $errors")
    }

    private fun onNodeWithTag(tag: String) = composeRule.onNodeWithTag(tag)

    private fun performTap(id: String) {
        onNodeWithTag(id).performClick()
        composeRule.mainClock.advanceTimeBy(contentRule.viewConfiguration.doubleTapTimeoutMillis)
    }

    private fun performSwipe(step: Step.Swipe) {
        val target = onNodeWithTag(step.id)
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

    private fun verifyText(step: Step.VerifyText) {
        onNodeWithTag(step.id).assertTextEquals(step.text)
    }

    private fun verifyFocus(step: Step.VerifyFocus) {
        val isFocusMatcher = isFocused() or hasAnyDescendant(isFocused())
        onNodeWithTag(step.id)
            .assert(if (step.focused) isFocusMatcher else !isFocusMatcher)
    }

    @OptIn(ExperimentalRoborazziApi::class)
    private fun verifySnapshot(
        step: Step.VerifySnapshot,
        index: Int,
        recordedSnapshotNames: MutableSet<String>,
    ) {
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

        contentRule.captureScreenshot(
            fileName = "interactive/${configuration.baseConfiguration.name}/${step.name}",
            id = step.id,
            roborazziOptions = options.copy(taskType = taskType, compareOptions = compareOptions),
        )
    }

    private fun wait(step: Step.Wait) {
        contentRule.testScope.testScheduler.run {
            advanceTimeBy(step.delay.milliseconds)
            runCurrent()
        }
        composeRule.mainClock.advanceTimeBy(milliseconds = step.delay)
    }

    companion object {

        // Cache cases because ParameterizedRobolectricTestRunner calls the provider repeatedly.
        private val cases =
            ParsingUtils.parseFiles(INTERACTIVE_SCREENSHOT_CASES_PATH) { file, json ->
                val fileName = file.getRelativeFileName(INTERACTIVE_SCREENSHOT_CASES_PATH)
                if (fileName in composeIgnoredInteractiveScreenshotCases) {
                    return@parseFiles emptyList()
                }
                val configuration = InteractiveScreenshotTestConfiguration(
                    name = fileName.removeSuffix(".json"),
                    json = json,
                )
                listOf(ParsingResult.Success(configuration))
            }

        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        @Suppress("unused")
        fun cases() = cases
    }
}
