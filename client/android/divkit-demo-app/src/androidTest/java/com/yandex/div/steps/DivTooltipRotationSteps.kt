package com.yandex.div.steps

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import com.yandex.div.core.view2.Div2View
import com.yandex.divkit.demo.DummyActivity
import com.yandex.test.idling.SimpleIdlingResource
import com.yandex.test.idling.waitForIdlingResource
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import kotlin.math.abs

private const val ROTATION_TEST_ASSET = "ui_test_data/tooltips/tooltip_rotation.json"
private const val SHOW_ROTATION_TOOLTIP_TEXT = "Show rotation tooltip"
private const val ROTATION_ANCHOR_ID = "rotation_anchor"
private const val ROTATION_SOURCE_OMNIBOX_ID = "rotation_source_omnibox"
private const val ROTATION_POPUP_ROOT_ID = "rotation_popup_root"
private const val ROTATION_TOOLTIP_BUBBLE_ID = "rotation_tooltip_bubble"
private const val LAYOUT_POLLING_INTERVAL_MILLIS = 16L
private const val ROTATION_LAYOUT_LOG_TAG = "TooltipRotationTest"

internal fun tooltipRotation(f: DivTooltipRotationSteps.() -> Unit) = f(DivTooltipRotationSteps())

@StepsDsl
internal class DivTooltipRotationSteps : DivTestAssetSteps() {

    private var rotationState: RotationTestState? = null

    fun ActivityTestRule<DummyActivity>.buildRotationContainer() {
        testAsset = ROTATION_TEST_ASSET
        // DummyActivity handles configuration changes; keep the host hierarchy across rotations.
        buildContainer(MATCH_PARENT, MATCH_PARENT)
        runOnMainSync {
            // Align the injected container with the visible window used for popup geometry.
            val parent = container.parent as View
            val decorView = activity.window.decorView
            ViewCompat.setOnApplyWindowInsetsListener(container) { _, insets ->
                container.fitToWindowFrame(parent, decorView)
                insets
            }
            // Layout can precede insets dispatch. Updating only changed bounds prevents a relayout loop.
            parent.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                container.fitToWindowFrame(parent, decorView)
            }
            container.fitToWindowFrame(parent, decorView)
            ViewCompat.requestApplyInsets(container)
        }
        Espresso.onIdle()
        rotationState = RotationTestState(
            activityRule = this,
            activity = activity,
            divView = div2View,
        )
    }

    fun ActivityTestRule<DummyActivity>.rotateTo(orientation: Int): Unit =
        step("Rotate activity to ${orientation.name}") {
            runOnMainSync {
                activity.requestedOrientation = orientation.requestedOrientation
            }
            waitForIdlingResource(OrientationIdlingResource(this, orientation))
        }

    fun showRotationTooltip(): Unit = step("Show rotation tooltip") {
        onView(withText(SHOW_ROTATION_TOOLTIP_TEXT)).perform(click())
    }

    fun assertRotation(f: DivTooltipRotationSteps.() -> Unit) = f(this)

    fun hostIsNotRecreated(): Unit = step("Check activity and Div2View are not recreated") {
        val state = requireRotationState()
        val currentActivity = state.activityRule.activity
        assertSame("Activity was recreated after rotation", state.activity, currentActivity)
        assertSame(
            "Div2View was recreated after rotation",
            state.divView,
            currentActivityDiv2View(currentActivity),
        )
    }

    fun orientationIs(orientation: Int): Unit =
        step("Check orientation is ${orientation.name}") {
            val divView = requireRotationState().divView
            assertTrue(
                "Expected ${orientation.name} orientation, " +
                    "actual=${divView.resources.configuration.orientation.name}, " +
                    "bounds=${divView.boundsOnScreen}",
                divView.resources.configuration.orientation == orientation &&
                    divView.hasExpectedOrientation(orientation),
            )
        }

    fun tooltipIsShownAtBottomCenter(orientation: Int): Unit =
        step("Check tooltip is shown at bottom center in ${orientation.name}") {
            val views = rotationTooltipViews()
            waitForIdlingResource(TooltipAtBottomCenterIdlingResource(views, orientation))

            onView(withTagValue(equalTo(ROTATION_TOOLTIP_BUBBLE_ID)))
                .inRoot(isPlatformPopup())
                .check(matches(isCompletelyDisplayed()))

            val state = views.placementState(orientation)
            assertTrue(
                "Tooltip is not at bottom center in ${orientation.name}: $state",
                state.isValid,
            )
        }

    private fun rotationTooltipViews(): RotationTooltipViews {
        val divView = requireRotationState().divView
        return RotationTooltipViews(
            div2View = divView,
            anchor = activityView(ROTATION_ANCHOR_ID, divView),
            sourceOmnibox = activityView(ROTATION_SOURCE_OMNIBOX_ID, divView),
            popupRoot = popupView(ROTATION_POPUP_ROOT_ID).rootView,
            overlayOmnibox = popupView(ROTATION_SOURCE_OMNIBOX_ID),
            bubble = popupView(ROTATION_TOOLTIP_BUBBLE_ID),
        )
    }

    private fun requireRotationState(): RotationTestState {
        return requireNotNull(rotationState) {
            "buildRotationContainer must be called before rotation assertions"
        }
    }
}

private data class RotationTestState(
    val activityRule: ActivityTestRule<DummyActivity>,
    val activity: DummyActivity,
    val divView: Div2View,
)

private class OrientationIdlingResource(
    private val activityRule: ActivityTestRule<DummyActivity>,
    private val expectedOrientation: Int,
) : SimpleIdlingResource(
    pollingIntervalMillis = LAYOUT_POLLING_INTERVAL_MILLIS,
    description = "Wait for ${expectedOrientation.name} layout",
) {

    override fun checkIdle(): Boolean {
        val activity = activityRule.activity
        val decorView = activity.window.decorView
        return activity.resources.configuration.orientation == expectedOrientation &&
            decorView.hasExpectedOrientation(expectedOrientation) &&
            !decorView.isLayoutRequested
    }
}

private fun currentActivityDiv2View(activity: DummyActivity): Div2View {
    return onView(isAssignableFrom(Div2View::class.java))
        .inRoot(withDecorView(`is`(activity.window.decorView)))
        .stealView() as Div2View
}

private class TooltipAtBottomCenterIdlingResource(
    private val views: RotationTooltipViews,
    private val expectedOrientation: Int,
) : SimpleIdlingResource(
    pollingIntervalMillis = LAYOUT_POLLING_INTERVAL_MILLIS,
    description = "Wait for tooltip at bottom center in ${expectedOrientation.name}",
) {

    private var lastState: TooltipPlacementState? = null

    override fun checkIdle(): Boolean {
        val state = views.placementState(expectedOrientation)
        if (state != lastState) {
            Log.i(ROTATION_LAYOUT_LOG_TAG, state.toString())
            lastState = state
        }
        return state.isValid
    }
}

private class RotationTooltipViews(
    private val div2View: Div2View,
    private val anchor: View,
    private val sourceOmnibox: View,
    private val popupRoot: View,
    private val overlayOmnibox: View,
    private val bubble: View,
) {

    private val allViews = listOf(
        div2View,
        anchor,
        sourceOmnibox,
        popupRoot,
        overlayOmnibox,
        bubble,
    )

    fun geometry() = RotationTooltipGeometry(
        div2View = div2View.boundsOnScreen,
        anchor = anchor.boundsOnScreen,
        sourceOmnibox = sourceOmnibox.boundsOnScreen,
        popupRoot = popupRoot.boundsOnScreen,
        overlayOmnibox = overlayOmnibox.boundsOnScreen,
        bubble = bubble.boundsOnScreen,
    )

    fun placementState(orientation: Int): TooltipPlacementState {
        val geometry = geometry()
        return TooltipPlacementState(
            geometry = geometry,
            allViewsShown = allViews.all(View::isShown),
            allLayoutsSettled = allViews.none(View::isLayoutRequested),
            expectedOrientation = geometry.hasExpectedOrientation(orientation),
            popupCoversDiv2View = geometry.popupRoot.hasSameSizeAs(geometry.div2View),
            overlayMatchesSource = geometry.overlayOmnibox == geometry.sourceOmnibox,
            bubbleTouchesAnchor = abs(geometry.bubble.bottom - geometry.anchor.top) <= 1,
            bubbleIsCenteredOnScreen = geometry.bubble.isCenteredHorizontallyIn(geometry.div2View),
            bubbleIsInBottomThird = geometry.bubble.isInBottomThirdOf(geometry.div2View),
        )
    }
}

private data class TooltipPlacementState(
    val geometry: RotationTooltipGeometry,
    val allViewsShown: Boolean,
    val allLayoutsSettled: Boolean,
    val expectedOrientation: Boolean,
    val popupCoversDiv2View: Boolean,
    val overlayMatchesSource: Boolean,
    val bubbleTouchesAnchor: Boolean,
    val bubbleIsCenteredOnScreen: Boolean,
    val bubbleIsInBottomThird: Boolean,
) {

    val isValid: Boolean
        get() = allViewsShown &&
            allLayoutsSettled &&
            expectedOrientation &&
            popupCoversDiv2View &&
            overlayMatchesSource &&
            bubbleTouchesAnchor &&
            bubbleIsCenteredOnScreen &&
            bubbleIsInBottomThird
}

private data class RotationTooltipGeometry(
    val div2View: Rect,
    val anchor: Rect,
    val sourceOmnibox: Rect,
    val popupRoot: Rect,
    val overlayOmnibox: Rect,
    val bubble: Rect,
) {

    fun hasExpectedOrientation(orientation: Int): Boolean {
        return listOf(div2View, anchor, sourceOmnibox, popupRoot, overlayOmnibox, bubble).none(Rect::isEmpty) &&
            div2View.hasExpectedOrientation(orientation)
    }
}

private val View.boundsOnScreen: Rect
    get() {
        val location = IntArray(2)
        getLocationOnScreen(location)
        return Rect(location[0], location[1], location[0] + width, location[1] + height)
    }

private fun View.fitToWindowFrame(parent: View, decorView: View) {
    val windowFrame = Rect()
    decorView.getWindowVisibleDisplayFrame(windowFrame)
    val parentLocation = IntArray(2)
    parent.getLocationOnScreen(parentLocation)
    val leftMargin = windowFrame.left - parentLocation[0] - parent.paddingLeft
    val topMargin = windowFrame.top - parentLocation[1] - parent.paddingTop
    val layoutParams = layoutParams as FrameLayout.LayoutParams
    if (layoutParams.width == windowFrame.width() &&
        layoutParams.height == windowFrame.height() &&
        layoutParams.leftMargin == leftMargin &&
        layoutParams.topMargin == topMargin
    ) {
        return
    }
    layoutParams.width = windowFrame.width()
    layoutParams.height = windowFrame.height()
    layoutParams.setMargins(
        leftMargin,
        topMargin,
        0,
        0,
    )
    this.layoutParams = layoutParams
    parent.requestLayout()
}

private fun Rect.hasSameSizeAs(other: Rect): Boolean {
    return width() == other.width() && height() == other.height()
}

private fun Rect.isCenteredHorizontallyIn(other: Rect): Boolean {
    val leftGap = left - other.left
    val rightGap = other.right - right
    return abs(leftGap - rightGap) <= 1
}

private fun Rect.isInBottomThirdOf(other: Rect): Boolean {
    return top >= other.top + other.height() * 2 / 3 && bottom <= other.bottom
}

private fun Rect.hasExpectedOrientation(orientation: Int): Boolean {
    return when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> width() >= height()
        Configuration.ORIENTATION_PORTRAIT -> height() >= width()
        else -> false
    }
}

private fun View.hasExpectedOrientation(orientation: Int): Boolean {
    return width > 0 && height > 0 && when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> width >= height
        Configuration.ORIENTATION_PORTRAIT -> height >= width
        else -> false
    }
}

private val Int.requestedOrientation: Int
    get() = when (this) {
        Configuration.ORIENTATION_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        Configuration.ORIENTATION_PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        else -> error("Unsupported orientation: $this")
    }

private val Int.name: String
    get() = when (this) {
        Configuration.ORIENTATION_LANDSCAPE -> "landscape"
        Configuration.ORIENTATION_PORTRAIT -> "portrait"
        else -> "unknown ($this)"
    }

private fun activityView(tag: String, div2View: Div2View): View {
    return onView(withTagValue(equalTo(tag)))
        .inRoot(withDecorView(`is`(div2View.rootView)))
        .stealView()
}

private fun popupView(tag: String): View {
    return onView(withTagValue(equalTo(tag)))
        .inRoot(isPlatformPopup())
        .stealView()
}

private fun ViewInteraction.stealView(): View {
    var result: View? = null
    check { view, noViewFoundException ->
        noViewFoundException?.let { throw it }
        result = view
    }
    return requireNotNull(result)
}
