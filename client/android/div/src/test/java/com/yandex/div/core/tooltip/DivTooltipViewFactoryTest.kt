package com.yandex.div.core.tooltip

import android.app.Activity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.OnBackPressedDispatcherOwner
import com.yandex.div.core.asExpression
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.expression.local.RuntimeStore
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.AccessibilityStateProvider
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivVisibilityActionTracker
import com.yandex.div.core.view2.animations.DivAnimationsEnabledController
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivText
import com.yandex.div2.DivTooltip
import com.yandex.div2.DivTooltipMode
import com.yandex.div2.DivTooltipModeModal
import com.yandex.div2.DivTooltipModeNonModal
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class DivTooltipViewFactoryTest {

    private val activityController = Robolectric.buildActivity(Activity::class.java).setup()
    private val activity = activityController.get()
    private val anchor = View(activity)
    private val rootView = FrameLayout(activity).apply {
        addView(anchor)
    }
    private val tooltipView = View(activity).apply {
        layoutParams = ViewGroup.LayoutParams(100, 50)
    }
    private val container = DivTooltipContainer(activity).apply {
        setViews(substrate = null, bringToTop = null, tooltip = this@DivTooltipViewFactoryTest.tooltipView)
    }
    private val builder = mock<DivTooltipViewBuilder> {
        on { buildTooltipView(any(), anyOrNull()) } doReturn container
    }
    private val backDispatcher = OnBackPressedDispatcher()
    private val dispatcherOwner = mock<OnBackPressedDispatcherOwner> {
        on { onBackPressedDispatcher } doReturn backDispatcher
    }
    private val runtimeStore = mock<RuntimeStore> {
        on { getOrCreateRuntime(any(), any(), any()) } doReturn ExpressionsRuntime(mock())
    }
    private val divView = mock<Div2View> {
        on { getContext() } doReturn activity
        on { resources } doReturn activity.resources
        on { runtimeStore } doReturn runtimeStore
        on { getTag(androidx.activity.R.id.view_tree_on_back_pressed_dispatcher_owner) }
            .doReturn(dispatcherOwner)
    }
    private val accessibility = mock<AccessibilityStateProvider> {
        on { isAccessibilityEnabled(any()) } doReturn false
    }
    private val visibilityTracker = mock<DivVisibilityActionTracker>()
    private val animations = mock<DivAnimationsEnabledController> {
        on { isEnabled() } doReturn false
    }
    private val onTouchOutside = mock<() -> Unit>()
    private val factory = DivTooltipViewFactory(builder, accessibility, visibilityTracker, animations)
    private val div = Div.Text(DivText(text = "tooltip".asExpression()))
    private var shownTooltip: DivTooltipView? = null

    init {
        activity.setContentView(rootView)
        activity.window.decorView.measure(
            View.MeasureSpec.makeMeasureSpec(500, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1_000, View.MeasureSpec.EXACTLY),
        )
        activity.window.decorView.layout(0, 0, 500, 1_000)
        anchor.layout(100, 200, 200, 250)
    }

    @After
    fun tearDown() {
        shownTooltip?.dismissImmediately()
        activityController.pause().stop().destroy()
    }

    @Test
    fun `modal popup receives focus and blocks touches behind it`() {
        val popup = showTooltip(mode = DivTooltipMode.Modal(DivTooltipModeModal()))

        assertTrue(popup.isFocusable)
        assertTrue(popup.isTouchModal)
        assertTrue(popup.isTouchable)
    }

    @Test
    fun `non modal popup leaves underlying window focus and touches available`() {
        val popup = showTooltip(mode = DivTooltipMode.NonModal(DivTooltipModeNonModal()))

        assertFalse(popup.isFocusable)
        assertFalse(popup.isTouchModal)
        assertTrue(popup.isTouchable)
    }

    @Test
    fun `close by outside tap enables outside touch delivery`() {
        val popup = showTooltip(closeByTapOutside = true)

        assertTrue(popup.isOutsideTouchable)
    }

    @Test
    fun `disabled close by outside tap disables outside touch delivery`() {
        val popup = showTooltip(closeByTapOutside = false)

        assertFalse(popup.isOutsideTouchable)
    }

    @Test
    fun `outside touch interceptor invokes dismissal callback`() {
        val popup = showTooltip(closeByTapOutside = true)
        val popupRoot = popup.contentView.parent as View
        val event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, -10f, -10f, 0)

        popupRoot.dispatchTouchEvent(event)
        event.recycle()

        verify(onTouchOutside).invoke()
    }

    @Test
    fun `non modal dismiss action forwards down to underlying window`() {
        // Arrange
        val downEvents = mutableListOf<Int>()
        rootView.setOnTouchListener { _, event ->
            downEvents.add(event.action)
            true
        }
        showTooltip(mode = DivTooltipMode.NonModal(DivTooltipModeNonModal()))
        val event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 50f, 100f, 0)

        // Act
        container.dismissAction(event)
        event.recycle()

        // Assert
        assertEquals(listOf(MotionEvent.ACTION_DOWN), downEvents)
    }

    @Test
    fun `modal dismiss action leaves underlying window untouched`() {
        // Arrange
        val downEvents = mutableListOf<Int>()
        rootView.setOnTouchListener { _, event ->
            downEvents.add(event.action)
            true
        }
        showTooltip(mode = DivTooltipMode.Modal(DivTooltipModeModal()))
        val event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 50f, 100f, 0)

        // Act
        container.dismissAction(event)
        event.recycle()

        // Assert
        assertEquals(emptyList<Int>(), downEvents)
    }

    @Test
    fun `substrate popup fills decor without clipping`() {
        val popup = showTooltip(substrateDiv = div)

        assertTrue(popup.isAttachedInDecor)
        assertFalse(popup.isClippingEnabled)
        assertEquals(ViewGroup.LayoutParams.MATCH_PARENT, popup.width)
    }

    @Test
    fun `accessibility back callback invokes dismissal request`() {
        whenever(accessibility.isAccessibilityEnabled(any())).doReturn(true)
        showTooltip()

        backDispatcher.onBackPressed()

        verify(onTouchOutside).invoke()
    }

    @Test
    fun `accessibility disabled leaves back dispatcher without tooltip callback`() {
        showTooltip()

        assertFalse(backDispatcher.hasEnabledCallbacks())
    }

    @Test
    fun `popup dismiss releases accessibility back callback`() {
        whenever(accessibility.isAccessibilityEnabled(any())).doReturn(true)
        val popup = showTooltip()
        assertTrue(backDispatcher.hasEnabledCallbacks())

        popup.dismiss()

        assertFalse(backDispatcher.hasEnabledCallbacks())
    }

    @Test
    fun `dismiss before showing releases accessibility back callback`() {
        whenever(accessibility.isAccessibilityEnabled(any())).doReturn(true)
        val tooltip = factory.create(tooltipData(), onTouchOutside) {}
        assertTrue(backDispatcher.hasEnabledCallbacks())

        tooltip.dismissImmediately()

        assertFalse(backDispatcher.hasEnabledCallbacks())
    }

    private fun showTooltip(
        mode: DivTooltipMode = DivTooltipMode.NonModal(DivTooltipModeNonModal()),
        closeByTapOutside: Boolean = true,
        substrateDiv: Div? = null,
    ): PopupWindow {
        val tooltip = factory.create(
            data = tooltipData(mode, closeByTapOutside, substrateDiv),
            onTouchOutside = onTouchOutside,
            onDismissed = {},
        )
        shownTooltip = tooltip
        tooltip.show {}
        return shadowOf(RuntimeEnvironment.getApplication()).latestPopupWindow
    }

    private fun tooltipData(
        mode: DivTooltipMode = DivTooltipMode.NonModal(DivTooltipModeNonModal()),
        closeByTapOutside: Boolean = true,
        substrateDiv: Div? = null,
    ): TooltipData {
        return TooltipData(
            id = "tooltip",
            scopeId = null,
            divTooltip = DivTooltip(
                id = "tooltip",
                div = div,
                mode = mode,
                substrateDiv = substrateDiv,
                closeByTapOutside = closeByTapOutside.asExpression(),
                position = DivTooltip.Position.RIGHT.asExpression(),
            ),
            anchor = anchor,
            anchorBlock = DivBlock.Text(div, ExpressionResolver.EMPTY, DivStatePath.fromState(0)),
            divView = divView,
        )
    }
}
