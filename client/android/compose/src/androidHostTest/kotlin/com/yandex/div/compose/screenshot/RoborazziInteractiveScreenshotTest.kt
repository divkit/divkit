package com.yandex.div.compose.screenshot

import android.graphics.PointF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.TouchInjectionScope
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.click
import androidx.compose.ui.test.doubleClick
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isFocused
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextInputSelection
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziTaskType
import com.github.takahirom.roborazzi.provideRoborazziContext
import com.yandex.div.test.INTERACTIVE_SCREENSHOT_CASES_PATH
import com.yandex.div.test.ScreenshotTests
import com.yandex.div.test.composeIgnoredInteractiveScreenshotCases
import com.yandex.div.test.crossplatform.InteractiveScreenshotTestData.Step
import com.yandex.div.test.crossplatform.InteractiveScreenshotTestData.Target
import com.yandex.div.test.crossplatform.ParsingResult
import com.yandex.div.test.crossplatform.ParsingUtils
import com.yandex.div.test.crossplatform.SightActionRecorder
import com.yandex.div.test.crossplatform.clipSwipeToBounds
import com.yandex.div.test.crossplatform.tapPointWithinBounds
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
 * ./gradlew :compose:verifyRoborazziAndroidHostTest -Proborazzi-tests --tests "*.RoborazziInteractiveScreenshotTest"
 * ```
 *
 * Record golden screenshots:
 * ```
 * ./gradlew :compose:verifyAndRecordRoborazziAndroidHostTest -Proborazzi-tests --tests "*.RoborazziInteractiveScreenshotTest"
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

                is Step.Tap -> performTap(step.target, step.point)
                is Step.DoubleTap -> performGesture(step.target, step.point) { doubleClick(it) }
                is Step.LongTap -> performGesture(step.target, step.point) { longClick(it) }
                is Step.Swipe -> performSwipe(step)
                is Step.EditText -> performEditText(step)
                is Step.VerifyText -> verifyText(step)
                is Step.VerifyFocus -> verifyFocus(step)
                is Step.VerifyLog -> verifyLog(step)
                is Step.VerifySnapshot -> verifySnapshot(step, index, recordedSnapshotNames)
                is Step.Wait -> wait(step)
                is Step.ScrollTo -> performScrollTo(step)
            }
        }

        contentRule.waitForIdle()
        expectedErrorCount?.let { verifyExpectedErrors(it) }
    }

    private fun verifyExpectedErrors(count: Int) {
        val errors = contentRule.reporter.errors
        assertEquals(count, errors.size, "Expected $count error(s), got: $errors")
    }

    // A clickable parent merges children without own semantics, so gestures use the unmerged tree.
    private fun onTarget(target: Target, unmerged: Boolean = false) = when (target) {
        is Target.DivId -> composeRule.onNode(hasTestTag(target.id) and isPlaced(), unmerged)
        is Target.Text -> composeRule.onNode(hasText(target.text) and isPlaced(), useUnmergedTree = true)
    }

    // The id of an input is on its wrapper, the text and the editing actions are on its child.
    private fun onInput(target: Target): SemanticsNodeInteraction {
        val matcher = target.matcher()
        return composeRule.onNode(
            hasSetTextAction() and (matcher or hasWrapper(matcher)) and isPlaced(),
            useUnmergedTree = true,
        )
    }

    private fun performTap(target: Target, point: PointF?) {
        performGesture(target, point) { click(it) }
        composeRule.mainClock.advanceTimeBy(contentRule.viewConfiguration.doubleTapTimeoutMillis)
    }

    private fun performGesture(
        target: Target,
        point: PointF?,
        gesture: TouchInjectionScope.(Offset) -> Unit,
    ) {
        val interaction = onTarget(target, unmerged = true)
        val position = point?.let { tapPosition(interaction.fetchSemanticsNode(), it) }
        interaction.performTouchInput { gesture(position ?: center) }
    }

    private fun tapPosition(node: SemanticsNode, point: PointF): Offset {
        val visibleBounds = node.boundsInWindow.translate(-node.positionInWindow)
        val position = tapPointWithinBounds(
            point,
            node.size.width.toFloat(),
            node.size.height.toFloat(),
            visibleBounds.toAndroidRectF()
        )
        // Touch input coordinates start at the clipped bounds, not at the node position.
        return node.positionInRoot - node.boundsInRoot.topLeft + Offset(position.x, position.y)
    }

    private fun performEditText(step: Step.EditText) {
        // Typing may change the text of the target and recreate the field, but not its wrapper.
        val wrapperId = onInput(step.target).fetchSemanticsNode().layoutInfo.parentInfo?.semanticsId
        val input = composeRule.onNode(
            hasSetTextAction() and SemanticsMatcher("input found by ${step.target}") {
                it.layoutInfo.parentInfo?.semanticsId == wrapperId
            },
            useUnmergedTree = true,
        )
        input.performClick()
        composeRule.mainClock.advanceTimeBy(contentRule.viewConfiguration.doubleTapTimeoutMillis)
        input.assert(isFocused()) { "Failed to focus ${step.target} by a tap" }
        val length = input.fetchSemanticsNode().config[SemanticsProperties.InputText].length
        val start = if (step.mode == Step.EditText.Mode.REPLACE) 0 else length
        input.performTextInputSelection(TextRange(start, length), relativeToOriginalText = true)
        step.inputs.forEachIndexed { index, text ->
            input.assert(isFocused()) {
                "Failed to keep focus in ${step.target} " +
                    "before character ${index + 1} of '${step.text}'"
            }
            input.performTextInput(text)
            contentRule.waitForIdle()
        }
    }

    private fun performSwipe(step: Step.Swipe) {
        val target = onTarget(step.target, unmerged = true)
        val node = target.fetchSemanticsNode()
        val size = node.size
        val from = PointF(size.width * step.from.x, size.height * step.from.y)
        val to = PointF(size.width * step.to.x, size.height * step.to.y)
        val visibleBounds = node.boundsInWindow.translate(-node.positionInWindow)
        clipSwipeToBounds(from, to, visibleBounds.toAndroidRectF())
        val offset = node.positionInRoot - node.boundsInRoot.topLeft
        target.performTouchInput {
            val start = offset + Offset(from.x, from.y)
            val end = offset + Offset(to.x, to.y)
            if (step.holdDurationMs > 0) {
                swipeAndHold(start, end, step.holdDurationMs)
            } else {
                swipe(start, end)
            }
        }
    }

    private fun verifyText(step: Step.VerifyText) {
        val target = onTarget(step.target)
        val input = onInput(step.target)
        // A missing or ambiguous target fails in assertTextEquals below with its usual message.
        if (runCatching { target.fetchSemanticsNode(); input.fetchSemanticsNode() }.isSuccess) {
            // The field also merges the hint its decorator shows while the text is empty.
            input.assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.EditableText,
                    AnnotatedString(step.text),
                )
            )
        } else {
            target.assertTextEquals(step.text)
        }
    }

    private fun verifyFocus(step: Step.VerifyFocus) {
        val isFocusMatcher = isFocused() or hasAnyDescendant(isFocused())
        onTarget(step.target)
            .assert(if (step.focused) isFocusMatcher else !isFocusMatcher)
    }

    // Delayed actions run only when the test clock advances.
    private fun verifyLog(step: Step.VerifyLog) {
        val recorder = contentRule.sightActionRecorder
        var waited = 0L
        while (!recorder.isSettled(step) && waited < SightActionRecorder.TIMEOUT_MS) {
            wait(Step.Wait(SightActionRecorder.POLL_INTERVAL_MS))
            contentRule.waitForIdle()
            waited += SightActionRecorder.POLL_INTERVAL_MS
        }
        recorder.verify(step)
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
            target = when (val target = step.target) {
                is Target.DivId -> composeRule
                    .onNode(hasTestTag(target.id) and isPlaced(), useUnmergedTree = true)
                    .assertExists("Failed to find screenshot target: ${target.id}")
                is Target.Text -> onTarget(target)
                null -> null
            },
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

    private fun performScrollTo(step: Step.ScrollTo) {
        for (swipes in 0..Step.ScrollTo.MAX_SWIPES) {
            if (swipes > 0) contentRule.waitForIdle()
            if (isScrollDestinationVisible(step)) return
            if (swipes < Step.ScrollTo.MAX_SWIPES) swipeToScroll(step)
        }
        throw AssertionError(
            "Destination did not become visible after ${Step.ScrollTo.MAX_SWIPES} swipes: " +
                "${step.destination}"
        )
    }

    private fun isScrollDestinationVisible(step: Step.ScrollTo): Boolean {
        val container = onTarget(step.container, unmerged = true).fetchSemanticsNode()
        val destinations = composeRule.onAllNodes(
            step.destination.matcher() and isPlaced() and
                hasAnyAncestor(SemanticsMatcher("is ${step.container}") { it.id == container.id }),
            useUnmergedTree = true,
        ).fetchSemanticsNodes()
        if (destinations.size > 1) {
            throw AssertionError(
                "Expected one destination, found ${destinations.size}: ${step.destination}"
            )
        }
        val destination = destinations.singleOrNull() ?: return false
        val center = destination.positionInWindow +
            Offset(destination.size.width / 2f, destination.size.height / 2f)
        return container.boundsInWindow.contains(center)
    }

    private fun swipeToScroll(step: Step.ScrollTo) {
        val target = onTarget(step.container, unmerged = true)
        val node = target.fetchSemanticsNode()
        val from = PointF(node.size.width * step.from.x, node.size.height * step.from.y)
        val to = PointF(node.size.width * step.to.x, node.size.height * step.to.y)
        val visibleBounds = node.boundsInWindow.translate(-node.positionInWindow)
        clipSwipeToBounds(from, to, visibleBounds.toAndroidRectF())
        // Touch input coordinates start at the clipped bounds, not at the node position.
        val offset = node.positionInRoot - node.boundsInRoot.topLeft
        target.performTouchInput {
            swipe(
                offset + Offset(from.x, from.y),
                offset + Offset(to.x, to.y),
                Step.ScrollTo.SWIPE_DURATION_MS,
            )
        }
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

private fun TouchInjectionScope.swipeAndHold(start: Offset, end: Offset, holdDurationMs: Long) {
    val moveSteps = (SWIPE_DURATION_MS / eventPeriodMillis).toInt().coerceAtLeast(1)
    down(start)
    for (index in 1..moveSteps) {
        moveTo(lerp(start, end, index.toFloat() / moveSteps), SWIPE_DURATION_MS / moveSteps)
    }
    var held = 0L
    while (held < holdDurationMs) {
        moveTo(end, eventPeriodMillis)
        held += eventPeriodMillis
    }
    up()
}

private const val SWIPE_DURATION_MS = 200L

private fun Target.matcher() = when (this) {
    is Target.DivId -> hasTestTag(id)
    is Target.Text -> hasText(text)
}

// Div-tabs measures pages in compositions that are never placed, so their nodes duplicate visible ones.
private fun isPlaced() = SemanticsMatcher("is placed") { it.layoutInfo.isPlaced }

// The wrapper is the layout parent: without an id it may lack semantics and not be the parent.
private fun hasWrapper(matcher: SemanticsMatcher) =
    SemanticsMatcher("hasWrapperThat(${matcher.description})") { node ->
        node.parent?.takeIf { it.id == node.layoutInfo.parentInfo?.semanticsId }
            ?.let(matcher::matches) == true
    }
